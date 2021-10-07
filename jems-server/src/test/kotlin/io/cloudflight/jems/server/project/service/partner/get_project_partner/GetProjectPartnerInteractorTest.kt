package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.partner.toModel
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetail
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectBudgetPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

internal class GetProjectPartnerInteractorTest : UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @MockK
    lateinit var getProjectBudget: GetProjectBudget

    @InjectMockKs
    lateinit var getInteractor: GetProjectPartner

    private val UNPAGED = Pageable.unpaged()

    private val projectPartnerEntity = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
        nace = NaceGroupLevel.A,
        otherIdentifierNumber = "id-12",
        pic = "009",
        legalStatus = ProgrammeLegalStatusEntity(id = 1),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes
    )

    private val projectPartner = projectPartnerEntity.toModel()
    private val projectPartnerDetail = projectPartnerEntity.toProjectPartnerDetail()

    private val projectBudgetPartnerSummary = ProjectBudgetPartnerSummary(
        partnerSummary = projectPartner,
        totalBudget = BigDecimal.ZERO
    )
    private val partnerBudget = PartnerBudget(
        partner = projectPartner,
        staffCosts = BigDecimal.ZERO,
        travelCosts = BigDecimal.ZERO,
        externalCosts = BigDecimal.ZERO,
        equipmentCosts = BigDecimal.ZERO,
        infrastructureCosts = BigDecimal.ZERO,
        officeAndAdministrationCosts = BigDecimal.ZERO,
        otherCosts = BigDecimal.ZERO,
        lumpSumContribution = BigDecimal.ZERO,
        unitCosts = BigDecimal.ZERO,
        totalCosts = BigDecimal.ZERO,
        )

    @Test
    fun getById() {
        every { persistence.getById(-1) } throws ResourceNotFoundException("partner")
        every { persistence.getById(1) } returns projectPartnerDetail

        assertThrows<ResourceNotFoundException> { getInteractor.getById(-1) }
        Assertions.assertThat(getInteractor.getById(1)).isEqualTo(projectPartnerDetail)
    }

    @Test
    fun getByIdAndVersion() {
        every { persistence.getById(1, "404") } throws ApplicationVersionNotFoundException()
        every { persistence.getById(1, "1.0") } returns projectPartnerDetail

        assertThrows<ApplicationVersionNotFoundException> { getInteractor.getById(1, "404") }
        Assertions.assertThat(getInteractor.getById(1, "1.0")).isEqualTo(projectPartnerDetail)
    }

    @Test
    fun findAllByProjectId() {
        every { persistence.findAllByProjectId(0, UNPAGED) } returns PageImpl(emptyList())
        every { persistence.findAllByProjectId(1, UNPAGED) } returns PageImpl(mutableListOf(projectPartner))
        every { getProjectBudget.getBudget(any(), 0, any())} returns emptyList()
        every { getProjectBudget.getBudget(any(), 1, any())} returns listOf(partnerBudget)

        Assertions.assertThat(getInteractor.findAllByProjectId(0, UNPAGED)).isEmpty()
        Assertions.assertThat(getInteractor.findAllByProjectId(1, UNPAGED)).containsExactly(projectBudgetPartnerSummary)
    }

}
