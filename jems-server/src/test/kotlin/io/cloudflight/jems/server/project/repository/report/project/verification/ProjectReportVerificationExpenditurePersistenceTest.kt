package io.cloudflight.jems.server.project.repository.report.project.verification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.control.expenditure.PartnerReportParkedExpenditureEntity
import io.cloudflight.jems.server.project.entity.report.partner.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.verification.expenditure.ProjectReportVerificationExpenditureEntity
import io.cloudflight.jems.server.project.repository.report.partner.control.expenditure.PartnerReportParkedExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Optional

class ProjectReportVerificationExpenditurePersistenceTest : UnitTest() {

    companion object {
        private const val PROJECT_REPORT_ID = 10L
        private const val PROJECT_ID = 101L
        private const val PARTNER_REPORT_ID = 496L
        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private const val FIRST_EXPENDITURE_ID = 1L
        private const val EXPENDITURE_ID_1 = 2L
        private const val EXPENDITURE_ID_2 = 3L
        private const val PARTNER_ID = 54L
        val submission = ZonedDateTime.now()

        private val projectReportEntity = ProjectReportEntity(
            id = PROJECT_REPORT_ID,
            projectId = 99L,
            number = 1,
            status = ProjectReportStatus.Draft,
            applicationFormVersion = "3.0",
            startDate = LocalDate.now().minusDays(1),
            endDate = null,

            type = ContractingDeadlineType.Both,
            deadline = mockk(),
            finalReport = false,
            reportingDate = mockk(),
            periodNumber = 4,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",
            spfPartnerId = null,

            createdAt = ZonedDateTime.now().minusWeeks(1),
            firstSubmission = ZonedDateTime.now().minusYears(1),
            lastReSubmission = mockk(),
            verificationDate = null,
            verificationEndDate = null,
            verificationConclusionJs = "CONCLUSION JS",
            verificationConclusionMa = "CONCLUSION MA",
            verificationFollowup = "CONCLUSION FOLLOWUP",
            lastVerificationReOpening = mockk(),
            riskBasedVerification = false,
            riskBasedVerificationDescription = "Description"
        )

        private val indentification = PartnerReportIdentificationEntity(
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            partnerNumber = 4,
            partnerAbbreviation = "P-4",
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = null,
            nameInEnglish = null,
            legalStatus = null,
            country = null,
            currency = null,
        )

        private val partnerReport =
            ProjectPartnerReportEntity(
                id = PARTNER_REPORT_ID,
                partnerId = 72,
                number = 4,
                status = ReportStatus.Certified,
                applicationFormVersion = "v",
                firstSubmission = null,
                lastReSubmission = null,
                controlEnd = null,
                identification = indentification,
                createdAt = ZonedDateTime.now(),
                projectReport = projectReportEntity,
                lastControlReopening = null,
            )

        private val firstExpenditure = PartnerReportExpenditureCostEntity(
            id = FIRST_EXPENDITURE_ID,
            number = 1,
            partnerReport = partnerReport,
            reportLumpSum = null,
            reportUnitCost = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            reportInvestment = null,
            procurementId = 101L,
            internalReferenceNumber = "internalReferenceNumber",
            invoiceNumber = "invoiceNumber",
            invoiceDate = LAST_WEEK.toLocalDate(),
            dateOfPayment = LAST_WEEK.plusWeeks(1).toLocalDate(),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ONE,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.ONE,
            currencyCode = "currencyCode",
            currencyConversionRate = BigDecimal.ONE,
            declaredAmountAfterSubmission = BigDecimal.valueOf(1000),
            attachment = null,
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(500),
            deductedAmount = BigDecimal.valueOf(300),
            typologyOfErrorId = null,
            verificationComment = "CONTROL VERIFICATION COMMENT",
            parked = false,
            reIncludedFromExpenditure = null,
            reportOfOrigin = null,
            parkedInProjectReport = null,
            originalNumber = 1,
            partOfSampleLocked = false
        )

        private fun expenditure(id: Long, partnerReport: ProjectPartnerReportEntity) = PartnerReportExpenditureCostEntity(
            id = id,
            number = 1,
            partnerReport = partnerReport,
            reportLumpSum = null,
            reportUnitCost = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            reportInvestment = null,
            procurementId = 101L,
            internalReferenceNumber = "internalReferenceNumber",
            invoiceNumber = "invoiceNumber",
            invoiceDate = LAST_WEEK.toLocalDate(),
            dateOfPayment = LAST_WEEK.plusWeeks(1).toLocalDate(),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ONE,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.ONE,
            currencyCode = "currencyCode",
            currencyConversionRate = BigDecimal.ONE,
            declaredAmountAfterSubmission = BigDecimal.valueOf(1000),
            attachment = null,
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(500),
            deductedAmount = BigDecimal.valueOf(500),
            typologyOfErrorId = null,
            verificationComment = "CONTROL VERIFICATION COMMENT",
            parked = true,
            reIncludedFromExpenditure = firstExpenditure,
            reportOfOrigin = partnerReport,
            parkedInProjectReport = null,
            originalNumber = 1,
            partOfSampleLocked = false,
        )

        private val expendituresToUpdate = listOf(
            ProjectReportVerificationExpenditureLineUpdate(
                expenditureId = EXPENDITURE_ID_1,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                typologyOfErrorId = null,
                parked = false,
                verificationComment = "JS/MA VERIFICATION COMMENT, UNPARKED"
            ),
            ProjectReportVerificationExpenditureLineUpdate(
                expenditureId = EXPENDITURE_ID_2,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.ZERO,
                deductedByMa = BigDecimal.ZERO,
                typologyOfErrorId = null,
                parked = true,
                verificationComment = "JS/MA VERIFICATION COMMENT, PARKED"
            )
        )

        val procurementEntity = ProjectPartnerReportProcurementEntity(
            id = 101L,
            reportEntity = partnerReport,
            contractName = "",
            referenceNumber = "",
            contractDate = LocalDate.now(),
            contractType = "",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "",
            supplierName = "",
            vatNumber = "",
            lastChanged = ZonedDateTime.now().minusYears(20),
            comment = "",
        )

    }

