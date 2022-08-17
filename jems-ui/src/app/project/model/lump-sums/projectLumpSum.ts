import {PartnerContribution} from './partnerContribution';

export class ProjectLumpSum {
  programmeLumpSumId: number;
  period: number;
  lumpSumContributions: PartnerContribution[];
  comment: string;
  readyForPayment: boolean;
  fastTrack: boolean;

  constructor(
    programmeLumpSumId: number,
    period: number,
    lumpSumContributions: PartnerContribution[],
    comment: string,
    readyForPayment: boolean,
    fastTrack: boolean
  ) {
    this.programmeLumpSumId = programmeLumpSumId;
    this.period = period;
    this.lumpSumContributions = lumpSumContributions;
    this.comment = comment;
    this.readyForPayment = readyForPayment;
    this.fastTrack = fastTrack;
  }
}
