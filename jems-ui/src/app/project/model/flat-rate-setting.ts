export class FlatRateSetting {
  rate: number;
  isAdjustable: boolean;

  constructor(rate: number, isAdjustable: boolean) {
    this.rate = rate;
    this.isAdjustable = isAdjustable;
  }
}