    @MockK
    lateinit var expenditureRepository: ProjectPartnerReportExpenditureRepository

    @MockK
    lateinit var expenditureVerificationRepository: ProjectReportVerificationExpenditureRepository

    @MockK
    lateinit var procurementRepository: ProjectPartnerReportProcurementRepository

    @MockK
    lateinit var projectReportRepository: ProjectReportRepository

    @MockK
    lateinit var reportParkedExpenditureRepository: PartnerReportParkedExpenditureRepository

    @MockK
    lateinit var projectReportCertificatePersistence: ProjectReportCertificatePersistence

    @InjectMockKs
    lateinit var projectReportVerificationExpenditurePersistenceProvider: ProjectReportVerificationExpenditurePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(
            expenditureRepository,
            expenditureVerificationRepository,
            procurementRepository,
            projectReportRepository
        )
    }

    @Test
    fun getExpenditureVerificationRiskBasedDataTest() {
        val expectedRiskBased = ProjectReportVerificationRiskBased(
            projectReportId = projectReportEntity.id,
            riskBasedVerification = projectReportEntity.riskBasedVerification,
            riskBasedVerificationDescription = projectReportEntity.riskBasedVerificationDescription
        )

        every { projectReportRepository.getByIdAndProjectId(PROJECT_REPORT_ID, PROJECT_ID) } returns projectReportEntity

        assertThat(
            projectReportVerificationExpenditurePersistenceProvider.getExpenditureVerificationRiskBasedData(
                PROJECT_ID, PROJECT_REPORT_ID
            )
        ).isEqualTo(expectedRiskBased)
    }

    @Test
    fun getProjectReportExpenditureVerification() {
        val reportOfOrigin = mockk<ProjectPartnerReportEntity>()
        every { reportOfOrigin.id } returns 11L
        every { reportOfOrigin.number } returns 111

        val unParkedFrom = mockk<PartnerReportExpenditureCostEntity>()
        every { unParkedFrom.id } returns 5L

        val expenditure = expenditure(EXPENDITURE_ID_1, partnerReport)

        val expenditureVerificationEntity = ProjectReportVerificationExpenditureEntity(
            expenditure = expenditure,
            expenditureId = expenditure.id,
            partOfVerificationSample = false,
            deductedByJs = BigDecimal.valueOf(100),
            deductedByMa = BigDecimal.valueOf(200),
            amountAfterVerification = BigDecimal.valueOf(100),
            typologyOfErrorId = null,
            parked = true,
            verificationComment = "JS/MA VERIFICATION COMMENT",
        )

        val expectedProjectPartnerReportExpenditure = ProjectReportVerificationExpenditureLine(
            expenditure = expenditure.toExpenditurePart(procurementEntity),
            partOfVerificationSample = false,
            deductedByJs = BigDecimal.valueOf(100),
            deductedByMa = BigDecimal.valueOf(200),
            amountAfterVerification = BigDecimal.valueOf(100),
            typologyOfErrorId = null,
            parked = true,
            verificationComment = "JS/MA VERIFICATION COMMENT",
            parkedOn = LAST_WEEK
        )

        val certificate = mockk<ProjectPartnerReportSubmissionSummary>()
        every { certificate.partnerId } returns PARTNER_ID

        val partnerIds = setOf(certificate.partnerId)

        every { projectReportCertificatePersistence.listCertificatesOfProjectReport(PROJECT_REPORT_ID) } returns listOf(
            certificate
        )
        every { procurementRepository.findAllByReportEntityPartnerIdIn(partnerIds)  } returns listOf(procurementEntity)
        every {
            expenditureVerificationRepository
                .findAllByExpenditurePartnerReportProjectReportId(PROJECT_REPORT_ID)
        } returns listOf(expenditureVerificationEntity)

        val partnerReportParkedExpenditureEntity = mockk<PartnerReportParkedExpenditureEntity>()
        every { partnerReportParkedExpenditureEntity.parkedFromExpenditureId } returns EXPENDITURE_ID_1
        every { partnerReportParkedExpenditureEntity.parkedOn } returns LAST_WEEK

        every {reportParkedExpenditureRepository
            .findAllByParkedFromExpenditureIdIn(setOf(EXPENDITURE_ID_1))
        } returns listOf(partnerReportParkedExpenditureEntity)


        assertThat(
            projectReportVerificationExpenditurePersistenceProvider.getProjectReportExpenditureVerification(
                PROJECT_REPORT_ID
            )
        ).isEqualTo(listOf(expectedProjectPartnerReportExpenditure))
    }

    @Test
    fun saveEmptyIncludedExpendituresTest() {
        val reportOfOrigin = mockk<ProjectPartnerReportEntity>()
        every { reportOfOrigin.id } returns 11L
        every { reportOfOrigin.number } returns 111

        every { expenditureRepository.findAllByPartnerReportProjectReportId(PROJECT_REPORT_ID) } returns listOf(firstExpenditure)

        val unParkedFrom = mockk<PartnerReportExpenditureCostEntity>()
        every { unParkedFrom.id } returns 5L

        every { expenditureRepository.getReferenceById(EXPENDITURE_ID_1) } returns unParkedFrom
        every { expenditureVerificationRepository.saveAll(any() as List<ProjectReportVerificationExpenditureEntity>) } returnsArgument 0

        projectReportVerificationExpenditurePersistenceProvider.initiateEmptyVerificationForProjectReport(
            PROJECT_REPORT_ID
        )
        verify(exactly = 1) { expenditureVerificationRepository.saveAll(any() as List<ProjectReportVerificationExpenditureEntity>) }
    }

    @Test
    fun updateProjectReportExpenditureVerification() {
        val indentification = PartnerReportIdentificationEntity(
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            partnerNumber = 4,
            partnerAbbreviation = "P-4",
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = null,
            nameInEnglish = null,
            legalStatus = null,
            country = null,
            currency = null,
        )

        val reportOfOrigin = mockk<ProjectPartnerReportEntity>()
        every { reportOfOrigin.id } returns 11L
        every { reportOfOrigin.number } returns 111
        every { reportOfOrigin.identification } returns indentification

        val unParkedFrom = mockk<PartnerReportExpenditureCostEntity>()
        every { unParkedFrom.id } returns 5L

        val certificate = mockk<ProjectPartnerReportSubmissionSummary>()
        every { certificate.partnerId } returns PARTNER_ID
        val partnerIds = setOf(certificate.partnerId)

        every { projectReportCertificatePersistence.listCertificatesOfProjectReport(PROJECT_REPORT_ID) } returns listOf(
            certificate
        )
        every { procurementRepository.findAllByReportEntityPartnerIdIn(partnerIds)  } returns listOf(procurementEntity)

        val reportEntity = ProjectPartnerReportEntity(
            id = PARTNER_REPORT_ID,
            partnerId = PARTNER_ID,
            number = 1,
            status = ReportStatus.InControl,
            applicationFormVersion = "v1.0",
            identification = indentification,
            controlEnd = null,
            firstSubmission = submission,
            lastReSubmission = submission,
            projectReport = projectReportEntity,
            lastControlReopening = null
        )

        val expenditure1 = expenditure(EXPENDITURE_ID_1, reportEntity)
        val expenditure2 = expenditure(EXPENDITURE_ID_2, reportEntity)

        val expenditureVerificationEntity1 = ProjectReportVerificationExpenditureEntity(
            expenditure = expenditure1,
            expenditureId = expenditure1.id,
            partOfVerificationSample = false,
            deductedByJs = BigDecimal.valueOf(0),
            deductedByMa = BigDecimal.valueOf(0),
            amountAfterVerification = BigDecimal.valueOf(0),
            typologyOfErrorId = null,
            parked = true,
            verificationComment = "JS/MA VERIFICATION COMMENT, PARKED",
        )

        val expenditureVerificationEntity2 = ProjectReportVerificationExpenditureEntity(
            expenditure = expenditure2,
            expenditureId = expenditure2.id,
            partOfVerificationSample = false,
            deductedByJs = BigDecimal.valueOf(100),
            deductedByMa = BigDecimal.valueOf(200),
            amountAfterVerification = BigDecimal.valueOf(200),
            typologyOfErrorId = null,
            parked = false,
            verificationComment = "JS/MA VERIFICATION COMMENT, UNPARKED",
        )

        val expectedExpenditureVerification = listOf(
            ProjectReportVerificationExpenditureLine(
                expenditure = expenditure1.toExpenditurePart(procurementEntity),
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                amountAfterVerification = BigDecimal.valueOf(200),
                typologyOfErrorId = null,
                parked = false,
                verificationComment = "JS/MA VERIFICATION COMMENT, UNPARKED",
                parkedOn = null
            ),
            ProjectReportVerificationExpenditureLine(
                expenditure = expenditure2.toExpenditurePart(procurementEntity),
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(0),
                deductedByMa = BigDecimal.valueOf(0),
                amountAfterVerification = BigDecimal.valueOf(0),
                typologyOfErrorId = null,
                parked = true,
                verificationComment = "JS/MA VERIFICATION COMMENT, PARKED",
                parkedOn = null
            )
        )

        every {
            expenditureVerificationRepository
                .findAllByExpenditurePartnerReportProjectReportId(PROJECT_REPORT_ID)
        } returns listOf(expenditureVerificationEntity1, expenditureVerificationEntity2)

        assertThat(
            projectReportVerificationExpenditurePersistenceProvider.updateProjectReportExpenditureVerification(
                PROJECT_REPORT_ID, expendituresToUpdate
            )
        ).isEqualTo(expectedExpenditureVerification)

    }

    @Test
    fun updateProjectReportExpenditureVerificationRiskBased() {

        val riskBaseToUpdate = ProjectReportVerificationRiskBased(
            projectReportId = projectReportEntity.id,
            riskBasedVerification = true,
            riskBasedVerificationDescription = "NEW DESCRIPTION"
        )

        val expectedRiskBased = ProjectReportVerificationRiskBased(
            projectReportId = projectReportEntity.id,
            riskBasedVerification = true,
            riskBasedVerificationDescription = "NEW DESCRIPTION"
        )

        every { projectReportRepository.findById(PROJECT_REPORT_ID) } returns Optional.of(projectReportEntity)

        assertThat(
            projectReportVerificationExpenditurePersistenceProvider.updateProjectReportExpenditureVerificationRiskBased(
                PROJECT_REPORT_ID, riskBasedData = riskBaseToUpdate
            )
        ).isEqualTo(expectedRiskBased)
    }

}
