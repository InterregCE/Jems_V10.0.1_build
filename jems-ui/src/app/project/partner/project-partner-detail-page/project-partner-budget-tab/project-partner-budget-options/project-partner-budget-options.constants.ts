export class ProjectPartnerBudgetOptionsConstants {
  public static MIN_FLAT_RATE_VALUE = 1;
  public static FORM_CONTROL_NAMES = {
    staffCostsFlatRate: 'StaffCost',
    isStaffCostsFlatRateSelected: 'isStaffCostSelected',
    officeOnStaffCostsFlatRate: 'OfficeOnStaff',
    isOfficeOnStaffCostsFlatRateSelected: 'isOfficeOnStaffSelected',
    officeOnDirectCostsFlatRate: 'OfficeOnOther',
    isOfficeOnDirectCostsFlatRateSelected: 'isOfficeOnOtherSelected',
    travelAndAccommodationOnStaffCostsFlatRate: 'TravelOnStaff',
    isTravelAndAccommodationOnStaffCostsFlatRateSelected: 'isTravelOnStaffSelected',
    otherCostsOnStaffCostsFlatRate: 'OtherOnStaff',
    isOtherCostsOnStaffCostsFlatRateSelected: 'isOtherOnStaffSelected',
  };

  public static FORM_ERRORS = {
    flatRateErrors: {
      required: 'use.case.update.budget.options.flat.rate.empty.error',
      max: 'use.case.update.budget.options.flat.rate.range.error',
      min: 'use.case.update.budget.options.flat.rate.range.error',
    },
  };

}
