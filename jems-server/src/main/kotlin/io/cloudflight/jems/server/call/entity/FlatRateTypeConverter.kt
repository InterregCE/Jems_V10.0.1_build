package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class FlatRateTypeConverter : AttributeConverter<FlatRateType, String> {
    override fun convertToDatabaseColumn(flatRateType: FlatRateType): String {
        return flatRateType.key
    }

    override fun convertToEntityAttribute(key: String): FlatRateType {
        return FlatRateType.values().first { it.key == key }
    }
}
