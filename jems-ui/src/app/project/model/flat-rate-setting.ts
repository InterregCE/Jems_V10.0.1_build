export class FlatRateSetting {
  rate: number;
  adjustable: boolean;

  constructor(rate: number, adjustable: boolean) {
    this.rate = rate;
    this.adjustable = adjustable;
  }
}
