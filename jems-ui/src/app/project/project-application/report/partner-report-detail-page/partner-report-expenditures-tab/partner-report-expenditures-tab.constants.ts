
export class PartnerReportExpendituresTabConstants {
  public static MIN_VALUE = 0;
  public static NEGATIVE_MIN_VALUE = -999_999_999.99;
  public static MAX_VALUE = 999_999_999.99;
  public static MAX_NUMBER_OF_ITEMS = 150;
  public static MAX_LENGTH = 255;
  public static MAX_LENGTH_INVOICE = 30;
  public static FOCUS_TIMEOUT = 50;
  public static FORM_CONTROL_NAMES = {
    rowId: 'rowId',
    number: 'number',
    parkingMetadata: 'parkingMetadata',
    reportOfOriginNumber: 'reportOfOriginNumber',
    originalExpenditureNumber: 'originalExpenditureNumber',
    costOptions: 'costOptions',
    costCategory: 'costCategory',
    costGDPR: 'costGDPR',
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
    declaredAmountInEur: 'declaredAmountInEur'
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
