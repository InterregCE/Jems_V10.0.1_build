import {Injectable} from '@angular/core';
import {CurrencyMaskConfig, CurrencyMaskInputMode} from 'ngx-currency';
import {Big, RoundingMode} from 'big.js';

@Injectable({providedIn: 'root'})
export class NumberService {

  /**
   * Converts the given number to specific locale (eg. de-DE).
   */
  static toLocale(value: number, locale = 'de-DE', numberFormatOptions?: any): string | number {
    if (value === undefined) {
      return '';
    }
    if (isNaN(value)) {
      return NaN;
    }
    return new Intl.NumberFormat(
      locale,
      numberFormatOptions || {minimumFractionDigits: 2, maximumFractionDigits: 2}
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
    return Number(
      arr.filter(item => item !== null)
        .reduce((accumulator, value) => accumulator.plus(value), new Big(0))
    );
  }

  /**
   * Minus the numbers from the given array using Big.js in order to avoid rounding errors.
   */
  static minus(minuend: number, subtrahend: number): number {
    return Number(new Big(minuend).minus(subtrahend));
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
   * Divides the numbers using Big.js in order to avoid rounding errors.
   */
  static divide(dividend: number | null, divisor: number | null): number {
    if (!dividend || !divisor) {
      return 0;
    }
    return Number(new Big(dividend).div(divisor));
  }

  /**
   * Truncates (rounds down) the given number to a fixed number of two decimals.
   */
  static truncateNumber(toFloor: number, fractionLength = 2): number {
    return Number(Big(toFloor).round(fractionLength, RoundingMode.RoundDown));
  }

  /**
   * Rounds a number mathematically to a fixed number of decimals (default = 2).
   */
  static roundNumber(value: number, precision = 2): number {
    return Number(Big(value).round(precision));
  }

  /**
   * Customizable later.
   */
  getPrecision(): number {
    return 2;
  }

  /**
   * Customizable later.
   */
  getDecimalSeparator(): string {
    return ',';
  }

  /**
   * Customizable later.
   */
  getThousandsSeparator(): string {
    return '.';
  }

  decimalInput(custom?: Partial<CurrencyMaskConfig>): CurrencyMaskConfig {
    return Object.assign(
      {
        align: 'left',
        allowNegative: false, // maybe the better default is true? check usages and adapt..
        allowZero: true,
        decimal: this.getDecimalSeparator(),
        precision: this.getPrecision(),
        prefix: '',
        suffix: '',
        thousands: this.getThousandsSeparator(),
        nullable: false,
        inputMode: CurrencyMaskInputMode.NATURAL
      },
      custom);
  }

  integerInput(custom?: Partial<CurrencyMaskConfig>): CurrencyMaskConfig {
    return Object.assign(
      {
        align: 'left',
        allowNegative: false,
        allowZero: true,
        decimal: null as any,
        precision: 0,
        prefix: '',
        suffix: '',
        thousands: this.getThousandsSeparator(),
        nullable: false,
        inputMode: CurrencyMaskInputMode.NATURAL
      },
      custom);
  }
}
