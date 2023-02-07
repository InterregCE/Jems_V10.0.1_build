export class InvestmentSummary {
  id: number;
  investmentNumber: number;
  workPackageNumber: number;
  deactivated: boolean;

  constructor(id: number, investmentNumber: number, workPackageId: number, deactivated: boolean) {
    this.id = id;
    this.investmentNumber = investmentNumber;
    this.workPackageNumber = workPackageId;
    this.deactivated = deactivated;
  }

  toString(): string{
    return `I${this.workPackageNumber}.${this.investmentNumber}`;
  }
}
