package io.cloudflight.jems.server.project.entity.partner.budget.staff_cost

import io.cloudflight.jems.server.project.service.partner.model.StaffCostType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class StaffCostTypeConverter : AttributeConverter<StaffCostType, String?> {

    override fun convertToDatabaseColumn(staffCostType: StaffCostType?) =
        staffCostType?.key

    override fun convertToEntityAttribute(key: String?)=
        StaffCostType.values().firstOrNull { it.key == key }
}
