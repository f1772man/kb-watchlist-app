import { SecretManagerServiceClient } from '@google-cloud/secret-manager';
import { CONFIG } from '../config.js';

let client: SecretManagerServiceClient | null = null;

function getClient(): SecretManagerServiceClient {
  if (!client) {
    client = new SecretManagerServiceClient();
  }
  return client;
}

// Memory cache for secrets during container lifecycle
const secretCache = new Map<string, { value: string; fetchedAt: number }>();
const CACHE_TTL_MS = 1000 * 60 * 30; // 30 minutes cache

export async function getSecret(secretName: string): Promise<string> {
  // Check env first (useful for local dev testing)
  const envKey = secretName.toUpperCase().replace(/-/g, '_');
  if (process.env[envKey]) {
    return process.env[envKey]!;
  }

  const cached = secretCache.get(secretName);
  if (cached && Date.now() - cached.fetchedAt < CACHE_TTL_MS) {
    return cached.value;
  }

  try {
    const smClient = getClient();
    const name = `projects/${CONFIG.projectId}/secrets/${secretName}/versions/latest`;
    const [version] = await smClient.accessSecretVersion({ name });
    const payload = version.payload?.data?.toString();
    if (!payload) {
      throw new Error(`Secret ${secretName} payload is empty`);
    }

    secretCache.set(secretName, { value: payload.trim(), fetchedAt: Date.now() });
    return payload.trim();
  } catch (error: any) {
    console.error(`Failed to fetch secret [${secretName}]:`, error.message);
    throw new Error(`Could not access secret [${secretName}]: ${error.message}`);
  }
}
