package io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.investment.UnitTestWorkPackageInvestmentBase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class UpdateWorkPackageInvestmentTest : UnitTestWorkPackageInvestmentBase() {

    @InjectMockKs
    lateinit var updateWorkPackageInvestment: UpdateWorkPackageInvestment

    @Test
    fun `should update the workPackageInvestment without any exception when workPackageInvestment already exists`() {
        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)

        every { persistence.updateWorkPackageInvestment(workPackageInvestment) } returns Unit

        updateWorkPackageInvestment.updateWorkPackageInvestment(projectId, workPackageInvestment)

        verify(exactly = 1) { persistence.updateWorkPackageInvestment(workPackageInvestment) }
        confirmVerified(persistence)

    }

}
