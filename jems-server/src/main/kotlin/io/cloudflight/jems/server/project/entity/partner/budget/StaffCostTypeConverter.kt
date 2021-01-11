package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.service.partner.model.StaffCostType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class StaffCostTypeConverter : AttributeConverter<StaffCostType, String?> {

    override fun convertToDatabaseColumn(staffCostType: StaffCostType) =
        if (staffCostType == StaffCostType.NONE) null else staffCostType.key

    override fun convertToEntityAttribute(key: String?) =
        if (key == null) StaffCostType.NONE else StaffCostType.values().firstOrNull { it.key == key }
            ?: StaffCostType.NONE
}
