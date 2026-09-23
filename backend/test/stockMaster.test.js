import test from 'node:test';
import assert from 'node:assert';
import { searchStocks, findStockByCode } from '../dist/services/stockMaster.js';

test('searchStocks finds stocks by Korean name', () => {
  const results = searchStocks('삼성');
  assert.ok(results.length > 0);
  assert.ok(results.some((s) => s.name === '삼성전자' && s.code === '005930'));
});

test('searchStocks finds stocks by code', () => {
  const results = searchStocks('000660');
  assert.strictEqual(results.length, 1);
  assert.strictEqual(results[0].name, 'SK하이닉스');
});

test('findStockByCode returns correct stock', () => {
  const stock = findStockByCode('035420');
  assert.ok(stock);
  assert.strictEqual(stock.name, 'NAVER');
  assert.strictEqual(stock.market, 'KOSPI');
});
