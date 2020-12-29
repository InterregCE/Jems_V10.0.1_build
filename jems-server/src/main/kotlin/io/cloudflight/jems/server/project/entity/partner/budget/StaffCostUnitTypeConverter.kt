package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.service.partner.model.StaffCostUnitType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class StaffCostUnitTypeConverter : AttributeConverter<StaffCostUnitType, String?> {

    override fun convertToDatabaseColumn(staffCostUnitType: StaffCostUnitType) =
        if (staffCostUnitType !== StaffCostUnitType.NONE) staffCostUnitType.key else null;

    override fun convertToEntityAttribute(key: String?) =
        if (key == null) StaffCostUnitType.NONE else StaffCostUnitType.values().firstOrNull { it.key == key }
            ?: StaffCostUnitType.NONE
}
