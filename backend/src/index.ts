import express, { Request, Response, NextFunction } from 'express';
import cors from 'cors';
import { CONFIG } from './config.js';
import { requireAuth } from './middleware/auth.js';
import { quoteRouter } from './routes/quote.js';
import { watchlistRouter } from './routes/watchlist.js';
import { searchRouter } from './routes/search.js';

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Public health check endpoint
app.get('/health', (_req: Request, res: Response) => {
  res.json({
    status: 'ok',
    project: CONFIG.projectId,
    timestamp: new Date().toISOString(),
  });
});

// Authenticated API routes
app.use('/quote', requireAuth, quoteRouter);
app.use('/quotes', requireAuth, quoteRouter);
app.use('/watchlist', requireAuth, watchlistRouter);
app.use('/search', requireAuth, searchRouter);

// Global error handler
app.use((err: any, _req: Request, res: Response, _next: NextFunction) => {
  console.error('Unhandled server error:', err);
  res.status(500).json({
    error: 'Internal Server Error',
    message: err.message || 'Unknown error occurred',
  });
});

// Start server
app.listen(CONFIG.port, () => {
  console.log(`KB Watchlist Backend listening on port ${CONFIG.port}`);
  console.log(`GCP Project: ${CONFIG.projectId}`);
  console.log(`KB Base URL: ${CONFIG.kbBaseUrl}`);
});
