export class InvestmentSummary {
  id: number;
  investmentNumber: number;
  workPackageNumber: number;

  constructor(id: number, investmentNumber: number, workPackageId: number) {
    this.id = id;
    this.investmentNumber = investmentNumber;
    this.workPackageNumber = workPackageId;
  }

  toString(): string{
    return `I${this.workPackageNumber}.${this.investmentNumber}`;
  }
}
