package io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.investment.UnitTestWorkPackageInvestmentBase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*

internal class DeleteWorkPackageInvestmentTest : UnitTestWorkPackageInvestmentBase() {

    @InjectMockKs
    lateinit var deleteWorkPackageInvestment: DeleteWorkPackageInvestment

    @Test
    fun `should delete the workPackageInvestment from the specified workPackage without any exception`() {
        val expectedWorkPackageInvestmentId: Long = Math.random().toLong()
        every { persistence.deleteWorkPackageInvestment(expectedWorkPackageInvestmentId) } returns Unit

        deleteWorkPackageInvestment.deleteWorkPackageInvestment(expectedWorkPackageInvestmentId)

        verify(exactly = 1) { persistence.deleteWorkPackageInvestment(expectedWorkPackageInvestmentId) }
        confirmVerified(persistence)
    }

}
