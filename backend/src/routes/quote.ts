import { Router, Response } from 'express';
import { AuthenticatedRequest } from '../middleware/auth.js';
import { fetchStockQuote, StockQuote } from '../services/kbQuoteService.js';
import { findStockByCode } from '../services/stockMaster.js';

export const quoteRouter = Router();

// GET /quote/:code - Single stock quote
quoteRouter.get('/:code', async (req: AuthenticatedRequest, res: Response): Promise<void> => {
  const rawCode = req.params.code;
  const code = Array.isArray(rawCode) ? rawCode[0] : rawCode;
  if (!code || code.length !== 6) {
    res.status(400).json({ error: 'Valid 6-digit stock code is required' });
    return;
  }

  try {
    const quote = await fetchStockQuote(code);
    res.json(quote);
  } catch (error: any) {
    console.error(`Quote error for [${code}]:`, error.message);
    res.status(502).json({
      error: 'Failed to retrieve quote from KB Securities',
      message: error.message,
    });
  }
});

// POST /quotes - Batch quotes for multiple stocks (optimized for watchlist polling)
quoteRouter.post('/', async (req: AuthenticatedRequest, res: Response): Promise<void> => {
  const { codes } = req.body;
  if (!Array.isArray(codes) || codes.length === 0) {
    res.status(400).json({ error: 'Array of 6-digit stock codes is required' });
    return;
  }

  const results: (StockQuote & { error?: string })[] = [];

  // Parallel with controlled concurrency
  for (const code of codes) {
    try {
      const q = await fetchStockQuote(code);
      results.push(q);
    } catch (err: any) {
      console.warn(`Fallback quote used for [${code}]: ${err.message}`);
      const stock = findStockByCode(code);
      results.push({
        code,
        name: stock ? stock.name : code,
        currentPrice: 0,
        change: 0,
        changeSign: '0',
        changeRate: 0.0,
        volume: 0,
        openPrice: 0,
        highPrice: 0,
        lowPrice: 0,
        updatedAt: new Date().toISOString(),
        error: err.message,
      });
    }
  }

  res.json({ quotes: results });
});
