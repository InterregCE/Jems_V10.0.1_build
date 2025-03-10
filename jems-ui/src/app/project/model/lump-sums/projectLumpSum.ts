import {PartnerContribution} from './partnerContribution';

export class ProjectLumpSum {
  orderNr: number;
  programmeLumpSumId: number;
  period: number;
  lumpSumContributions: PartnerContribution[];
  comment: string;
  readyForPayment: boolean;
  fastTrack: boolean;
  paymentEnabledDate: Date;
  lastApprovedVersionBeforeReadyForPayment: string;
  installmentsAlreadyCreated: boolean;
  linkedToEcPaymentId: number;

  constructor(
    orderNr: number,
    programmeLumpSumId: number,
    period: number,
    lumpSumContributions: PartnerContribution[],
    comment: string,
    readyForPayment: boolean,
    fastTrack: boolean,
    paymentEnabledDate: Date,
    lastApprovedVersionBeforeReadyForPayment: string,
    installmentsAlreadyCreated: boolean,
    linkedToEcPaymentId: number
  ) {
    this.orderNr = orderNr;
    this.programmeLumpSumId = programmeLumpSumId;
    this.period = period;
    this.lumpSumContributions = lumpSumContributions;
    this.comment = comment;
    this.readyForPayment = readyForPayment;
    this.fastTrack = fastTrack;
    this.paymentEnabledDate = paymentEnabledDate;
    this.lastApprovedVersionBeforeReadyForPayment = lastApprovedVersionBeforeReadyForPayment;
    this.installmentsAlreadyCreated = installmentsAlreadyCreated;
    this.linkedToEcPaymentId = linkedToEcPaymentId;
  }
}
