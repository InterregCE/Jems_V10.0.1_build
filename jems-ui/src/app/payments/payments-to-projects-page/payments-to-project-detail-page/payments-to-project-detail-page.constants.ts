export class PaymentsToProjectDetailPageConstants {

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
    {minInRem: 5, maxInRem: 8}, //Installment number
    {minInRem:10, maxInRem: 15}, //Amount paid
    {minInRem: 8, maxInRem: 12},//Payment date
    {minInRem: 18, maxInRem: 45},//Comment
    {minInRem: 5, maxInRem: 40},//Save payment
    {minInRem: 10, maxInRem: 20},//User saving payment
    {minInRem: 6, maxInRem: 20},//Save payment date
    {minInRem: 5, maxInRem: 20},//Confirm payment
    {minInRem: 12, maxInRem: 30},//User confirming payment
    {minInRem: 6, maxInRem:20},//Payment confirmation date
    {minInRem: 2, maxInRem:3}//Actions
  ];

}
