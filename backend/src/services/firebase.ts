import admin from 'firebase-admin';
import { CONFIG } from '../config.js';

if (!admin.apps.length) {
  admin.initializeApp({
    projectId: CONFIG.projectId,
  });
}

export const db = admin.firestore();
export const auth = admin.auth();
