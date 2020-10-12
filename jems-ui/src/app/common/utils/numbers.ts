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

  static floorNumber(toFloor: number): number {
    return Math.floor(toFloor * 100) / 100;
  }
}
