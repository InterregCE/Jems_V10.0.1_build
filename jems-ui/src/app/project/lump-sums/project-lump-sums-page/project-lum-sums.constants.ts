export class ProjectLumSumsConstants {


  public static MIN_VALUE = 0;
  public static MAX_VALUE = 999_999_999.99;
  public static MAX_NUMBER_OF_ITEMS = 50;
  public static FORM_CONTROL_NAMES = {
    lumpSum: 'lumpSum',
    periodNumber: 'periodNumber',
    partnersContribution: 'partnersContribution',
    amount: 'amount',
    id: 'id',
    partnerId: 'partnerId',
    rowSum: 'rowSum',
    gap: 'gap',
    items: 'items',
    readyForPayment: 'readyForPayment',
    comment: 'comment',
    fastTrack: 'fastTrack',
  };

  public static FORM_ERRORS = {
    items: {
      notSplittable: 'project.application.form.section.part.e.lump.sums.costs.not.splittable.between.partners.error'
    },
    lumpSum: {
      required: 'project.application.form.section.part.e.lump.sums.period.required.error'
    },
    period: {
      required: 'project.application.form.section.part.e.lump.sums.required.error'
    }
  };

}
