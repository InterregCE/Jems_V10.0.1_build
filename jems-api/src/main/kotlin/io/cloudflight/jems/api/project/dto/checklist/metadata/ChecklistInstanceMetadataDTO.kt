package io.cloudflight.jems.api.project.dto.checklist.metadata

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
    JsonSubTypes.Type(value = OptionsToggleInstanceMetadataDTO::class, name = "OPTIONS_TOGGLE"),
    JsonSubTypes.Type(value = HeadlineInstanceMetadataDTO::class, name = "HEADLINE")
)
open class ChecklistInstanceMetadataDTO(@JsonIgnore val type: ProgrammeChecklistComponentTypeDTO)
