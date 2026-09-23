import dotenv from 'dotenv';
dotenv.config();

export const CONFIG = {
  port: parseInt(process.env.PORT || '8080', 10),
  projectId: process.env.GOOGLE_CLOUD_PROJECT || process.env.GCP_PROJECT || 'YOUR_PROJECT_ID',
  kbBaseUrl: process.env.KB_BASE_URL || 'https://developer.kbsec.com:32484',
  kbAppKeySecretName: process.env.KB_APP_KEY_SECRET_NAME || 'kb-app-key',
  kbAppSecretSecretName: process.env.KB_APP_SECRET_SECRET_NAME || 'kb-app-secret',
  defaultHeader: {
    ipAddr: '127.0.0.1',
    macAddr: '00:00:00:00:00:00',
  },
};
