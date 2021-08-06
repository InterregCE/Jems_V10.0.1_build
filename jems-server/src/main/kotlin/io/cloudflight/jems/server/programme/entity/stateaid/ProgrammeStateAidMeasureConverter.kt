package io.cloudflight.jems.server.programme.entity.stateaid

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class ProgrammeStateAidMeasureConverter : AttributeConverter<ProgrammeStateAidMeasure, String> {
    override fun convertToDatabaseColumn(stateAidMeasure: ProgrammeStateAidMeasure): String {
        return stateAidMeasure.key
    }

    override fun convertToEntityAttribute(key: String): ProgrammeStateAidMeasure {
        return ProgrammeStateAidMeasure.values().first { it.key == key }
    }

}
