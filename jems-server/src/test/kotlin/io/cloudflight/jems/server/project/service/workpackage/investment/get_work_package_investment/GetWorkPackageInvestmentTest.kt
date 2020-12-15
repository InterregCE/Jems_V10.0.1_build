package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.investment.UnitTestWorkPackageInvestmentBase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class GetWorkPackageInvestmentTest : UnitTestWorkPackageInvestmentBase() {

    @InjectMockKs
    lateinit var getWorkPackageInvestment: GetWorkPackageInvestment

    @Test
    fun `should return the workPackageInvestment when workPackageInvestment already exists`() {
        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)
        every { persistence.getWorkPackageInvestment(workPackageInvestmentId) } returns workPackageInvestment

        val createdWorkPackageInvestment = getWorkPackageInvestment.getWorkPackageInvestment(workPackageInvestmentId)

        verify(exactly = 1) { persistence.getWorkPackageInvestment(workPackageInvestmentId) }
        confirmVerified(persistence)

        Assertions.assertEquals(workPackageInvestment, createdWorkPackageInvestment)
    }

}
