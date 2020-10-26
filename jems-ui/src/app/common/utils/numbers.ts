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
   * Converts the number to its string representation, truncates it up to two decimals and
   * converts it back to a number.
   * This is the safest way to avoid rounding precision errors.
   */
  static truncateNumber(toFloor: number): number {
    const truncatedAsString = toFloor.toString().match(/^-?\d+(?:\.\d{0,2})?/);
    return Number(truncatedAsString?.length ? truncatedAsString[0] : NaN);
  }
}
