package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment.AddWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment.DeleteWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment.GetWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments.GetWorkPackageInvestmentsInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment.UpdateWorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProjectWorkPackageInvestmentControllerTest {

    companion object {
        const val investmentId = 1L
        const val projectId = 2L
        const val workPackageId = 3L
        const val version = "2.0"

        val workPackageInvestment = WorkPackageInvestment(
            id = investmentId,
            investmentNumber = 3,
            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
            justificationExplanation = setOf(InputTranslation(SystemLanguage.EN, "justificationExplanation")),
            justificationTransactionalRelevance = setOf(InputTranslation(SystemLanguage.EN, "justificationTransactionalRelevance")),
            justificationBenefits = setOf(InputTranslation(SystemLanguage.EN, "justificationBenefits")),
            justificationPilot = setOf(InputTranslation(SystemLanguage.EN, "justificationPilot")),
            address = Address("country", "reg2", "reg3", "str", "nr", "code", "city"),
            risk = setOf(InputTranslation(SystemLanguage.EN, "risk")),
            documentation = setOf(InputTranslation(SystemLanguage.EN, "documentation")),
            ownershipSiteLocation = setOf(InputTranslation(SystemLanguage.EN, "ownershipSiteLocation")),
            ownershipRetain = setOf(InputTranslation(SystemLanguage.EN, "ownershipRetain")),
            ownershipMaintenance = setOf(InputTranslation(SystemLanguage.EN, "ownershipMaintenance"))
        )
        val workPackageInvestmentDto = WorkPackageInvestmentDTO(
            id = investmentId,
            investmentNumber = workPackageInvestment.investmentNumber,
            title = workPackageInvestment.title,
            justificationExplanation = workPackageInvestment.justificationExplanation,
            justificationTransactionalRelevance = workPackageInvestment.justificationTransactionalRelevance,
            justificationBenefits = workPackageInvestment.justificationBenefits,
            justificationPilot = workPackageInvestment.justificationPilot,
            address = workPackageInvestment.address?.toAddressDTO(),
            risk = workPackageInvestment.risk,
            documentation = workPackageInvestment.documentation,
            ownershipSiteLocation = workPackageInvestment.ownershipSiteLocation,
            ownershipRetain = workPackageInvestment.ownershipRetain,
            ownershipMaintenance = workPackageInvestment.ownershipMaintenance
        )
    }

    @MockK
    lateinit var getWorkPackageInvestmentsInteractor: GetWorkPackageInvestmentsInteractor
    @MockK
    lateinit var addWorkPackageInvestmentInteractor: AddWorkPackageInvestmentInteractor
    @MockK
    lateinit var updateWorkPackageInvestment: UpdateWorkPackageInvestment
    @MockK
    lateinit var getWorkPackageInvestmentInteractor: GetWorkPackageInvestmentInteractor
    @MockK
    lateinit var deleteWorkPackageInvestmentInteractor: DeleteWorkPackageInvestmentInteractor

    @InjectMockKs
    private lateinit var controller: ProjectWorkPackageInvestmentController

    @Test
    fun `get work package investment`() {
        every { getWorkPackageInvestmentInteractor.getWorkPackageInvestment(projectId, investmentId) } returns workPackageInvestment

        assertThat(controller.getWorkPackageInvestment(investmentId, projectId, workPackageId)).isEqualTo(workPackageInvestmentDto)
    }

    @Test
    fun `get work package investment with version`() {
        every { getWorkPackageInvestmentInteractor.getWorkPackageInvestment(projectId, investmentId, version) } returns workPackageInvestment

        assertThat(controller.getWorkPackageInvestment(investmentId, projectId, workPackageId, version)).isEqualTo(workPackageInvestmentDto)
    }

    @Test
    fun `get work package investments`() {
        every { getWorkPackageInvestmentsInteractor.getWorkPackageInvestments(projectId, workPackageId) } returns listOf(workPackageInvestment)

        assertThat(controller.getWorkPackageInvestments(projectId, workPackageId)).containsExactly(workPackageInvestmentDto)
    }

    @Test
    fun `add work package investment`() {
        every { addWorkPackageInvestmentInteractor.addWorkPackageInvestment(projectId, workPackageId, workPackageInvestment) } returns 1L

        assertThat(controller.addWorkPackageInvestment(projectId, workPackageId, workPackageInvestmentDto)).isEqualTo(1L)
    }

    @Test
    fun `delete work package investment`() {
        every { deleteWorkPackageInvestmentInteractor.deleteWorkPackageInvestment(projectId, workPackageId, investmentId) } returns Unit

        controller.deleteWorkPackageInvestment(investmentId, projectId, workPackageId)
        verify { deleteWorkPackageInvestmentInteractor.deleteWorkPackageInvestment(projectId, workPackageId, investmentId) }
    }

    @Test
    fun `update work package investment`() {
        every { updateWorkPackageInvestment.updateWorkPackageInvestment(projectId, workPackageId, workPackageInvestment) } returns Unit

        controller.updateWorkPackageInvestment(projectId, workPackageId, workPackageInvestmentDto)
        verify { updateWorkPackageInvestment.updateWorkPackageInvestment(projectId, workPackageId, workPackageInvestment) }
    }
}
