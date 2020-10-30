package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLegalStatus
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.repository.ProgrammeLegalStatusRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProgrammeLegalStatusServiceTest {

    companion object {
        val programmeLegalStatus = ProgrammeLegalStatus(
            id = 3L,
            description = "3rd Status"
        )
    }

    @RelaxedMockK
    lateinit var programmeLegalStatusRepository: ProgrammeLegalStatusRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var programmeLegalStatusService: ProgrammeLegalStatusService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeLegalStatusService = ProgrammeLegalStatusServiceImpl(
            programmeLegalStatusRepository,
            auditService
        )
    }

    @Test
    fun get() {
        every { programmeLegalStatusRepository.findAll() } returns listOf(programmeLegalStatus)
        assertThat(programmeLegalStatusService.get())
            .isEqualTo(
                listOf(
                    OutputProgrammeLegalStatus(
                        id = programmeLegalStatus.id!!,
                        description = programmeLegalStatus.description
                    )
                )
            )
    }

    @Test
    fun `save new`() {
        val toBeCreatedLegalStatus = InputProgrammeLegalStatus(description = "created status");
        val toBeSaved = listOf(
            InputProgrammeLegalStatus(id = 1L),
            InputProgrammeLegalStatus(id = 2L),
            toBeCreatedLegalStatus
        )
        val toBeDeleted = listOf(InputProgrammeLegalStatus(id = 3L))
        val expectedResult = listOf(
            ProgrammeLegalStatus(
                id = 10L,
                description = toBeCreatedLegalStatus.description
            )
        )

        every { programmeLegalStatusRepository.findAllById(eq(emptySet())) } returns emptyList()
        val saveLegalStatusSlot = slot<List<ProgrammeLegalStatus>>()
        every { programmeLegalStatusRepository.saveAll(capture(saveLegalStatusSlot)) } returns expectedResult
        every { programmeLegalStatusRepository.count() } returns expectedResult.size.toLong()
        every { programmeLegalStatusRepository.findAll() } returns expectedResult

        val result = programmeLegalStatusService.save(toBeSaved, toBeDeleted)
        assertThat(result)
            .isEqualTo(
                listOf(
                    OutputProgrammeLegalStatus(
                        id = 10L,
                        description = toBeCreatedLegalStatus.description
                    )
                )
            )

        assertThat(saveLegalStatusSlot.captured)
            .isEqualTo(
                listOf(
                    ProgrammeLegalStatus(id = 1L),
                    ProgrammeLegalStatus(id = 2L),
                    ProgrammeLegalStatus(
                        description = toBeCreatedLegalStatus.description
                    )
                )
            )

        val audit = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(audit)) }
        verify { programmeLegalStatusRepository.deleteAll(any()) }
        with(audit) {
            assertThat(captured.action).isEqualTo(AuditAction.LEGAL_STATUS_EDITED)
            assertThat(captured.description).isEqualTo("Values for partner legal status set to:\ncreated status")
        }
    }

    @Test
    fun `save new - not allowed count`() {
        every { programmeLegalStatusRepository.findAllById(eq(emptySet())) } returns emptyList()
        every { programmeLegalStatusRepository.saveAll(any<List<ProgrammeLegalStatus>>()) } returnsArgument 0
        every { programmeLegalStatusRepository.count() } returns 21

        val exception =
            assertThrows<I18nValidationException> {
                programmeLegalStatusService.save(
                    listOf(
                        InputProgrammeLegalStatus(id = 1L),
                        InputProgrammeLegalStatus(id = 2L)
                    ),
                    emptyList()
                )
            }
        assertThat(exception.i18nKey).isEqualTo("programme.legal.status.wrong.size")
    }

    @Test
    fun `delete not allowed`() {
        assertThrows<ResourceNotFoundException> {
            programmeLegalStatusService.save(
                listOf(
                    InputProgrammeLegalStatus(
                        id = 1L,
                        description = "impossible to delete"
                    )
                ),
                emptyList()
            )
        }
    }

}
