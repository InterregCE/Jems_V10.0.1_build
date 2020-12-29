import {NumberService} from '../../../../../common/services/number.service';

export class ProjectPartnerBudgetConstants {

  public static MAX_NUMBER_OF_ITEMS = 300;
  public static MAX_STAFF_COMMENT_TEXT_LENGTH = 250;
  public static MAX_UNIT_TYPE_TEXT_LENGTH = 100;
  public static MAX_AWARD_PROCEDURES_TEXT_LENGTH = 250;
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
    type: 'type',
    unitType: 'unitType',
    awardProcedures: 'awardProcedures',
    investmentId: 'investmentId',
    comment: 'comment',
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
      max: {maxValue: NumberService.toLocale(ProjectPartnerBudgetConstants.MAX_VALUE)}
    },
  };

}
