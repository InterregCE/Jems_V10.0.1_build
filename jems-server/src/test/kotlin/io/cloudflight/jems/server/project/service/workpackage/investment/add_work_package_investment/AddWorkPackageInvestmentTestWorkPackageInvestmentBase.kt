package io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.investment.UnitTestWorkPackageInvestmentBase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AddWorkPackageInvestmentTestWorkPackageInvestmentBase : UnitTestWorkPackageInvestmentBase() {

    @InjectMockKs
    lateinit var addWorkPackageInvestment: AddWorkPackageInvestment

    @Test
    fun `should add a workPackageInvestment to the specified workPackage and return the UUID of newly created workPackageInvestment when workPackage already exists`() {
        every { persistence.countWorkPackageInvestments(workPackageId) } returns 5
        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)
        every { persistence.addWorkPackageInvestment(workPackageId, any()) } returns workPackageInvestmentId

        val createdWorkPackageInvestmentId = addWorkPackageInvestment.addWorkPackageInvestment(1L, workPackageId, workPackageInvestment)

        verify(exactly = 1) { persistence.countWorkPackageInvestments(workPackageId) }
        verify(exactly = 1) { persistence.addWorkPackageInvestment(workPackageId, any()) }
        confirmVerified(persistence)

        assertEquals(workPackageInvestmentId, createdWorkPackageInvestmentId)
    }

    @Test
    fun `addWorkPackageInvestment should fail if there are already a lot of investments`() {
        every { persistence.countWorkPackageInvestments(workPackageId) } returns 20
        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)
        val ex = assertThrows<I18nValidationException> { addWorkPackageInvestment.addWorkPackageInvestment(1L, workPackageId, workPackageInvestment) }
        assertThat(ex.i18nKey).isEqualTo("project.workPackage.investment.max.allowed.reached")
    }

}
