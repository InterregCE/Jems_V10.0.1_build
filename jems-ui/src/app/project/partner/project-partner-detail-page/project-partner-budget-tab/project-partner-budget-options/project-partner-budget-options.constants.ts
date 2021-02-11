export class ProjectPartnerBudgetOptionsConstants {
  public static MIN_FLAT_RATE_VALUE = 1;
  public static FORM_CONTROL_NAMES = {
    staffCostsFlatRate: 'staffCostsFlatRate',
    isStaffCostsFlatRateActive: 'isStaffCostsFlatRateActive',
    officeAndAdministrationOnStaffCostsFlatRate: 'officeAndAdministrationOnStaffCostsFlatRate',
    isOfficeAndAdministrationOnStaffCostsFlatRateActive: 'isOfficeAndAdministrationOnStaffCostsFlatRateActive',
    officeAndAdministrationOnDirectCostsFlatRate: 'officeAndAdministrationOnDirectCostsFlatRate',
    isOfficeAndAdministrationOnDirectCostsFlatRateActive: 'isOfficeAndAdministrationOnDirectCostsFlatRateActive',
    travelAndAccommodationOnStaffCostsFlatRate: 'travelAndAccommodationOnStaffCostsFlatRate',
    isTravelAndAccommodationOnStaffCostsFlatRateActive: 'isTravelAndAccommodationOnStaffCostsFlatRateActive',
    otherCostsOnStaffCostsFlatRate: 'otherCostsOnStaffCostsFlatRate',
    isOtherCostsOnStaffCostsFlatRateActive: 'isOtherCostsOnStaffCostsFlatRateActive',
  };

  public static FORM_ERRORS = {
    officeAndAdministrationOnStaffCostsFlatRateErrors: {
      required: 'project.partner.budget.options.office.on.staff.cost.flat.rate.empty',
      max: 'project.partner.budget.options.office.on.staff.cost.flat.rate.range',
      min: 'project.partner.budget.options.office.on.staff.cost.flat.rate.range',
    },
    officeAndAdministrationOnDirectCostsFlatRateErrors: {
      required: 'project.partner.budget.options.office.on.direct.cost.flat.rate.empty',
      max: 'project.partner.budget.options.office.on.direct.cost.flat.rate.range',
      min: 'project.partner.budget.options.office.on.direct.cost.flat.rate.range',
    },
    staffCostsFlatRateErrors: {
      required: 'project.partner.budget.options.staff.costs.on.direct.cost.flat.rate.empty',
      max: 'project.partner.budget.options.staff.costs.on.direct.cost.flat.rate.range',
      min: 'project.partner.budget.options.staff.costs.on.direct.cost.flat.rate.range',
    },
    travelAndAccommodationOnStaffCostsFlatRateErrors: {
      required: 'project.partner.budget.options.travel.on.staff.cost.flat.rate.empty',
      max: 'project.partner.budget.options.travel.on.staff.cost.flat.rate.range',
      min: 'project.partner.budget.options.travel.on.staff.cost.flat.rate.range',
    },
    otherCostsOnStaffCostsFlatRateErrors: {
      required: 'project.partner.budget.options.other.costs.on.staff.costs.flat.rate.empty',
      max: 'project.partner.budget.options.other.costs.on.staff.cost.flat.rate.range',
      min: 'project.partner.budget.options.other.costs.on.staff.cost.flat.rate.range',
    }
  };

}
