package io.cloudflight.jems.server.programme.service.checklist.model.metadata

class TextInputMetadata (
    var question: String = "",
    val explanationLabel: String = "",
    val explanationMaxLength: Int = 5000,
): ProgrammeChecklistMetadata
