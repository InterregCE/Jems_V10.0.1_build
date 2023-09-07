package io.cloudflight.jems.server.project.controller.report.project.verification.overview

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.CertificateVerificationDeductionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceBreakdownSplitLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceFundDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.VerificationDeductionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.VerificationDeductionOverviewRowDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.VerificationWorkOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.VerificationWorkOverviewLineDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.CertificateVerificationDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.VerificationDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.VerificationDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverviewLine
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getDeductionOverview.GetProjectReportVerificationDeductionOverviewInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.GetProjectReportFinancingSourceBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview.GetProjectReportVerificationWorkOverviewInteractor
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportVerificationOverviewControllerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 526L
        private const val REPORT_ID = 606L

        val ERDF = ProgrammeFund(
            id = 1L, type = ProgrammeFundType.ERDF, selected = true,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN ERDF"
                ),
                InputTranslation(SystemLanguage.SK, "SK ERDF")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            )
        )
        val NDCI = ProgrammeFund(id = 5L, type = ProgrammeFundType.NDICI, selected = true,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN NDCI"
                ),
                InputTranslation(SystemLanguage.SK, "SK NDCI")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            ))
        val IPA_III = ProgrammeFund(id = 4L, type = ProgrammeFundType.IPA_III, selected = true,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN IPA_III"
                ),
                InputTranslation(SystemLanguage.SK, "SK IPA_III")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            ))

        private val fundsSorted = listOf(
            Pair(ERDF, 74999.97.toScaledBigDecimal()),
            Pair(NDCI, 24999.99.toScaledBigDecimal()),
            Pair(IPA_III, 6249.99.toScaledBigDecimal())
        )

        private val splits = listOf(
            FinancingSourceBreakdownSplitLine(
                fundId = 1L,
                value = 74999.97.toScaledBigDecimal(),
                partnerContribution = 13235.30.toScaledBigDecimal(),
                publicContribution = 8196.86.toScaledBigDecimal(),
                automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                privateContribution = 846.72.toScaledBigDecimal(),
                total = 88235.27.toScaledBigDecimal()
            ),
            FinancingSourceBreakdownSplitLine(
                fundId = 5L,
                value = 24999.99.toScaledBigDecimal(),
                partnerContribution = 4411.77.toScaledBigDecimal(),
                publicContribution = 2732.28.toScaledBigDecimal(),
                automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                privateContribution = 282.24.toScaledBigDecimal(),
                total = 29411.76.toScaledBigDecimal()
            ),
            FinancingSourceBreakdownSplitLine(
                fundId = 4L,
                value = 6249.99.toScaledBigDecimal(),
                partnerContribution = 1102.94.toScaledBigDecimal(),
                publicContribution = 683.07.toScaledBigDecimal(),
                automaticPublicContribution = 349.30.toScaledBigDecimal(),
                privateContribution = 70.56.toScaledBigDecimal(),
                total = 7352.93.toScaledBigDecimal()
            ),
        )

        private val financingSource = FinancingSourceBreakdownLine(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            partnerId = 1L,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            fundsSorted = fundsSorted,
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = splits
        )

        private val financingSourcesTotal = FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = fundsSorted,
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = emptyList()
        )

        private val financingSourceBreakdown = FinancingSourceBreakdown(
            sources = listOf(financingSource),
            total = financingSourcesTotal
        )

        private val verifOverviewLine = VerificationWorkOverviewLine(
            partnerId = 47L,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 17,
            partnerReportId = 95L,
            partnerReportNumber = 65,
            requestedByPartner = BigDecimal.valueOf(17L),
            requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(18L),
            inVerificationSample = BigDecimal.valueOf(19L),
            inVerificationSamplePercentage = BigDecimal.valueOf(20L),
            parked = BigDecimal.valueOf(21L),
            deductedByJs = BigDecimal.valueOf(22L),
            deductedByMa = BigDecimal.valueOf(23L),
            deducted = BigDecimal.valueOf(24L),
            afterVerification = BigDecimal.valueOf(25L),
            afterVerificationPercentage = BigDecimal.valueOf(26),
        )
        private val verifOverview = VerificationWorkOverview(
            certificates = listOf(verifOverviewLine),
            total = verifOverviewLine,
        )

        private val verifOverviewLineExpected = VerificationWorkOverviewLineDTO(
            partnerId = 47L,
            partnerRole = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerNumber = 17,
            partnerReportId = 95L,
            partnerReportNumber = 65,
            requestedByPartner = BigDecimal.valueOf(17L),
            requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(18L),
            inVerificationSample = BigDecimal.valueOf(19L),
            inVerificationSamplePercentage = BigDecimal.valueOf(20L),
            parked = BigDecimal.valueOf(21L),
            deductedByJs = BigDecimal.valueOf(22L),
            deductedByMa = BigDecimal.valueOf(23L),
            deducted = BigDecimal.valueOf(24L),
            afterVerification = BigDecimal.valueOf(25L),
            afterVerificationPercentage = BigDecimal.valueOf(26),
        )
        private val verifOverviewExpected = VerificationWorkOverviewDTO(
            certificates = listOf(verifOverviewLineExpected),
            total = verifOverviewLineExpected,
        )

        // certificates deductions overview
        val certificateVerificationDeductionOverview = CertificateVerificationDeductionOverview(
            partnerReportNumber = 1,
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            deductionOverview =  VerificationDeductionOverview(
                deductionRows = listOf(
                    VerificationDeductionOverviewRow(
                        typologyOfErrorId = 1,
                        typologyOfErrorName = "err1",
                        staffCost = BigDecimal.ZERO,
                        officeAndAdministration = BigDecimal.ZERO,
                        travelAndAccommodation = BigDecimal.ZERO,
                        externalExpertise = BigDecimal.ZERO,
                        equipment = BigDecimal.ZERO,
                        infrastructureAndWorks = BigDecimal.ZERO,
                        lumpSums = BigDecimal.valueOf(555.56),
                        unitCosts = BigDecimal.ZERO,
                        otherCosts = BigDecimal.ZERO,
                        total = BigDecimal.valueOf(555.56)
                    ),
                    VerificationDeductionOverviewRow(
                        typologyOfErrorId = 2,
                        typologyOfErrorName = "err2",
                        staffCost = BigDecimal.valueOf(200.0),
                        officeAndAdministration = BigDecimal.ZERO,
                        travelAndAccommodation = BigDecimal.ZERO,
                        externalExpertise = BigDecimal.ZERO,
                        equipment = BigDecimal.ZERO,
                        infrastructureAndWorks = BigDecimal.ZERO,
                        lumpSums = BigDecimal.ZERO,
                        unitCosts = BigDecimal.ZERO,
                        otherCosts = BigDecimal.ZERO,
                        total = BigDecimal.valueOf(200.0)
                    )
                ),
                staffCostsFlatRate = null,
                officeAndAdministrationFlatRate = null,
                travelAndAccommodationFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
                total = VerificationDeductionOverviewRow(
                    typologyOfErrorId = null,
                    typologyOfErrorName = null,
                    staffCost = BigDecimal.valueOf(200.0),
                    officeAndAdministration =  BigDecimal.ZERO,
                    travelAndAccommodation = BigDecimal.ZERO,
                    externalExpertise = BigDecimal.ZERO,
                    equipment = BigDecimal.ZERO,
                    infrastructureAndWorks = BigDecimal.ZERO,
                    lumpSums = BigDecimal.valueOf(555.56),
                    unitCosts = BigDecimal.ZERO,
                    otherCosts = BigDecimal.ZERO,
                    total = BigDecimal.valueOf(755.56)
                )
            )
        )

        val expectedRow1 =   VerificationDeductionOverviewRowDTO(
            typologyOfErrorId = 1,
            typologyOfErrorName = "err1",
            staffCost = BigDecimal.ZERO,
            officeAndAdministration = BigDecimal.ZERO,
            travelAndAccommodation = BigDecimal.ZERO,
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.valueOf(555.56),
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(555.56)
        )

        val expectedRow2 = VerificationDeductionOverviewRowDTO(
            typologyOfErrorId = 2,
            typologyOfErrorName = "err2",
            staffCost = BigDecimal.valueOf(200.0),
            officeAndAdministration = BigDecimal.ZERO,
            travelAndAccommodation = BigDecimal.ZERO,
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(200.0)
        )

        val expectedOverview = CertificateVerificationDeductionOverviewDTO(
            partnerReportNumber =1,
            partnerNumber = 1,
            partnerRole = ProjectPartnerRoleDTO.LEAD_PARTNER,
            deductionOverview =  VerificationDeductionOverviewDTO(
                deductionRows = mutableListOf(expectedRow1, expectedRow2),
                staffCostsFlatRate = null,
                officeAndAdministrationFlatRate = null,
                travelAndAccommodationFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
                total = VerificationDeductionOverviewRowDTO(
                    typologyOfErrorId = null,
                    typologyOfErrorName = null,
                    staffCost = BigDecimal.valueOf(200.0),
                    officeAndAdministration =  BigDecimal.ZERO,
                    travelAndAccommodation = BigDecimal.ZERO,
                    externalExpertise = BigDecimal.ZERO,
                    equipment = BigDecimal.ZERO,
                    infrastructureAndWorks = BigDecimal.ZERO,
                    lumpSums = BigDecimal.valueOf(555.56),
                    unitCosts = BigDecimal.ZERO,
                    otherCosts = BigDecimal.ZERO,
                    total = BigDecimal.valueOf(755.56)
                )
            )
        )


    }

    @MockK
    private lateinit var getProjectReportVerificationWorkOverview: GetProjectReportVerificationWorkOverviewInteractor
    @MockK
    private lateinit var getProjectReportFinancingSourceBreakdown: GetProjectReportFinancingSourceBreakdownInteractor
    @MockK
    private lateinit var getProjectReportDeductionOverviewInteractor: GetProjectReportVerificationDeductionOverviewInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportVerificationOverviewController

    @BeforeEach
    fun resetMocks() {
        clearMocks(
            getProjectReportVerificationWorkOverview,
            getProjectReportFinancingSourceBreakdown,
            getProjectReportDeductionOverviewInteractor
        )
    }

    @Test
    fun getDeductionBreakdown() {
        every { getProjectReportVerificationWorkOverview.get(4L) } returns verifOverview
        assertThat(controller.getDeductionBreakdown(0L, 4L)).isEqualTo(verifOverviewExpected)
    }

    @Test
    fun getFinancingSourceBreakdown() {
        val funds = listOf(
            FinancingSourceFundDTO(
                id = 1L,
                type = ProgrammeFundTypeDTO.ERDF,
                abbreviation = ERDF.abbreviation,
                amount = 74999.97.toScaledBigDecimal()
            ),
            FinancingSourceFundDTO(
                id = 5L,
                type = ProgrammeFundTypeDTO.NDICI,
                abbreviation = NDCI.abbreviation,
                amount = 24999.99.toScaledBigDecimal()
            ),
            FinancingSourceFundDTO(
                id = 4L,
                type = ProgrammeFundTypeDTO.IPA_III,
                abbreviation = IPA_III.abbreviation,
                amount = 6249.99.toScaledBigDecimal()
            )
        )

        val splits = listOf(
            FinancingSourceBreakdownSplitLineDTO(
                fundId = 1L,
                value = 74999.97.toScaledBigDecimal(),
                partnerContribution = 13235.30.toScaledBigDecimal(),
                publicContribution = 8196.86.toScaledBigDecimal(),
                automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                privateContribution = 846.72.toScaledBigDecimal(),
                total = 88235.27.toScaledBigDecimal()
            ),
            FinancingSourceBreakdownSplitLineDTO(
                fundId = 5L,
                value = 24999.99.toScaledBigDecimal(),
                partnerContribution = 4411.77.toScaledBigDecimal(),
                publicContribution = 2732.28.toScaledBigDecimal(),
                automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                privateContribution = 282.24.toScaledBigDecimal(),
                total = 29411.76.toScaledBigDecimal()
            ),
            FinancingSourceBreakdownSplitLineDTO(
                fundId = 4L,
                value = 6249.99.toScaledBigDecimal(),
                partnerContribution = 1102.94.toScaledBigDecimal(),
                publicContribution = 683.07.toScaledBigDecimal(),
                automaticPublicContribution = 349.30.toScaledBigDecimal(),
                privateContribution = 70.56.toScaledBigDecimal(),
                total = 7352.93.toScaledBigDecimal(),
            )
        )

        val financingSourceLine = FinancingSourceBreakdownLineDTO(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            partnerId = 1L,
            partnerRole = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerNumber = 1,
            fundsSorted = funds,
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = splits
        )

        val financingSourcesTotal = FinancingSourceBreakdownLineDTO(
            partnerReportId = null,
            partnerReportNumber = null,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = funds,
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = emptyList()
        )

        val expected = FinancingSourceBreakdownDTO(
            sources = listOf(financingSourceLine),
            total = financingSourcesTotal
        )

        every { getProjectReportFinancingSourceBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns financingSourceBreakdown
        assertThat(controller.getFinancingSourceBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expected)
    }

    @Test
    fun getDeductionsPerCertificateByTypologyOfError() {
        every { getProjectReportDeductionOverviewInteractor.getDeductionOverview(REPORT_ID) } returns listOf(certificateVerificationDeductionOverview)
        assertThat(
            controller.getDeductionsByTypologyOfErrors(
                projectId = PROJECT_ID,
                reportId = REPORT_ID
            )
        ).usingRecursiveComparison().isEqualTo(
            listOf(expectedOverview)
        )
    }

}
