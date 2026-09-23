import axios from 'axios';
import { CONFIG } from '../config.js';
import { getKbAccessToken } from './kbTokenService.js';

export interface StockQuote {
  code: string;
  name: string;
  currentPrice: number;
  change: number;
  changeSign: '+' | '-' | '0';
  changeRate: number; // percentage, e.g., 2.35 or -1.50
  volume: number;
  openPrice: number;
  highPrice: number;
  lowPrice: number;
  updatedAt: string;
}

// Short memory cache (5 seconds) to respect KB rate limits and reduce redundant calls
const quoteCache = new Map<string, { quote: StockQuote; timestamp: number }>();
const QUOTE_CACHE_TTL_MS = 5000;

export async function fetchStockQuote(stockCode: string): Promise<StockQuote> {
  const code = stockCode.trim();
  const cached = quoteCache.get(code);
  if (cached && Date.now() - cached.timestamp < QUOTE_CACHE_TTL_MS) {
    return cached.quote;
  }

  const token = await getKbAccessToken();
  const url = `${CONFIG.kbBaseUrl}/api/v1/ivu10140`;

  const payload = {
    dataHeader: CONFIG.defaultHeader,
    dataBody: {
      excg_clsf: '0', // 0=통합
      shrt_cd: code,
    },
  };

  try {
    const response = await axios.post(url, payload, {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      timeout: 10000,
    });

    const resHeader = response.data?.dataHeader;
    const resBody = response.data?.dataBody;

    if (resHeader?.processFlag !== 'A') {
      const errorMsg = resHeader?.processMessage || resHeader?.resultMessage || 'Failed to fetch quote from KB';
      throw new Error(`KB Quote API Error [${resHeader?.processCode || resHeader?.resultCode}]: ${errorMsg}`);
    }

    if (!resBody) {
      throw new Error('KB Quote response missing dataBody');
    }

    // Parse values from KB API response
    const name = (resBody.is_nm || '').trim();
    const currentPrice = parseInt(resBody.now_prc || '0', 10);
    const rawChange = parseInt(resBody.bdy_cmpr || '0', 10);
    const ccd = String(resBody.bdy_cmpr_ccd || '3').trim();

    // Determine change sign based on bdy_cmpr_ccd
    // 1: 상한, 2: 상승, 3: 보합, 4: 하한, 5: 하락
    let changeSign: '+' | '-' | '0' = '0';
    let change = 0;

    if (ccd === '1' || ccd === '2') {
      changeSign = '+';
      change = Math.abs(rawChange);
    } else if (ccd === '4' || ccd === '5') {
      changeSign = '-';
      change = -Math.abs(rawChange);
    } else {
      changeSign = '0';
      change = 0;
    }

    // Parse rate (up_dwn_r_p2 can be e.g. "   1.25" or "  -2.50")
    let changeRate = parseFloat((resBody.up_dwn_r_p2 || '0').trim());
    if (isNaN(changeRate)) changeRate = 0;
    if (changeSign === '-' && changeRate > 0) {
      changeRate = -changeRate;
    }

    const volume = parseInt(resBody.acml_vlm || '0', 10);
    const openPrice = parseInt(resBody.opn_prc || '0', 10);
    const highPrice = parseInt(resBody.hgh_prc || '0', 10);
    const lowPrice = parseInt(resBody.lw_prc || '0', 10);

    const quote: StockQuote = {
      code,
      name,
      currentPrice,
      change,
      changeSign,
      changeRate,
      volume,
      openPrice,
      highPrice,
      lowPrice,
      updatedAt: new Date().toISOString(),
    };

    quoteCache.set(code, { quote, timestamp: Date.now() });
    return quote;
  } catch (error: any) {
    if (error.response) {
      const respData = error.response.data;
      const msg = respData?.dataHeader?.processMessage || respData?.dataHeader?.resultMessage || error.message;
      throw new Error(`KB Quote HTTP ${error.response.status}: ${msg}`);
    }
    throw error;
  }
}
