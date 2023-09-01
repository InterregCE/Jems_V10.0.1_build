export class ProjectVerificationReportExpenditureConstants {
  public static MIN_VALUE = -999_999_999.99;
  public static MAX_CHARS = 5000;
  public static MAX_LENGTH_VERIFY_COMMENT = 1000;

  public static RISK_BASED_FORM_CONTROL_NAMES = {
    riskBasedVerification: 'riskBasedVerification',
    riskBasedVerificationDescription: 'riskBasedVerificationDescription',
  };

  public static EXPENDITURE_FORM_CONTROL_NAMES = {
    expenditureLines: 'expenditureLines',
    expenditureData: 'expenditure',
    expenditure: {
      id: 'id',
      number: 'number',
      partnerId: 'partnerId',
      partnerRole: 'partnerRole',
      partnerNumber: 'partnerNumber',
      partnerReportId: 'partnerReportId',
      partnerReportNumber: 'partnerReportNumber',
      lumpSum: 'lumpSum',
      unitCost: 'unitCost',
      gdpr: 'gdpr',
      costCategory: 'costCategory',
      investment: 'investment',
      contract: 'contract',
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
      currencyCode: 'currencyCode',
      currencyConversionRate: 'currencyConversionRate',
      declaredAmountAfterSubmission: 'declaredAmountAfterSubmission',
      attachment: 'attachment',
      partOfSample: 'partOfSample',
      partOfSampleLocked: 'partOfSampleLocked',
      certifiedAmount: 'certifiedAmount',
      deductedAmount: 'deductedAmount',
      typologyOfErrorId: 'typologyOfErrorId',
      parked: 'parked',
      verificationComment: 'verificationComment',
      parkingMetadata: 'parkingMetadata',
    },
    verificationData: 'verification',
    verification: {
      expenditureId: 'expenditureId',
      partOfVerificationSample: 'partOfVerificationSample',
      deductedByJs: 'deductedByJs',
      deductedByMa: 'deductedByMa',
      amountAfterVerification: 'amountAfterVerification',
      typologyOfErrorId: 'typologyOfErrorId',
      parked: 'parked',
      verificationComment: 'verificationComment',
    }
  };
}
