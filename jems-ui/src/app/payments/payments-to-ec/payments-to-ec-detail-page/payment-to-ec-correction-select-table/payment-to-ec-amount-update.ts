export interface PaymentToEcAmountUpdate {
  correctedPublicContribution: number;
  correctedAutoPublicContribution: number;
  correctedPrivateContribution: number;
  correctionId: number;
  comment?: string;
}
