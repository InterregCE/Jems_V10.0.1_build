package io.cloudflight.jems.server.project.entity.partner.budget.staff_cost

import io.cloudflight.jems.server.project.service.partner.model.StaffCostUnitType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class StaffCostUnitTypeConverter : AttributeConverter<StaffCostUnitType, String?> {

    override fun convertToDatabaseColumn(staffCostUnitType: StaffCostUnitType?) =
        staffCostUnitType?.key

    override fun convertToEntityAttribute(key: String?) =
        StaffCostUnitType.values().firstOrNull { it.key == key }

}
