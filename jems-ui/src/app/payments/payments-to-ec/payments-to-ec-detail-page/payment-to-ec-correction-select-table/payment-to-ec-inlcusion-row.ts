import {PaymentToEcCorrectionLinkingDTO} from '@cat/api';

export interface PaymentToEcInclusionRow {
  correctionId: number;
  paymentToEcId: number;
  projectCustomIdentifier: string;
  projectAcronym: string;
  priorityAxis: string;
  paymentCorrectionNo?: string;
  scenario?: PaymentToEcCorrectionLinkingDTO.ScenarioEnum;
  controllingBody?: string;
  amountApprovedPerFund: number;
  partnerContribution: number;
  publicContribution: number;
  correctedPublicContribution: number;
  autoPublicContribution: number;
  correctedAutoPublicContribution: number;
  privateContribution: number;
  correctedPrivateContribution: number;
  comment?: string;
}
