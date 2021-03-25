package io.cloudflight.jems.server.programme.entity.legalstatus

import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class ProgrammeLegalStatusTypeConverter : AttributeConverter<ProgrammeLegalStatusType, String> {
    override fun convertToDatabaseColumn(legalStatusType: ProgrammeLegalStatusType): String {
        return legalStatusType.key
    }

    override fun convertToEntityAttribute(key: String): ProgrammeLegalStatusType {
        return ProgrammeLegalStatusType.values().first { it.key == key }
    }
}
