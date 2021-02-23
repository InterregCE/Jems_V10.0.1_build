package io.cloudflight.jems.server.programme.entity.legalstatus

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_legal_status_transl")
data class ProgrammeLegalStatusTranslationEntity(
    @EmbeddedId
    val translationId: ProgrammeLegalStatusTranslationId,
    val description: String? = null,
) : Serializable
