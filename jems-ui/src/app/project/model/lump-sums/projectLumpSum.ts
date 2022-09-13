import {PartnerContribution} from './partnerContribution';

export class ProjectLumpSum {
  programmeLumpSumId: number;
  period: number;
  lumpSumContributions: PartnerContribution[];
  comment: string;
  readyForPayment: boolean;
  fastTrack: boolean;
  paymentEnabledDate: Date;
  lastApprovedVersionBeforeReadyForPayment: string;

  constructor(
    programmeLumpSumId: number,
    period: number,
    lumpSumContributions: PartnerContribution[],
    comment: string,
    readyForPayment: boolean,
    fastTrack: boolean,
    paymentEnabledDate: Date,
    lastApprovedVersionBeforeReadyForPayment: string
  ) {
    this.programmeLumpSumId = programmeLumpSumId;
    this.period = period;
    this.lumpSumContributions = lumpSumContributions;
    this.comment = comment;
    this.readyForPayment = readyForPayment;
    this.fastTrack = fastTrack;
    this.paymentEnabledDate = paymentEnabledDate;
    this.lastApprovedVersionBeforeReadyForPayment = lastApprovedVersionBeforeReadyForPayment;
  }
}
