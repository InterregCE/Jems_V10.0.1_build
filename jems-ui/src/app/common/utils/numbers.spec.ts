import {Numbers} from './numbers';

describe('Numbers', () => {

  it('toLocale', () => {
    expect(Numbers.toLocale(102000.876, 'DE')).toBe('102.000,88');
    expect(Numbers.toLocale(102000, 'DE')).toBe('102.000,00');
  });

  it('toDecimal', () => {
    expect(Numbers.toDecimal('102.000,876')).toBe(102000.876);
  });

  it('sum', () => {
    // default js sums may result in rounding errors
    expect(6000.00 + 2000.90 + 278.22).toBe(8279.119999999999);
    expect(Numbers.sum([6000.00, 2000.90, 278.22])).toBe(8279.12);
  });

  it('product', () => {
    // default js products may result in rounding errors
    expect(619571.60 * 0.15).toBe(92935.73999999999);
    expect(Numbers.product([619571.60, 0.15])).toBe(92935.74);
  });

  it('truncateNumber', () => {
    expect(Numbers.truncateNumber(19)).toBe(19);
    expect(Numbers.truncateNumber(0.0019)).toBe(0.00);
    expect(Numbers.truncateNumber(0.199)).toBe(0.19);
    expect(Numbers.truncateNumber(19.90)).toBe(19.90);
  });
});
