package io.cloudflight.jems.server.programme.service.checklist.model.metadata

class OptionsToggleMetadata(
    var question: String = "",
    val firstOption: String,
    val secondOption: String,
    val thirdOption: String? = null,
    val justification: String? = null
) : ProgrammeChecklistMetadata
