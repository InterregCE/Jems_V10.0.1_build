package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.programme.dto.OutputProgrammeFund
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.cofinancing.InputProjectPartnerCoFinancing
import io.cloudflight.jems.api.project.dto.partner.cofinancing.OutputProjectPartnerCoFinancing
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeFund
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.call
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

internal class ProjectPartnerCoFinancingServiceTest {

    companion object {

        private val fund = ProgrammeFund(id = 1, selected = true)

        private val callWithFunds = call.copy(funds = setOf(fund))

        private val projectPartner = ProjectPartner(
            id = 1,
            project = project.copy(call = callWithFunds),
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER,
            legalStatus = ProgrammeLegalStatus(1, "test")
        )

        private val outputFund = OutputProgrammeFund(
            id = fund.id,
            selected = fund.selected
        )
    }

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    lateinit var projectPartnerCoFinancingService: ProjectPartnerCoFinancingService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectPartnerCoFinancingService = ProjectPartnerCoFinancingServiceImpl(
            projectPartnerRepository
        )
    }

    @Test
    fun `update not existing partner`() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()

        val ex = assertThrows<ResourceNotFoundException> {
            projectPartnerCoFinancingService.updatePartnerCoFinancing(-1, emptySet())
        }
        assertThat(ex.entity).isEqualTo("projectPartner")
    }

    @Test
    fun `update financing forbidden or not-existing fund`() {
        every { projectPartnerRepository.findById(projectPartner.id) } returns Optional.of(projectPartner)

        val toSave = setOf(
            InputProjectPartnerCoFinancing(fundId = -1, percentage = 20),
            InputProjectPartnerCoFinancing(fundId = null, percentage = 80)
        )
        val ex = assertThrows<I18nValidationException> {
            projectPartnerCoFinancingService.updatePartnerCoFinancing(projectPartner.id, toSave)
        }
        assertThat(ex.i18nKey).isEqualTo("project.partner.coFinancing.fundId.not.allowed.for.call")
    }

    @Test
    fun `update financing OK`() {
        every { projectPartnerRepository.findById(projectPartner.id) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.save(any<ProjectPartner>()) } returnsArgument 0

        val toSave = setOf(
            InputProjectPartnerCoFinancing(fundId = 1, percentage = 20),
            InputProjectPartnerCoFinancing(fundId = null, percentage = 80)
        )
        val result = projectPartnerCoFinancingService.updatePartnerCoFinancing(projectPartner.id, toSave)

        assertThat(result.financing).containsExactlyInAnyOrder(
            OutputProjectPartnerCoFinancing(percentage = 20, fund = outputFund),
            OutputProjectPartnerCoFinancing(percentage = 80, fund = null)
        )
    }

}
