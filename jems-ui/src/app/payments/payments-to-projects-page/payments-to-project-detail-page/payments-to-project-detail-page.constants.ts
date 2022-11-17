export class PaymentsToProjectDetailPageConstants {

  public static MAX_VALUE = 999_999_999.99;

  public static FORM_CONTROL_NAMES = {
    partnerPayments: 'partnerPayments',
    partnerNumber: 'partnerNumber',
    partnerType: 'partnerType',
    partnerAbbreviation: 'partnerAbbreviation',
    amountApproved: 'amountApproved',
    installments: 'installments',

    installmentNumber: 'installmentNumber',
    amountPaid: 'amountPaid',
    paymentDate: 'paymentDate',
    comment: 'comment',
    savePaymentInfo: 'savePaymentInfo',
    savePaymentInfoUser: 'savePaymentInfoUser',
    savePaymentDate: 'savePaymentDate',
    paymentConfirmed: 'paymentConfirmed',
    paymentConfirmedUser: 'paymentConfirmedUser',
    paymentConfirmedDate: 'paymentConfirmedDate',
  };

  public static columnsWidths = [
    {minInRem: 4, maxInRem: 4}, //Installment number (just to align with the name)
    {minInRem: 9, maxInRem: 9}, //Amount paid
    {minInRem: 9, maxInRem: 9}, //Payment date
    {minInRem: 12},             //Comment
    {minInRem: 5, maxInRem: 5}, //Authorise payment
    {minInRem: 9},              //User authorising payment
    {minInRem: 9, maxInRem: 9}, //Authorisation date
    {minInRem: 5, maxInRem: 5}, //Confirm payment
    {minInRem: 9},              //User confirming payment
    {minInRem: 6, maxInRem: 6}, //Payment confirmation date
    {minInRem: 3, maxInRem: 3}  //Actions
  ];

}
