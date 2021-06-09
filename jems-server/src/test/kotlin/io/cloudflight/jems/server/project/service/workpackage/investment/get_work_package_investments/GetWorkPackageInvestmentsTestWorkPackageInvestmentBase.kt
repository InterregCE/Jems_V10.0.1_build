package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments

import io.cloudflight.jems.server.project.service.workpackage.investment.UnitTestWorkPackageInvestmentBase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl

internal class GetWorkPackageInvestmentsTestWorkPackageInvestmentBase : UnitTestWorkPackageInvestmentBase() {

    @InjectMockKs
    lateinit var getWorkPackageInvestments: GetWorkPackageInvestments

    @Test
    fun `should return a page of workPackageInvestments for the specified workPackage when workPackage already exists`() {

        val expectedResult = listOf(createWorkPackageInvestment(), createWorkPackageInvestment())
        every { persistence.getWorkPackageInvestments(workPackageId) } returns expectedResult

        val workPackageInvestments = getWorkPackageInvestments.getWorkPackageInvestments(workPackageId,)

        verify(exactly = 1) { persistence.getWorkPackageInvestments(workPackageId) }
        confirmVerified(persistence)

        Assertions.assertEquals(expectedResult, workPackageInvestments)

    }
}
