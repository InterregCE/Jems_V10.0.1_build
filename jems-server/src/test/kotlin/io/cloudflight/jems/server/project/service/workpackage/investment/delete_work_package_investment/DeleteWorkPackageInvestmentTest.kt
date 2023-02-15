package io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment

import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.workpackage.investment.UnitTestWorkPackageInvestmentBase
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class DeleteWorkPackageInvestmentTest : UnitTestWorkPackageInvestmentBase() {

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var deleteWorkPackageInvestment: DeleteWorkPackageInvestment

    @BeforeEach
    fun reset() { clearMocks(projectPersistence, persistence) }

    @ParameterizedTest(name = "should delete the workPackageInvestment {0}")
    @EnumSource(value = ApplicationStatus::class, names = [
        "CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"
    ], mode = EnumSource.Mode.EXCLUDE)
    fun `should delete the workPackageInvestment`(status: ApplicationStatus) {
        every { projectPersistence.getApplicantAndStatusById(projectId).projectStatus } returns status

        val expectedWorkPackageInvestmentId: Long = status.ordinal.toLong()
        every { persistence.deleteWorkPackageInvestment(workPackageId, expectedWorkPackageInvestmentId) } returns Unit

        deleteWorkPackageInvestment.deleteWorkPackageInvestment(1L, workPackageId, expectedWorkPackageInvestmentId)

        verify(exactly = 1) { persistence.deleteWorkPackageInvestment(workPackageId, expectedWorkPackageInvestmentId) }
        verify(exactly = 0) { persistence.deactivateWorkPackageInvestment(workPackageId, expectedWorkPackageInvestmentId) }
        confirmVerified(persistence)
    }

    @ParameterizedTest(name = "should deactivate the workPackageInvestment {0}")
    @EnumSource(value = ApplicationStatus::class, names = [
        "CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"
    ])
    fun `should deactivate the workPackageInvestment`(status: ApplicationStatus) {
        every { projectPersistence.getApplicantAndStatusById(projectId).projectStatus } returns status

        val expectedWorkPackageInvestmentId: Long = status.ordinal.toLong()
        every { persistence.deactivateWorkPackageInvestment(workPackageId, expectedWorkPackageInvestmentId) } returns Unit

        deleteWorkPackageInvestment.deleteWorkPackageInvestment(1L, workPackageId, expectedWorkPackageInvestmentId)

        verify(exactly = 1) { persistence.deactivateWorkPackageInvestment(workPackageId, expectedWorkPackageInvestmentId) }
        verify(exactly = 0) { persistence.deleteWorkPackageInvestment(workPackageId, expectedWorkPackageInvestmentId) }
        confirmVerified(persistence)
    }

}
