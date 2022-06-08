package io.cloudflight.jems.api.programme.dto.checklist.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = OptionsToggleMetadataDTO::class, name = "OPTIONS_TOGGLE"),
    JsonSubTypes.Type(value = HeadlineMetadataDTO::class, name = "HEADLINE"),
    JsonSubTypes.Type(value = TextInputMetadataDTO::class, name = "TEXT_INPUT"),
    JsonSubTypes.Type(value = ScoreMetadataDTO::class, name = "SCORE")
)
open class ProgrammeChecklistMetadataDTO(@JsonIgnore val type: ProgrammeChecklistComponentTypeDTO)
