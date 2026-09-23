export interface StockItem {
  code: string;
  name: string;
  market: 'KOSPI' | 'KOSDAQ';
}

// Top Korean representative stocks across major sectors for search
export const STOCK_MASTER: StockItem[] = [
  { code: '005930', name: '삼성전자', market: 'KOSPI' },
  { code: '000660', name: 'SK하이닉스', market: 'KOSPI' },
  { code: '373220', name: 'LG에너지솔루션', market: 'KOSPI' },
  { code: '207940', name: '삼성바이오로직스', market: 'KOSPI' },
  { code: '005380', name: '현대차', market: 'KOSPI' },
  { code: '000270', name: '기아', market: 'KOSPI' },
  { code: '068270', name: '셀트리온', market: 'KOSPI' },
  { code: '005490', name: 'POSCO홀딩스', market: 'KOSPI' },
  { code: '105560', name: 'KB금융', market: 'KOSPI' },
  { code: '055550', name: '신한지주', market: 'KOSPI' },
  { code: '035420', name: 'NAVER', market: 'KOSPI' },
  { code: '035720', name: '카카오', market: 'KOSPI' },
  { code: '051910', name: 'LG화학', market: 'KOSPI' },
  { code: '006400', name: '삼성SDI', market: 'KOSPI' },
  { code: '012330', name: '현대모비스', market: 'KOSPI' },
  { code: '086790', name: '하나금융지주', market: 'KOSPI' },
  { code: '028260', name: '삼성물산', market: 'KOSPI' },
  { code: '323410', name: '카카오뱅크', market: 'KOSPI' },
  { code: '259960', name: '크래프톤', market: 'KOSPI' },
  { code: '003550', name: 'LG', market: 'KOSPI' },
  { code: '034730', name: 'SK', market: 'KOSPI' },
  { code: '015760', name: '한국전력', market: 'KOSPI' },
  { code: '032830', name: '삼성생명', market: 'KOSPI' },
  { code: '017670', name: 'SK텔레콤', market: 'KOSPI' },
  { code: '030200', name: 'KT', market: 'KOSPI' },
  { code: '033780', name: 'KT&G', market: 'KOSPI' },
  { code: '018260', name: '삼성에스디에스', market: 'KOSPI' },
  { code: '009150', name: '삼성전기', market: 'KOSPI' },
  { code: '034020', name: '두산에너빌리티', market: 'KOSPI' },
  { code: '010130', name: '고려아연', market: 'KOSPI' },
  { code: '011200', name: 'HMM', market: 'KOSPI' },
  { code: '247540', name: '에코프로비엠', market: 'KOSDAQ' },
  { code: '086520', name: '에코프로', market: 'KOSDAQ' },
  { code: '196170', name: '알테오젠', market: 'KOSDAQ' },
  { code: '028300', name: 'HLB', market: 'KOSDAQ' },
  { code: '277810', name: '레인보우로보틱스', market: 'KOSDAQ' },
  { code: '058470', name: '리노공업', market: 'KOSDAQ' },
  { code: '035900', name: 'JYP Ent.', market: 'KOSDAQ' },
  { code: '041510', name: '에스엠', market: 'KOSDAQ' },
  { code: '122870', name: '와이지엔터테인먼트', market: 'KOSDAQ' },
  { code: '263750', name: '펄어비스', market: 'KOSDAQ' },
  { code: '036570', name: '엔씨소프트', market: 'KOSPI' },
  { code: '066570', name: 'LG전자', market: 'KOSPI' },
  { code: '096770', name: 'SK이노베이션', market: 'KOSPI' },
  { code: '010950', name: 'S-Oil', market: 'KOSPI' },
  { code: '003670', name: '포스코퓨처엠', market: 'KOSPI' },
  { code: '329180', name: 'HD현대중공업', market: 'KOSPI' },
  { code: '042660', name: '한화오션', market: 'KOSPI' },
  { code: '012450', name: '한화에어로스페이스', market: 'KOSPI' },
  { code: '047810', name: '한국항공우주', market: 'KOSPI' },
  { code: '005935', name: '삼성전자우', market: 'KOSPI' }
];

export function searchStocks(keyword: string): StockItem[] {
  const query = keyword.trim().toLowerCase();
  if (!query) {
    return STOCK_MASTER.slice(0, 20);
  }

  return STOCK_MASTER.filter(
    (stock) =>
      stock.name.toLowerCase().includes(query) ||
      stock.code.toLowerCase().includes(query)
  );
}

export function findStockByCode(code: string): StockItem | undefined {
  return STOCK_MASTER.find((stock) => stock.code === code.trim());
}
