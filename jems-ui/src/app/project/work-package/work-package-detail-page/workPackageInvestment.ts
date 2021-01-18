export class InvestmentSummary {
  id: number;
  investmentNumber: number;
  workPackageId: number;

  constructor(id: number, investmentNumber: number, workPackageId: number) {
    this.id = id;
    this.investmentNumber = investmentNumber;
    this.workPackageId = workPackageId;
  }

  toString(): string{
    return `I${this.workPackageId}.${this.investmentNumber}`;
  }
}
