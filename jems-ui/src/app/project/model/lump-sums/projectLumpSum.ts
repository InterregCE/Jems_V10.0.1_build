import {PartnerContribution} from './partnerContribution';

export class ProjectLumpSum {
  programmeLumpSumId: number;
  period: number;
  lumpSumContributions: PartnerContribution[];

  constructor(programmeLumpSumId: number, period: number, lumpSumContributions: PartnerContribution[]) {
    this.programmeLumpSumId = programmeLumpSumId;
    this.period = period;
    this.lumpSumContributions = lumpSumContributions;
  }
}
