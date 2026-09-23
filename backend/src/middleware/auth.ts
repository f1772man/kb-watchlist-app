import { Request, Response, NextFunction } from 'express';
import { auth } from '../services/firebase.js';

export interface AuthenticatedRequest extends Request {
  userId?: string;
}

export async function requireAuth(
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
): Promise<void> {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    res.status(401).json({
      error: 'Unauthorized: Missing or invalid Authorization header (Bearer token required)',
    });
    return;
  }

  const idToken = authHeader.split('Bearer ')[1].trim();
  if (!idToken) {
    res.status(401).json({ error: 'Unauthorized: Empty bearer token' });
    return;
  }

  try {
    const decodedToken = await auth.verifyIdToken(idToken);
    req.userId = decodedToken.uid;
    next();
  } catch (error: any) {
    console.warn('ID Token verification failed:', error.message);
    res.status(401).json({
      error: 'Unauthorized: Invalid or expired Firebase ID token',
      message: error.message,
    });
  }
}
