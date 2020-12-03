export const CALL_FLAT_RATE_FORM_ERRORS = {
  staffCostErrors: {
    max: 'call.detail.flat.rate.staff.direct.cost',
    min: 'call.detail.flat.rate.staff.direct.cost'
  },
  officeAdminDirectStaffCostPercentErrors: {
    max: 'call.detail.flat.rate.office.admin.direct.staff.cost',
    min: 'call.detail.flat.rate.office.admin.direct.staff.cost'
  },
  officeAdministrationDirectCostPercentErrors: {
    max: 'call.detail.flat.rate.office.admin.direct.cost',
    min: 'call.detail.flat.rate.office.admin.direct.cost'
  },
  travelAccommodationDirectStaffCostPercentErrors: {
    max: 'call.detail.flat.rate.travel.accommodation.direct.staff.cost',
    min: 'call.detail.flat.rate.travel.accommodation.direct.staff.cost'
  },
  otherCostsPercentErrors: {
    max: 'call.detail.flat.rate.other.cost',
    min: 'call.detail.flat.rate.other.cost'
  }
};
export const FLAT_RATE_MAX_VALUES = {
  STAFF_COST: 20,
  OFFICE_ON_STAFF: 15,
  OFFICE_ON_OTHER: 25,
  TRAVEL_ON_STAFF: 15,
  OTHER_ON_STAFF: 40,
  IS_ADJUSTABLE: false
};
