import {PartnerContribution} from './partnerContribution';

export class ProjectLumpSum {
  programmeLumpSumId: number;
  period: number;
  lumpSumContributions: PartnerContribution[];
  comment: string;
  readyForPayment: boolean;

  constructor(
    programmeLumpSumId: number,
    period: number,
    lumpSumContributions: PartnerContribution[],
    comment: string,
    readyForPayment: boolean
  ) {
    this.programmeLumpSumId = programmeLumpSumId;
    this.period = period;
    this.lumpSumContributions = lumpSumContributions;
    this.comment = comment;
    this.readyForPayment = readyForPayment;
  }
}
