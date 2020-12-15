export class ProjectPartnerBudgetConstants {

  public static MAX_NUMBER_OF_ITEMS = 300;
  public static MAX_TEXT_LENGTH = 250;
  public static MAX_VALUE = 999_999_999;
  public static MIN_VALUE = 0;
  public static FORM_CONTROL_NAMES = {
    staff: 'staff',
    travel: 'travel',
    external: 'external',
    equipment: 'equipment',
    infrastructure: 'infrastructure',
    items: 'items',
    numberOfUnits: 'numberOfUnits',
    pricePerUnit: 'pricePerUnit',
    description: 'description',
    rowSum: 'rowSum',
    total: 'total'
  };

  public static FORM_ERRORS = {
    total: {
      max: 'project.partner.budget.table.total.max.invalid'
    },
  };

  public static FORM_ERRORS_ARGS = {
    total: {
      max: {maxValue: ProjectPartnerBudgetConstants.MAX_VALUE}
    },
  };

}
