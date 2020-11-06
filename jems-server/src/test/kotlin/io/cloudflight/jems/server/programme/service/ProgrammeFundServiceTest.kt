package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeFund
import io.cloudflight.jems.api.programme.dto.OutputProgrammeFund
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.entity.ProgrammeFund
import io.cloudflight.jems.server.programme.repository.ProgrammeFundRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ProgrammeFundServiceTest {

    companion object {
        val programmeFund = ProgrammeFund(
            id = 1L,
            abbreviation = "1st Fund",
            description = "1st Fund description",
            selected = false
        )
        val  outputProgrammeFund = OutputProgrammeFund(
            id = programmeFund.id,
            abbreviation = programmeFund.abbreviation,
            description = programmeFund.description,
            selected = programmeFund.selected
        )
    }
    @MockK
    lateinit var programmeFundRepository: ProgrammeFundRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var programmeFundService: ProgrammeFundService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeFundService = ProgrammeFundServiceImpl(
            programmeFundRepository,
            auditService
        )
    }

    @Test
    fun get() {
        every { programmeFundRepository.findAll() } returns listOf(programmeFund)
        assertThat(programmeFundService.get()).isEqualTo(listOf(OutputProgrammeFund(id = programmeFund.id, selected = programmeFund.selected)))
    }

    @Test
    fun `update existing - select`() {
        every { programmeFundRepository.findAllById(eq(setOf(1L))) } returns listOf(programmeFund)
        every { programmeFundRepository.saveAll(any<List<ProgrammeFund>>()) } returnsArgument 0
        val expectedResult = listOf(programmeFund.copy(selected = true))
        every { programmeFundRepository.count() } returns expectedResult.size.toLong()
        every { programmeFundRepository.findAll() } returns expectedResult

        val existingFundToSelect = InputProgrammeFund(id = 1L, selected = true)
        val result = programmeFundService.update(listOf(existingFundToSelect))
        assertThat(result).isEqualTo(listOf(OutputProgrammeFund(id = 1L, selected = true)))

        val audit = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(audit)) }
        with(audit) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_FUNDS_CHANGED)
            assertThat(captured.description).isEqualTo("Programme funds has been set to:\n1st Fund")
        }
    }

    @Test
    fun `select new`() {
        val toBeCreatedFund = InputProgrammeFund(
            abbreviation = "created fund",
            description = "created fund",
            selected = true
        )
        val expectedResult = listOf(ProgrammeFund(
            id = 10L,
            abbreviation = toBeCreatedFund.abbreviation,
            description = toBeCreatedFund.description,
            selected = toBeCreatedFund.selected
        ))

        every { programmeFundRepository.findAllById(eq(emptySet())) } returns emptyList()
        val saveFundSlot = slot<List<ProgrammeFund>>()
        every { programmeFundRepository.saveAll(capture(saveFundSlot)) } returns expectedResult
        every { programmeFundRepository.count() } returns expectedResult.size.toLong()
        every { programmeFundRepository.findAll() } returns expectedResult

        val result = programmeFundService.update(listOf(toBeCreatedFund))
        assertThat(result)
            .isEqualTo(listOf(OutputProgrammeFund(
                id = 10L,
                abbreviation = toBeCreatedFund.abbreviation,
                description = toBeCreatedFund.description,
                selected = toBeCreatedFund.selected
            )))

        assertThat(saveFundSlot.captured)
            .isEqualTo(listOf(ProgrammeFund(
                abbreviation = toBeCreatedFund.abbreviation,
                description = toBeCreatedFund.description,
                selected = toBeCreatedFund.selected
            )))

        val audit = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(audit)) }
        with(audit) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_FUNDS_CHANGED)
            assertThat(captured.description).isEqualTo("Programme funds has been set to:\ncreated fund")
        }
    }

    @Test
    fun `select new - not allowed count`() {
        every { programmeFundRepository.findAllById(eq(emptySet())) } returns emptyList()
        every { programmeFundRepository.saveAll(any<List<ProgrammeFund>>()) } returnsArgument 0
        every { programmeFundRepository.count() } returns 21

        val exception = assertThrows<I18nValidationException> { programmeFundService.update(emptyList()) }
        assertThat(exception.i18nKey).isEqualTo("programme.fund.wrong.size")
    }

}
