
export class PartnerReportExpendituresTabConstants {
  public static MIN_VALUE = 0;
  public static MAX_VALUE = 999_999_999.99;
  public static MAX_NUMBER_OF_ITEMS = 150;
  public static MAX_LENGTH = 255;
  public static MAX_LENGTH_INVOICE = 30;
  public static FORM_CONTROL_NAMES = {
    rowId: 'rowId',
    costCategory: 'costCategory',
    investmentNumber: 'investmentNumber',
    contractID: 'contractID',
    internalReferenceNumber: 'internalReferenceNumber',
    invoiceNumber: 'invoiceNumber',
    invoiceDate: 'invoiceDate',
    dateOfPayment: 'dateOfPayment',
    description: 'description',
    comment: 'comment',
    totalValueInvoice: 'totalValueInvoice',
    vat: 'vat',
    declaredAmount: 'declaredAmount',
    items: 'items',
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
