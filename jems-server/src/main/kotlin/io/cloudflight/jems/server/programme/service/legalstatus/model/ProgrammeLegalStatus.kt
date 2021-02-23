package io.cloudflight.jems.server.programme.service.legalstatus.model

data class ProgrammeLegalStatus(
    val id: Long = 0,
    val translatedValues: Set<ProgrammeLegalStatusTranslatedValue> = emptySet(),
)
