package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerControlReportExpenditureVerificationTest : UnitTest() {

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService

    @InjectMockKs
    lateinit var getExpenditure: GetProjectPartnerControlReportExpenditureVerification

    @Test
    fun getExpenditureVerification() {
        val expenditure = mockk<ProjectPartnerReportExpenditureVerification>()
        every {  sensitiveDataAuthorization.canViewPartnerSensitiveData(1L) } returns true
        every {
            reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(1L, reportId = 10L)
        } returns listOf(expenditure)

        assertThat(getExpenditure.getExpenditureVerification(1L, reportId = 10L)).containsExactly(expenditure)
    }
}
