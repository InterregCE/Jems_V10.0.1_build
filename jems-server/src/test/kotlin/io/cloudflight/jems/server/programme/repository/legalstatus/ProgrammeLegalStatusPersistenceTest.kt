package io.cloudflight.jems.server.programme.repository.legalstatus

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusTranslationEntity
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProgrammeLegalStatusPersistenceTest : UnitTest() {

    companion object {
        private val ID = 1L

        private val legalStatusEntity = ProgrammeLegalStatusEntity(id = ID, ProgrammeLegalStatusType.OTHER).apply {
            translatedValues.addAll(
                setOf(
                    ProgrammeLegalStatusTranslationEntity(
                        translationId = TranslationId(sourceEntity = this, language = EN),
                        description = "EN desc"
                    ),
                    ProgrammeLegalStatusTranslationEntity(
                        translationId = TranslationId(sourceEntity = this, language = SK),
                        description = "SK desc"
                    ),
                )
            )
        }

        private val legalStatus = ProgrammeLegalStatus(
            id = ID,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc")
            ),
            type = ProgrammeLegalStatusType.OTHER
        )

    }

    @MockK
    lateinit var repository: ProgrammeLegalStatusRepository


    @InjectMockKs
    private lateinit var persistence: ProgrammeLegalStatusPersistenceProvider

    @Test
    fun getMax20Statuses() {
        every { repository.findTop20ByOrderById() } returns listOf(legalStatusEntity)
        assertThat(persistence.getMax20Statuses()).containsExactly(legalStatus)
    }

    @Test
    fun `updateLegalStatuses - everything should be fine`() {
        val toBeRemoved = listOf(
            ProgrammeLegalStatusEntity(id = 14, ProgrammeLegalStatusType.OTHER),
            ProgrammeLegalStatusEntity(id = 15, ProgrammeLegalStatusType.OTHER),
        )

        val slotToDelete = slot<Iterable<ProgrammeLegalStatusEntity>>()
        every { repository.findAllById(setOf(14, 15)) } returns toBeRemoved
        every { repository.deleteInBatch(capture(slotToDelete)) } answers { }
        every { repository.saveAll(any<List<ProgrammeLegalStatusEntity>>()) } returnsArgument 0
        every { repository.findTop20ByOrderById() } returns listOf(legalStatusEntity)

        assertThat(
            persistence.updateLegalStatuses(
                toDeleteIds = setOf(14, 15),
                toPersist = listOf(legalStatus.copy(id = 0))
            )
        ).containsExactly(legalStatus)

        verify(exactly = 1) { repository.deleteInBatch(toBeRemoved) }
    }

}
