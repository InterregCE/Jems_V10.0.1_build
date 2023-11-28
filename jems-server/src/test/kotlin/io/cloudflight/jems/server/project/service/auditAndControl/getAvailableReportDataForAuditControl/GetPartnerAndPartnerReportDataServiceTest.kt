package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFund
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableReportTmp
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionEcPayment
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionProjectReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetPartnerAndPartnerReportDataServiceTest: UnitTest() {

    private val year1 = mockk<AccountingYear>()
    private val year2 = mockk<AccountingYear>()

    private val fund1 = ProgrammeFund(
        id = 39L,
        selected = true,
        type = ProgrammeFundType.ERDF,
        abbreviation = setOf(InputTranslation(SystemLanguage.FI, "FI-39-ERDF")),
    )
    private val fund2 = ProgrammeFund(
        id = 40L,
        selected = true,
        type = ProgrammeFundType.NEIGHBOURHOOD_CBC,
        abbreviation = setOf(InputTranslation(SystemLanguage.FI, "FI-40-NEIGHBOURHOOD_CBC")),
    )

    private val report1 = CorrectionAvailableReportTmp(
        partnerId = 15L,
        id = 151L,
        reportNumber = 5,
        projectReportId = 26L,
        projectReportNumber = 12,
        availableFund = fund1,
        ecPaymentId = 647L,
        ecPaymentStatus = PaymentEcStatus.Draft,
        ecPaymentAccountingYear = year1,
    )
    private val report2 = CorrectionAvailableReportTmp(
        partnerId = 16L,
        id = 161L,
        reportNumber = 6,
        projectReportId = 36L,
        projectReportNumber = 32,
        availableFund = fund1,
        ecPaymentId = 647L,
        ecPaymentStatus = PaymentEcStatus.Draft,
        ecPaymentAccountingYear = year1,
    )
    private val report3 = CorrectionAvailableReportTmp(
        partnerId = 15L,
        id = 152L,
        reportNumber = 7,
        projectReportId = 46L,
        projectReportNumber = 22,
        availableFund = fund2,
        ecPaymentId = 648L,
        ecPaymentStatus = PaymentEcStatus.Finished,
        ecPaymentAccountingYear = year2,
    )
    private val report4 = CorrectionAvailableReportTmp(
        partnerId = 16L,
        id = 162L,
        reportNumber = 8,
        projectReportId = 56L,
        projectReportNumber = 42,
        availableFund = fund2,
        ecPaymentId = 648L,
        ecPaymentStatus = PaymentEcStatus.Finished,
        ecPaymentAccountingYear = year2,
    )

    @MockK
    private lateinit var partnerReportPersistence: ProjectPartnerReportPersistence
    @MockK
    private lateinit var partnerPersistence: PartnerPersistence
    @MockK
    private lateinit var versionPersistence: ProjectVersionPersistence

    @InjectMockKs
    private lateinit var service: GetPartnerAndPartnerReportDataService

    @Test
    fun getPartnerAndPartnerReportData() {
        every { versionPersistence.getLatestApprovedOrCurrent(75L) } returns "V4-7c"

        val partnerA = mockk<ProjectPartnerDetail> {
            every { id } returns 15L
            every { active } returns true
            every { role } returns ProjectPartnerRole.PARTNER
            every { sortNumber } returns 2
            every { abbreviation } returns "PP2 active"
        }
        val partnerB = mockk<ProjectPartnerDetail> {
            every { id } returns 16L
            every { active } returns false
            every { role } returns ProjectPartnerRole.LEAD_PARTNER
            every { sortNumber } returns 1
            every { abbreviation } returns "LP1 passive"
        }

        every { partnerPersistence.findTop50ByProjectId(75L, "V4-7c") } returns listOf(partnerA, partnerB)

        every { partnerReportPersistence.getAvailableReports(setOf(15L, 16L)) } returns listOf(
            report1, report2, report3, report4,
        )

        assertThat(service.getPartnerAndPartnerReportData(75L)).containsExactly(
            CorrectionAvailablePartner(
                partnerId = 16L,
                partnerNumber = 1,
                partnerAbbreviation = "LP1 passive",
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerDisabled = true,
                availableReports = listOf(
                    CorrectionAvailablePartnerReport(
                        id = 161L,
                        reportNumber = 6,
                        projectReport = CorrectionProjectReport(id = 36L, number = 32),
                        availableFunds = listOf(
                            CorrectionAvailableFund(fund = fund1, ecPayment = CorrectionEcPayment(647L, PaymentEcStatus.Draft, year1)),
                        ),
                    ),
                    CorrectionAvailablePartnerReport(
                        id = 162L,
                        reportNumber = 8,
                        projectReport = CorrectionProjectReport(id = 56L, number = 42),
                        availableFunds = listOf(
                            CorrectionAvailableFund(fund = fund2, ecPayment = CorrectionEcPayment(648L, PaymentEcStatus.Finished, year2)),
                        ),
                    ),
                ),
                availableFtls = emptyList(),
            ),
            CorrectionAvailablePartner(
                partnerId = 15L,
                partnerNumber = 2,
                partnerAbbreviation = "PP2 active",
                partnerRole = ProjectPartnerRole.PARTNER,
                partnerDisabled = false,
                availableReports = listOf(
                    CorrectionAvailablePartnerReport(
                        id = 151L,
                        reportNumber = 5,
                        projectReport = CorrectionProjectReport(id = 26L, number = 12),
                        availableFunds = listOf(
                            CorrectionAvailableFund(fund = fund1, ecPayment = CorrectionEcPayment(647L, PaymentEcStatus.Draft, year1)),
                        ),
                    ),
                    CorrectionAvailablePartnerReport(
                        id = 152L,
                        reportNumber = 7,
                        projectReport = CorrectionProjectReport(id = 46L, number = 22),
                        availableFunds = listOf(
                            CorrectionAvailableFund(fund = fund2, ecPayment = CorrectionEcPayment(648L, PaymentEcStatus.Finished, year2)),
                        ),
                    ),
                ),
                availableFtls = emptyList(),
            ),
        )
    }

}
