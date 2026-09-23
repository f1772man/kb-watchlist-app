import { Router, Response } from 'express';
import { AuthenticatedRequest } from '../middleware/auth.js';
import { searchStocks } from '../services/stockMaster.js';

export const searchRouter = Router();

// GET /search?q=... - Search Korean stocks by name or code
searchRouter.get('/', (req: AuthenticatedRequest, res: Response): void => {
  const query = (req.query.q as string) || '';
  const results = searchStocks(query);
  res.json({ results });
});
