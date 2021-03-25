package io.cloudflight.jems.server.programme.entity.fund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class ProgrammeFundTypeConverter : AttributeConverter<ProgrammeFundType, String> {
    override fun convertToDatabaseColumn(legalStatusType: ProgrammeFundType): String {
        return legalStatusType.key
    }

    override fun convertToEntityAttribute(key: String): ProgrammeFundType {
        return ProgrammeFundType.values().first { it.key == key }
    }
}
