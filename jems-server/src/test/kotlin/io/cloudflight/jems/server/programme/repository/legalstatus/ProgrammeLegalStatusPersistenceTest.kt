package io.cloudflight.jems.server.programme.repository.legalstatus

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusTranslationEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusTranslationId
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusTranslatedValue
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

        private val legalStatusEntity = ProgrammeLegalStatusEntity(id = ID).apply {
            translatedValues.addAll(
                setOf(
                    ProgrammeLegalStatusTranslationEntity(
                        translationId = ProgrammeLegalStatusTranslationId(legalStatus = this, language = EN),
                        description = "EN desc"
                    ),
                    ProgrammeLegalStatusTranslationEntity(
                        translationId = ProgrammeLegalStatusTranslationId(legalStatus = this, language = SK),
                        description = "SK desc"
                    ),
                )
            )
        }

        private val legalStatus = ProgrammeLegalStatus(
            id = ID,
            translatedValues = setOf(
                ProgrammeLegalStatusTranslatedValue(language = EN, description = "EN desc"),
                ProgrammeLegalStatusTranslatedValue(language = SK, description = "SK desc"),
            )
        )

    }

    @MockK
    lateinit var repository: ProgrammeLegalStatusRepository

    @MockK
    lateinit var callRepository: CallRepository

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
            ProgrammeLegalStatusEntity(id = 14),
            ProgrammeLegalStatusEntity(id = 15),
        )

        every { callRepository.existsByStatus(CallStatus.PUBLISHED) } returns false
        val slotToDelete = slot<Iterable<ProgrammeLegalStatusEntity>>()
        every { repository.findAllById(setOf(14, 15)) } returns toBeRemoved
        every { repository.deleteInBatch(capture(slotToDelete)) } answers { }
        every { repository.saveAll(any()) } returnsArgument 0
        every { repository.findTop20ByOrderById() } returns listOf(legalStatusEntity)

        assertThat(
            persistence.updateLegalStatuses(
                toDeleteIds = setOf(14, 15),
                toPersist = listOf(legalStatus.copy(id = 0))
            )
        ).containsExactly(legalStatus)

        verify(exactly = 1) { repository.deleteInBatch(toBeRemoved) }
    }

    @Test
    fun `updateLegalStatuses - there should not be deletion if programme setup is restricted`() {
        val toBeRemoved = listOf(
            ProgrammeLegalStatusEntity(id = 14),
            ProgrammeLegalStatusEntity(id = 15),
        )

        every { callRepository.existsByStatus(CallStatus.PUBLISHED) } returns true
        every { repository.findAllById(setOf(14, 15)) } returns toBeRemoved
        every { repository.saveAll(any()) } returnsArgument 0
        every { repository.findTop20ByOrderById() } returns emptyList()

        persistence.updateLegalStatuses(toDeleteIds = setOf(14, 15), toPersist = emptyList())
        verify(exactly = 0) { repository.deleteInBatch(any()) }
    }

}
