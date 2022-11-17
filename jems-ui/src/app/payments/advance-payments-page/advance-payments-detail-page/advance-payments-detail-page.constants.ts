export class AdvancePaymentsDetailPageConstants {

  public static MAX_VALUE = 999_999_999.99;

  public static FORM_CONTROL_NAMES = {
    id: 'id',
    projectId: 'projectId',
    projectIdSearch: 'projectIdSearch',
    advancePayment: 'advancePayment',
    settlements: 'settlements',

    projectCustomIdentifier: 'projectCustomIdentifier',
    projectCustomIdentifierSearch: 'projectCustomIdentifierSearch',
    projectAcronym: 'projectAcronym',
    partnerAbbreviation: 'partnerAbbreviation',
    selectedPartner: 'selectedPartner',
    partnerRole: 'partnerRole',
    partnerType: 'partnerType',
    partnerNumber: 'partnerNumber',
    sourceOrFundName: 'sourceOrFundName',
    programmeFundId: 'programmeFundId',
    partnerContributionId: 'partnerContributionId',
    partnerContributionSpfId: 'partnerContributionSpfId',
    amountAdvance: 'amountAdvance',
    dateOfPayment: 'dateOfPayment',
    comment: 'comment',
    paymentAuthorized: 'paymentAuthorized',
    paymentAuthorizedUser: 'paymentAuthorizedUser',
    paymentAuthorizedUserId: 'paymentAuthorizedUserId',
    paymentAuthorizedDate: 'paymentAuthorizedDate',
    paymentConfirmed: 'paymentConfirmed',
    paymentConfirmedUser: 'paymentConfirmedUser',
    paymentConfirmedUserId: 'paymentConfirmedUserId',
    paymentConfirmedDate: 'paymentConfirmedDate',

    settlementNr: 'settlementNr',
    amountSettled: 'amountSettled',
    settlementDate: 'settlementDate',
    settlementComment: 'settlementComment'
  };

  public static inputErrorMessages = {
    min: 'payments.advance.payments.detail.amount.error',
    error: 'common.error.field.blank',
  };
}
