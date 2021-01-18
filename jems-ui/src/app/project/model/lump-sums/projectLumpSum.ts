import {PartnerContribution} from './partnerContribution';

export class ProjectLumpSum {
  id: string;
  programmeLumpSumId: number;
  period: number;
  lumpSumContributions: PartnerContribution[];

  constructor(id: string, programmeLumpSumId: number, period: number, lumpSumContributions: PartnerContribution[]) {
    this.id = id;
    this.programmeLumpSumId = programmeLumpSumId;
    this.period = period;
    this.lumpSumContributions = lumpSumContributions;
  }
}
