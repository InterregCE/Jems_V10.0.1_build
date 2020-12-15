package io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.investment.UnitTestWorkPackageInvestmentBase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AddWorkPackageInvestmentTestWorkPackageInvestmentBase : UnitTestWorkPackageInvestmentBase() {

    @InjectMockKs
    lateinit var addWorkPackageInvestment: AddWorkPackageInvestment

    @Test
    fun `should add a workPackageInvestment to the specified workPackage and return the UUID of newly created workPackageInvestment when workPackage already exists`() {

        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)
        every { persistence.addWorkPackageInvestment(workPackageId, any()) } returns workPackageInvestmentId

        val createdWorkPackageInvestmentId = addWorkPackageInvestment.addWorkPackageInvestment(workPackageId, workPackageInvestment)

        verify(exactly = 1) { persistence.addWorkPackageInvestment(workPackageId, any()) }
        confirmVerified(persistence)

        assertEquals(workPackageInvestmentId, createdWorkPackageInvestmentId)
    }

}
