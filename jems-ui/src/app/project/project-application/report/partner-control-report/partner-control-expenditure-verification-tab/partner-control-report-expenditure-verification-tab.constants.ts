
export class PartnerControlReportExpenditureConstants {
  public static MIN_VALUE = 0;
  public static MAX_VALUE = 999_999_999.99;
  public static MAX_NUMBER_OF_ITEMS = 150;
  public static MAX_LENGTH = 255;
  public static MAX_LENGTH_INVOICE = 30;
  public static FOCUS_TIMEOUT = 50;
  public static FORM_CONTROL_NAMES = {
    rowId: 'rowId',
    costOptions: 'costOptions',
    costCategory: 'costCategory',
    investmentId: 'investmentId',
    contractId: 'contractId',
    internalReferenceNumber: 'internalReferenceNumber',
    invoiceNumber: 'invoiceNumber',
    invoiceDate: 'invoiceDate',
    dateOfPayment: 'dateOfPayment',
    description: 'description',
    comment: 'comment',
    totalValueInvoice: 'totalValueInvoice',
    vat: 'vat',
    numberOfUnits: 'numberOfUnits',
    pricePerUnit: 'pricePerUnit',
    declaredAmount: 'declaredAmount',
    items: 'items',
    attachment: 'attachment',
    currencyCode: 'currencyCode',
    currencyConversionRate: 'currencyConversionRate',
    declaredAmountInEur: 'declaredAmountInEur',

    partOfSample: 'partOfSample',
    certifiedAmount: 'certifiedAmount',
    deductedAmount: 'deductedAmount',
    typologyOfErrorId: 'typologyOfErrorId',
    verificationComment: 'verificationComment',
  };

  public static FORM_ERRORS = {
    costCategory: {
      required: 'project.application.partner.report.expenditures.cost.category.required.error'
    },
    invoiceNumber: {
      maxlength: 'common.error.field.max.length'
    }
  };
}
