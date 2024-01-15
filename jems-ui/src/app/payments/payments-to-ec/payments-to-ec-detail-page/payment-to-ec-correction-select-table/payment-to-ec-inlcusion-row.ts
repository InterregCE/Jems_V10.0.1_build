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
  correctedFundAmount: number;
  partnerContribution: number;
  publicContribution: number;
  correctedPublicContribution: number;
  autoPublicContribution: number;
  correctedAutoPublicContribution: number;
  privateContribution: number;
  correctedPrivateContribution: number;
  comment?: string;
  projectFlagged94Or95: boolean;
  totalEligibleWithoutArt94or95: number;
  correctedTotalEligibleWithoutArt94or95: number;
  unionContribution: number;
  correctedUnionContribution: number;
}
