export interface PaymentToEcAmountUpdate {
  correctedPublicContribution: number;
  correctedAutoPublicContribution: number;
  correctedPrivateContribution: number;
  correctionId: number;
  comment?: string;
  correctedTotalEligibleWithoutArt94or95: number;
  correctedUnionContribution: number;
  correctedFundAmount: number;
}
