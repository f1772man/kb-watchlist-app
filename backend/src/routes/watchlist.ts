import { Router, Response } from 'express';
import { AuthenticatedRequest } from '../middleware/auth.js';
import { db } from '../services/firebase.js';
import { findStockByCode } from '../services/stockMaster.js';

export const watchlistRouter = Router();

export interface WatchlistItem {
  code: string;
  name: string;
  createdAt: string;
}

// GET /watchlist - Retrieve current user's watchlist
watchlistRouter.get('/', async (req: AuthenticatedRequest, res: Response): Promise<void> => {
  const userId = req.userId;
  if (!userId) {
    res.status(401).json({ error: 'User not authenticated' });
    return;
  }

  try {
    const snapshot = await db
      .collection(`users/${userId}/watchlist`)
      .orderBy('createdAt', 'desc')
      .get();

    const items: WatchlistItem[] = [];
    snapshot.forEach((doc) => {
      const data = doc.data();
      items.push({
        code: data.code || doc.id,
        name: data.name || '',
        createdAt: data.createdAt || new Date().toISOString(),
      });
    });

    res.json({ watchlist: items });
  } catch (error: any) {
    console.error(`Failed to get watchlist for ${userId}:`, error.message);
    res.status(500).json({ error: 'Failed to retrieve watchlist', message: error.message });
  }
});

// POST /watchlist - Add item to watchlist
watchlistRouter.post('/', async (req: AuthenticatedRequest, res: Response): Promise<void> => {
  const userId = req.userId;
  if (!userId) {
    res.status(401).json({ error: 'User not authenticated' });
    return;
  }

  const { code, name } = req.body;
  if (!code || typeof code !== 'string' || code.trim().length !== 6) {
    res.status(400).json({ error: 'Valid 6-digit stock code is required' });
    return;
  }

  const stockCode = code.trim();
  let stockName = (name || '').trim();

  // If name not provided, find in master
  if (!stockName) {
    const stockInfo = findStockByCode(stockCode);
    stockName = stockInfo ? stockInfo.name : stockCode;
  }

  try {
    const item: WatchlistItem = {
      code: stockCode,
      name: stockName,
      createdAt: new Date().toISOString(),
    };

    await db.doc(`users/${userId}/watchlist/${stockCode}`).set(item, { merge: true });
    res.status(201).json({ success: true, item });
  } catch (error: any) {
    console.error(`Failed to add watchlist item for ${userId}:`, error.message);
    res.status(500).json({ error: 'Failed to add item to watchlist', message: error.message });
  }
});

// DELETE /watchlist/:code - Remove item from watchlist
watchlistRouter.delete('/:code', async (req: AuthenticatedRequest, res: Response): Promise<void> => {
  const userId = req.userId;
  if (!userId) {
    res.status(401).json({ error: 'User not authenticated' });
    return;
  }

  const rawCode = req.params.code;
  const code = Array.isArray(rawCode) ? rawCode[0] : rawCode;
  if (!code) {
    res.status(400).json({ error: 'Stock code is required' });
    return;
  }

  const stockCode = code.trim();

  try {
    await db.doc(`users/${userId}/watchlist/${stockCode}`).delete();
    res.json({ success: true, code: stockCode });
  } catch (error: any) {
    console.error(`Failed to delete watchlist item ${stockCode} for ${userId}:`, error.message);
    res.status(500).json({ error: 'Failed to delete item from watchlist', message: error.message });
  }
});
