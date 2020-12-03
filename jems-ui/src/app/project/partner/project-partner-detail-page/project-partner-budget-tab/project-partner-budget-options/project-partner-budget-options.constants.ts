export const BUDGET_OPTIONS_FORM_CONTROL_NAMES = {
  staffCostsFlatRateBasedOnDirectCost: 'staffCostsFlatRateBasedOnDirectCost',
  isStaffCostsFlatRateBasedOnDirectCostActive: 'isStaffCostsFlatRateBasedOnDirectCostActive',
  officeFlatRateBasedOnStaffCost: 'officeFlatRateBasedOnStaffCost',
  isOfficeFlatRateBasedOnStaffCostActive: 'isOfficeFlatRateBasedOnStaffCostActive',
  isTravelFlatRateBasedOnStaffCostActive: 'isTravelFlatRateBasedOnStaffCostActive',
  travelFlatRateBasedOnStaffCost: 'travelFlatRateBasedOnStaffCost'
};

export const BUDGET_OPTIONS_FORM_ERRORS = {
  officeAdministrationFlatRateErrors: {
    required: 'project.partner.budget.options.office.on.staff.cost.flat.rate.empty',
    max: 'project.partner.budget.options.office.on.staff.cost.flat.rate.range',
    min: 'project.partner.budget.options.office.on.staff.cost.flat.rate.range',
  },
  staffCostsFlatRateErrors: {
    required: 'project.partner.budget.options.staff.costs.on.direct.cost.flat.rate.empty',
    max: 'project.partner.budget.options.staff.costs.on.direct.cost.flat.rate.range',
    min: 'project.partner.budget.options.staff.costs.on.direct.cost.flat.rate.range',
  },
  travelFlatRateErrors: {
    required: 'project.partner.budget.options.travel.on.staff.cost.flat.rate.empty',
    max: 'project.partner.budget.options.travel.on.staff.cost.flat.rate.range',
    min: 'project.partner.budget.options.travel.on.staff.cost.flat.rate.range',
  }
};
