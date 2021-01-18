export class PartnerContribution {
  partnerId: number;
  amount: number;

  constructor(partnerId: number, amount: number) {
    this.partnerId = partnerId;
    this.amount = amount;
  }
}
