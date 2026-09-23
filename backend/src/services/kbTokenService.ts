import axios from 'axios';
import { CONFIG } from '../config.js';
import { getSecret } from './secretManager.js';
import { db } from './firebase.js';

interface TokenDoc {
  accessToken: string;
  tokenType: string;
  expiresAt: number; // Unix timestamp in milliseconds
  updatedAt: string;
}

const TOKEN_DOC_PATH = 'tokens/kb';
const EXPIRY_BUFFER_MS = 10 * 60 * 1000; // 10 minutes buffer

export async function getKbAccessToken(): Promise<string> {
  // 1. Check Firestore token cache
  try {
    const docRef = db.doc(TOKEN_DOC_PATH);
    const docSnap = await docRef.get();

    if (docSnap.exists) {
      const data = docSnap.data() as TokenDoc;
      const now = Date.now();
      if (data.accessToken && data.expiresAt && now < data.expiresAt - EXPIRY_BUFFER_MS) {
        return data.accessToken;
      }
    }
  } catch (err: any) {
    console.warn('Failed to read token from Firestore, will attempt new fetch:', err.message);
  }

  // 2. Need new token: Fetch appKey and appSecret
  const appKey = await getSecret(CONFIG.kbAppKeySecretName);
  const appSecret = await getSecret(CONFIG.kbAppSecretSecretName);

  const url = `${CONFIG.kbBaseUrl}/oauth2/token`;
  const requestPayload = {
    dataHeader: CONFIG.defaultHeader,
    dataBody: {
      appKey,
      appSecret,
      grantType: 'client_credentials',
    },
  };

  try {
    const response = await axios.post(url, requestPayload, {
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: 10000,
    });

    const resHeader = response.data?.dataHeader;
    const resBody = response.data?.dataBody;

    if (resHeader?.processFlag !== 'A') {
      const errorMsg = resHeader?.processMessage || resHeader?.resultMessage || 'Failed to issue KB token';
      throw new Error(`KB Token API Error [${resHeader?.processCode || resHeader?.resultCode}]: ${errorMsg}`);
    }

    const accessToken = resBody?.access_token;
    const expiresIn = parseInt(resBody?.expires_in || '86400', 10); // default 24h (86400s)

    if (!accessToken) {
      throw new Error('KB Token response missing access_token in dataBody');
    }

    const expiresAt = Date.now() + expiresIn * 1000;

    // 3. Save to Firestore cache
    try {
      await db.doc(TOKEN_DOC_PATH).set(
        {
          accessToken,
          tokenType: resBody?.token_type || 'Bearer',
          expiresAt,
          updatedAt: new Date().toISOString(),
        },
        { merge: true }
      );
    } catch (saveErr: any) {
      console.error('Failed to cache token to Firestore:', saveErr.message);
    }

    return accessToken;
  } catch (error: any) {
    if (error.response) {
      const respData = error.response.data;
      const msg = respData?.dataHeader?.processMessage || respData?.dataHeader?.resultMessage || error.message;
      throw new Error(`KB OAuth Token HTTP ${error.response.status}: ${msg}`);
    }
    throw error;
  }
}
