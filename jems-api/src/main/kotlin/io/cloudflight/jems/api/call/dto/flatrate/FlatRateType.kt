package io.cloudflight.jems.api.call.dto.flatrate

enum class FlatRateType(val key:String) {
    STAFF_COSTS("StaffCost"),
    OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS("OfficeOnStaff"),
    OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS("OfficeOnOther"),
    TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS("TravelOnStaff"),
    OTHER_COSTS_ON_STAFF_COSTS("OtherOnStaff"),
}
