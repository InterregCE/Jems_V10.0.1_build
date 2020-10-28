import {Big, RoundingMode} from 'big.js';

export class Numbers {

  /**
   * Converts the given number to specific locale (eg. de-DE).
   */
  static toLocale(value: number, locale: string): string | number {
    if (value === undefined) {
      return '';
    }
    if (isNaN(value)) {
      return NaN;
    }
    return new Intl.NumberFormat(
      locale,
      {minimumFractionDigits: 2, maximumFractionDigits: 2}
    ).format(value);
  }

  /**
   * Removes the thousand separator, replaces comma as decimal separator and converts the value to decimal
   */
  static toDecimal(value: string): number {
    if (!value) {
      return 0;
    }
    return parseFloat(
      value
        .split('.').join('')
        .replace(',', '.')
    );
  }

  /**
   * Sums the numbers from the given array using Big.js in order to avoid rounding errors.
   */
  static sum(arr: number[]): number {
    if (!arr || !arr.length) {
      return 0;
    }
    let sum = new Big(0);
    arr.forEach(nr => sum = sum.plus(nr));
    return Number(sum);
  }

  /**
   * Multiplies the numbers from the given array using Big.js in order to avoid rounding errors.
   */
  static product(arr: number[]): number {
    if (!arr || !arr.length) {
      return 0;
    }
    let prod = new Big(1);
    arr.forEach(nr => prod = prod.mul(nr));
    return Number(prod);
  }

  /**
   * Truncates (rounds down) the given number to a fixed number of two decimals.
   */
  static truncateNumber(toFloor: number): number {
    return Number(Big(toFloor).round(2, RoundingMode.RoundDown));
  }
}
