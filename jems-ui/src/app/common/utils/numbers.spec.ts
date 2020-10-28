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
    let x = 0; // 0.0001 to 0.0049
    let y = 0; // 0.0001 to 0.0050
    for (let i = 0; i < 50; i++) {
      for (let j = 0; j < 51; j++) {
        expect(Numbers.truncateNumber(Numbers.sum([x, y]))).toBe(0)

        y = Numbers.sum([y, 0.0001]);
      }
      y = 0;
      x = Numbers.sum([x, 0.0001]);
    }

    // default js sums may result in rounding errors
    expect(6000.00 + 2000.90 + 278.22).toBe(8279.119999999999);
    expect(Numbers.sum([6000.00, 2000.90, 278.22])).toBe(8279.12);
  });

  it('product', () => {
    let x = 0; // 0.001 to 0.099
    let y = 0; // 0.001 to 0.100
    for (let i = 0; i < 100; i++) {
      for (let j = 0; j < 101; j++) {
        expect(Numbers.truncateNumber(Numbers.product([x, y]))).toBe(0)

        y = Numbers.sum([y, 0.001]);
      }
      y = 0;
      x = Numbers.sum([x, 0.001]);
    }

    // default js products may result in rounding errors
    expect(619571.60 * 0.15).toBe(92935.73999999999);
    expect(Numbers.product([619571.60, 0.15])).toBe(92935.74);
  });

  it('truncateNumber', () => {
    let x = 0; // 0.0001 to 0.0099
    for (let i = 0; i < 100; i++) {
      expect(Numbers.truncateNumber(x)).toBe(0)
      x = Numbers.sum([x, 0.0001]);
    }

    // specific cases from past
    expect(Numbers.truncateNumber(19)).toBe(19);
    expect(Numbers.truncateNumber(0.0019)).toBe(0.00);
    expect(Numbers.truncateNumber(0.199)).toBe(0.19);
    expect(Numbers.truncateNumber(19.90)).toBe(19.90);
  });
});
