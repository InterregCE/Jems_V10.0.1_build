export class ProjectPartnerBudgetOptionsConstants {
  public static MIN_FLAT_RATE_VALUE = 1;
  public static FORM_CONTROL_NAMES = {
    staffCostsFlatRate: 'StaffCost',
    isStaffCostsFlatRateActive: 'isStaffCostsFlatRateActive',
    officeAndAdministrationOnStaffCostsFlatRate: 'OfficeOnStaff',
    isOfficeAndAdministrationOnStaffCostsFlatRateActive: 'isOfficeAndAdministrationOnStaffCostsFlatRateActive',
    officeAndAdministrationOnDirectCostsFlatRate: 'OfficeOnOther',
    isOfficeAndAdministrationOnDirectCostsFlatRateActive: 'isOfficeAndAdministrationOnDirectCostsFlatRateActive',
    travelAndAccommodationOnStaffCostsFlatRate: 'TravelOnStaff',
    isTravelAndAccommodationOnStaffCostsFlatRateActive: 'isTravelAndAccommodationOnStaffCostsFlatRateActive',
    otherCostsOnStaffCostsFlatRate: 'OtherOnStaff',
    isOtherCostsOnStaffCostsFlatRateActive: 'isOtherCostsOnStaffCostsFlatRateActive',
  };

  public static FORM_ERRORS = {
    flatRateErrors: {
      required: 'use.case.update.budget.options.flat.rate.empty.error',
      max: 'use.case.update.budget.options.flat.rate.range.error',
      min: 'use.case.update.budget.options.flat.rate.range.error',
    },
  };

}
