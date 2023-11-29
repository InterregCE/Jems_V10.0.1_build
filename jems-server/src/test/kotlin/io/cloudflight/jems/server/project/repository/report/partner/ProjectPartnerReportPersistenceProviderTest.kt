package io.cloudflight.jems.server.project.repository.report.partner

import com.querydsl.core.Tuple
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.BooleanOperation
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportBaseData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.QAccountingYearEntity
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.QProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.entity.report.partner.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.QProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.QProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.QProjectReportEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.report.partner.model.CertificateSummary
import io.cloudflight.jems.server.project.repository.report.partner.model.ReportSummary
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableReportTmp
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO
import java.math.BigDecimal.valueOf
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Stream

class ProjectPartnerReportPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L
        private const val PARTNER_ID_2 = 11L
        private const val PARTNER_REPORT_ID = 21L
        private const val PROJECT_REPORT_ID = 32L
        private const val EC_PAYMENT_ID = 43L
        private const val ACCOUNTING_YEAR_ID = 54L
        private val LAST_WEEK = LocalDate.now().minusWeeks(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)
        private val LAST_MONTH = ZonedDateTime.now().minusMonths(1)

        private fun reportEntity(
            id: Long,
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            controlEnd: ZonedDateTime? = null,
            status: ReportStatus = ReportStatus.Draft,
        ): ProjectPartnerReportEntity {
            val projectReport = mockk<ProjectReportEntity>()
            every { projectReport.id } returns 54L
            every { projectReport.number } returns 540
            return ProjectPartnerReportEntity(
                id = id,
                partnerId = PARTNER_ID,
                number = 1,
                status = status,
                applicationFormVersion = "3.0",
                firstSubmission = LAST_YEAR,
                lastReSubmission = LAST_MONTH,
                controlEnd = controlEnd,
                lastControlReopening = null,
                identification = PartnerReportIdentificationEntity(
                    projectIdentifier = "projectIdentifier",
                    projectAcronym = "projectAcronym",
                    partnerNumber = 4,
                    partnerAbbreviation = "partnerAbbreviation",
                    partnerRole = ProjectPartnerRole.PARTNER,
                    nameInOriginalLanguage = "nameInOriginalLanguage",
                    nameInEnglish = "nameInEnglish",
                    legalStatus = legalStatusEntity,
                    partnerType = ProjectTargetGroup.SectoralAgency,
                    vatRecovery = ProjectPartnerVatRecovery.Yes,
                    country = "Österreich (AT)",
                    currency = "EUR",
                ),
                createdAt = createdAt,
                projectReport = projectReport,
            )
        }

        private fun reportSummary(
            id: Long,
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            status: ReportStatus = ReportStatus.Draft,
            partnerId: Long = 99L
        ) = ReportSummary(
            id = id,
            number = 1,
            status = status,
            version = "3.0",
            firstSubmission = null,
            lastReSubmission = LAST_MONTH,
            controlEnd = null,
            createdAt = createdAt,
            totalEligibleAfterControl = TEN,
            totalAfterSubmitted = ONE,
            periodNumber = 2,
            startDate = LAST_WEEK,
            endDate = NEXT_WEEK,
            periodStart = 4,
            periodEnd = 6,
            periodBudget = ONE,
            periodBudgetCumulative = TEN,
            projectReportId = 54L,
            projectReportNumber = 540,
            partnerNumber = 1,
            partnerAbbreviation = "sample partner",
            projectId = 10L,
            partnerId = partnerId,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            projectCustomIdentifier = "project"
        )

        private fun draftReportSubmissionEntity(id: Long, createdAt: ZonedDateTime = ZonedDateTime.now()) = ProjectPartnerReportSubmissionSummary(
            id = id,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "3.0",
            firstSubmission = LAST_YEAR,
            controlEnd = null,
            createdAt = createdAt,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            partnerAbbreviation = "partnerAbbreviation",
            partnerNumber = 4,
            partnerRole = ProjectPartnerRole.PARTNER,
            partnerId = PARTNER_ID
        )

        private val programmeFundEntity = ProgrammeFundEntity(
            id = 1L,
            selected = true,
            type = ProgrammeFundType.ERDF,
        )

        private val programmeFund = ProgrammeFund(
            id = programmeFundEntity.id,
            selected = programmeFundEntity.selected,
            type = programmeFundEntity.type,
        )

        private val legalStatusEntity = ProgrammeLegalStatusEntity(
            id = 650L,
            type = ProgrammeLegalStatusType.PRIVATE,
        )

        private val legalStatus = ProgrammeLegalStatus(
            id = legalStatusEntity.id,
            type = legalStatusEntity.type,
        )

        private fun draftReport(id: Long, coFinancing: List<ProjectPartnerCoFinancing>) = ProjectPartnerReport(
            id = id,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "3.0",
            firstSubmission = LAST_YEAR,
            lastResubmission = LAST_MONTH,
            controlEnd = null,
            lastControlReopening = null,
            projectReportId = 54L,
            projectReportNumber = 540,
            identification = PartnerReportIdentification(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 4,
                partnerAbbreviation = "partnerAbbreviation",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "nameInOriginalLanguage",
                nameInEnglish = "nameInEnglish",
                legalStatus = legalStatus,
                partnerType = ProjectTargetGroup.SectoralAgency,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                country = "Österreich (AT)",
                currency = "EUR",
                coFinancing = coFinancing,
            )
        )

        private fun reportSummaryModel(
            id: Long,
            createdAt: ZonedDateTime,
            status: ReportStatus,
            partnerId: Long
        ) = ProjectPartnerReportSummary(
            id = id,
            reportNumber = 1,
            status = status,
            version = "3.0",
            firstSubmission = null,
            lastReSubmission = LAST_MONTH,
            createdAt = createdAt,
            controlEnd = null,
            startDate = LAST_WEEK,
            endDate = NEXT_WEEK,
            periodDetail = ProjectPartnerReportPeriod(
                number = 2,
                periodBudget = ONE,
                periodBudgetCumulative = TEN,
                start = 4,
                end = 6,
            ),
            projectReportId = 54L,
            projectReportNumber = 540,
            totalEligibleAfterControl = TEN,
            totalAfterSubmitted = ONE,
            deletable = false,
            partnerNumber = 1,
            partnerAbbreviation = "sample partner",
            projectId = 10L,
            partnerId = partnerId,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            projectCustomIdentifier = "project"
        )

        private fun coFinancingEntities(report: ProjectPartnerReportEntity) = listOf(
            ProjectPartnerReportCoFinancingEntity(
                ProjectPartnerReportCoFinancingIdEntity(report = report, fundSortNumber = 1),
                programmeFund = programmeFundEntity,
                percentage = ONE,
                percentageSpf = valueOf(15),
                total = ZERO,
                current = ONE,
                totalEligibleAfterControl = ZERO,
                previouslyReported = TEN,
                previouslyValidated = ONE,
                previouslyPaid = ONE,
                currentParked = ONE,
                currentReIncluded = ONE,
                previouslyReportedParked = ZERO,
                disabled = true,
            ),
            ProjectPartnerReportCoFinancingEntity(
                ProjectPartnerReportCoFinancingIdEntity(report = report, fundSortNumber = 1),
                programmeFund = null,
                percentage = TEN,
                percentageSpf = valueOf(35),
                total = TEN,
                current = ZERO,
                totalEligibleAfterControl = ZERO,
                previouslyReported = ONE,
                previouslyValidated = TEN,
                previouslyPaid = ZERO,
                currentParked = ONE,
                currentReIncluded = ONE,
                previouslyReportedParked = ONE,
                disabled = false,
            ),
        )

        private val coFinancing = listOf(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = programmeFund,
                percentage = ONE,
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fund = null,
                percentage = TEN,
            ),
        )

        private val accountingYearEntity = AccountingYearEntity(
            id = ACCOUNTING_YEAR_ID,
            year = 1,
            startDate = LAST_WEEK,
            endDate = NEXT_WEEK
        )

        private val expectedCorrectionAvailableReportTmp = CorrectionAvailableReportTmp(
            partnerId = PARTNER_ID,
            id = PARTNER_REPORT_ID,
            reportNumber = 1,
            projectReportId = PROJECT_REPORT_ID,
            projectReportNumber = 2,
            availableFund = programmeFund,
            ecPaymentId = EC_PAYMENT_ID,
            ecPaymentStatus = PaymentEcStatus.Draft,
            ecPaymentAccountingYear = accountingYearEntity.toModel(),
        )
    }

    @MockK
    private lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    private lateinit var partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository

    @MockK
    private lateinit var partnerRepository: ProjectPartnerRepository

    @MockK
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @InjectMockKs
    private lateinit var persistence: ProjectPartnerReportPersistenceProvider

    @Test
    fun `updateStatusAndTimes - update status and first submission`() {
        val NOW = ZonedDateTime.now()
        val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val report = reportEntity(id = 45L, YESTERDAY, null, ReportStatus.Draft)
        every { partnerReportRepository.findByIdAndPartnerId(45L, 10L) } returns report

        val submittedReport = persistence.updateStatusAndTimes(10L, 45L, ReportStatus.Submitted, NOW)

        assertThat(submittedReport).isEqualTo(
            draftReportSubmissionEntity(id = 45L, YESTERDAY).copy(
                status = ReportStatus.Submitted,
                firstSubmission = NOW,
            )
        )
        assertThat(report.lastReSubmission).isEqualTo(LAST_MONTH)
    }

    @Test
    fun `updateStatusAndTimes - update status and last submission`() {
        val NOW = ZonedDateTime.now()
        val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val report = reportEntity(id = 46L, YESTERDAY, null, ReportStatus.ReOpenInControlLast)
        every { partnerReportRepository.findByIdAndPartnerId(46L, 11L) } returns report

        val submittedReport = persistence.updateStatusAndTimes(11L, 46L, ReportStatus.InControl, null, NOW)

        assertThat(submittedReport).isEqualTo(
            draftReportSubmissionEntity(id = 46L, YESTERDAY).copy(
                status = ReportStatus.InControl,
            )
        )
        assertThat(report.firstSubmission).isEqualTo(LAST_YEAR)
        assertThat(report.lastReSubmission).isEqualTo(NOW)
    }

    @Test
    fun `updateStatusAndTimes - update status and no time change`() {
        val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val report = reportEntity(id = 47L, YESTERDAY, null, ReportStatus.Submitted)
        every { partnerReportRepository.findByIdAndPartnerId(47L, 12L) } returns report

        val startedControlReport = persistence.updateStatusAndTimes(12L, 47L, ReportStatus.InControl, null, null)

        assertThat(startedControlReport).isEqualTo(
            draftReportSubmissionEntity(id = 47L, YESTERDAY).copy(status = ReportStatus.InControl)
        )
        assertThat(report.firstSubmission).isEqualTo(LAST_YEAR)
        assertThat(report.lastReSubmission).isEqualTo(LAST_MONTH)
    }

    @Test
    fun finalizeControlOnReportById() {
        val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val report = reportEntity(id = 48L, LAST_YEAR, null, ReportStatus.InControl)
        every { partnerReportRepository.findByIdAndPartnerId(48L, 16L) } returns report

        assertThat(persistence.finalizeControlOnReportById(16L, 48L, YESTERDAY)).isEqualTo(
            draftReportSubmissionEntity(id = 48L, LAST_YEAR).copy(
                status = ReportStatus.Certified,
                controlEnd = YESTERDAY,
            )
        )
    }

    @Test
    fun getPartnerReportStatusAndVersion() {
        val report = reportEntity(id = 49L, LAST_YEAR, null)
        every { partnerReportRepository.findByIdAndPartnerId(49L, 16L) } returns report

        assertThat(persistence.getPartnerReportStatusAndVersion(16L, 49L)).isEqualTo(
            ProjectPartnerReportStatusAndVersion(
                reportId = 49L,
                status = ReportStatus.Draft,
                version = "3.0",
            )
        )
    }

    @Test
    fun `getPartnerReportByProjectIdAndId - projectId fits`() {
        val report = reportEntity(id = 50L, LAST_YEAR, null)
        every { partnerReportRepository.getById(50L) } returns report
        every { partnerRepository.getProjectIdForPartner(PARTNER_ID) } returns 777L

        assertThat(persistence.getPartnerReportByProjectIdAndId(777L, 50L)).isEqualTo(
            ProjectPartnerReportStatusAndVersion(
                reportId = 50L,
                status = ReportStatus.Draft,
                version = "3.0",
            )
        )
    }

    @Test
    fun `getPartnerReportByProjectIdAndId - projectId does NOT fit`() {
        val report = reportEntity(id = 51L, LAST_YEAR, null)
        every { partnerReportRepository.getById(51L) } returns report
        every { partnerRepository.getProjectIdForPartner(PARTNER_ID) } returns -1L // important part here

        assertThat(persistence.getPartnerReportByProjectIdAndId(777L, 51L)).isNull()
    }

    @Test
    fun getPartnerReportStatusById() {
        val report = reportEntity(id = 75L)
        every { partnerReportRepository.findByIdAndPartnerId(75L, 20L) } returns report
        assertThat(persistence.getPartnerReportStatusAndVersion(partnerId = 20L, reportId = 75L))
            .isEqualTo(ProjectPartnerReportStatusAndVersion(75L, ReportStatus.Draft, "3.0"))
    }

    @Test
    fun getPartnerReportById() {
        val report = reportEntity(id = 35L)
        every { partnerReportRepository.findByIdAndPartnerId(35L, 10L) } returns report
        every { partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(35L) } returns
                coFinancingEntities(report)

        assertThat(persistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = 35L))
            .isEqualTo(draftReport(id = 35L, coFinancing = coFinancing))
    }

    @Test
    fun getAllPartnerReportsBaseDataByProjectId() {
        val streamData = Stream.of(
            ProjectPartnerReportBaseData(80L, 75L, "v1.0", 1),
            ProjectPartnerReportBaseData(81L, 75L, "v1.0", 2),
            ProjectPartnerReportBaseData(82L, 76L, "v1.0", 1),
        )

        val sequenceData = sequenceOf(
            ProjectPartnerReportBaseData(80L, 75L, "v1.0", 1),
            ProjectPartnerReportBaseData(81L, 75L, "v1.0", 2),
            ProjectPartnerReportBaseData(82L, 76L, "v1.0", 1),
        )
        every { partnerReportRepository.findAllPartnerReportsBaseDataByProjectId(75L) } returns streamData
        assertThat(persistence.getAllPartnerReportsBaseDataByProjectId(75L).toList())
            .isEqualTo(sequenceData.toList())
    }

    @Test
    fun listPartnerReports() {
        val twoWeeksAgo = ZonedDateTime.now().minusDays(14)

        every {
            partnerReportRepository.findAllByPartnerIdInAndStatusIn(
                setOf(PARTNER_ID),
                ReportStatus.values().toSet(),
                Pageable.unpaged()
            )
        } returns
                PageImpl(listOf(reportSummary(id = 18L, createdAt = twoWeeksAgo)))

        assertThat(persistence.listPartnerReports(setOf(PARTNER_ID), ReportStatus.values().toSet(), Pageable.unpaged()).content)
            .containsExactly(reportSummaryModel(id = 18L, createdAt = twoWeeksAgo, status = ReportStatus.Draft, partnerId = 99L))
    }

    @Test
    fun listPartnerReportsMine() {
        val partnerIds = setOf(99L, 100L)
        val twoWeeksAgo = ZonedDateTime.now().minusDays(14)

        every {
            partnerReportRepository.findAllByPartnerIdInAndStatusIn(
                partnerIds,
                ReportStatus.FINANCIALLY_CLOSED_STATUSES,
                Pageable.unpaged()
            )
        } returns PageImpl(
            listOf(
                reportSummary(101L, createdAt = twoWeeksAgo, status = ReportStatus.Submitted, partnerId = 99L),
                reportSummary(102L, createdAt = twoWeeksAgo, status = ReportStatus.Certified, partnerId = 100L)
            )
        )

        assertThat(persistence.listPartnerReports(partnerIds, ReportStatus.FINANCIALLY_CLOSED_STATUSES, Pageable.unpaged()).content)
            .containsExactly(
                reportSummaryModel(id = 101L, createdAt = twoWeeksAgo, status = ReportStatus.Submitted, partnerId = 99L),
                reportSummaryModel(id = 102L, createdAt = twoWeeksAgo, status = ReportStatus.Certified, partnerId = 100L)
            )
    }

    @Test
    fun listCertificates() {
        val now = Instant.now()
        val certificate = mockk<CertificateSummary>()
        every { certificate.partnerReportId } returns 845L
        every { certificate.partnerReportNumber } returns 22
        every { certificate.partnerId } returns 252L
        every { certificate.partnerRole } returns ProjectPartnerRole.LEAD_PARTNER
        every { certificate.partnerNumber } returns 250
        every { certificate.totalEligibleAfterControl } returns valueOf(7984L, 2)
        every { certificate.controlEnd } returns now
        every { certificate.projectReportId } returns 887L
        every { certificate.projectReportNumber } returns 15

        every { partnerReportRepository.findAllCertificates(partnerIds = setOf(PARTNER_ID), Pageable.unpaged()) } returns
                PageImpl(listOf(certificate))

        assertThat(persistence.listCertificates(setOf(PARTNER_ID), Pageable.unpaged())).containsExactly(
            PartnerReportCertificate(
                partnerReportId = 845L,
                partnerReportNumber = 22,
                partnerId = 252L,
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerNumber = 250,
                totalEligibleAfterControl = valueOf(7984L, 2),
                controlEnd = now.atZone(ZoneId.systemDefault()),
                projectReportId = 887L,
                projectReportNumber = 15,
            ),
        )
    }

    @Test
    fun getSubmittedPartnerReportIds() {
        val report18 = mockk<ProjectPartnerReportEntity>()
        every { report18.id } returns 18L
        every { report18.status } returns ReportStatus.InControl
        every { report18.applicationFormVersion } returns "AFv2"
        every {
            partnerReportRepository
                .findAllByPartnerIdAndStatusInOrderByNumberDesc(
                    PARTNER_ID, setOf(
                        // it's important to verify those statuses, as they are considered as "closed" financially-wise
                        ReportStatus.Submitted,
                        ReportStatus.ReOpenSubmittedLimited,
                        ReportStatus.InControl,
                        ReportStatus.ReOpenInControlLimited,
                        ReportStatus.Certified,
                        ReportStatus.ReOpenCertified,
                    )
                )
        } returns listOf(report18)
        assertThat(persistence.getSubmittedPartnerReports(PARTNER_ID)).containsExactly(
            ProjectPartnerReportStatusAndVersion(18L, ReportStatus.InControl, "AFv2")
        )
    }

    @Test
    fun exists() {
        every { partnerReportRepository.existsByPartnerIdAndId(PARTNER_ID, 25L) } returns false
        assertThat(persistence.exists(PARTNER_ID, 25L)).isFalse
    }

    @Test
    fun existsByStatusIn() {
        val statuses = mockk<Set<ReportStatus>>()
        every { partnerReportRepository.existsByPartnerIdAndStatusIn(PARTNER_ID, statuses) } returns true
        assertThat(persistence.existsByStatusIn(PARTNER_ID, statuses)).isTrue()
    }

    @Test
    fun getCurrentLatestReportForPartner() {
        val report = reportEntity(id = 48L)
        every { partnerReportRepository.findFirstByPartnerIdOrderByIdDesc(PARTNER_ID) } returns report
        assertThat(persistence.getCurrentLatestReportForPartner(PARTNER_ID)).isEqualTo(draftReport(48L, emptyList()))
    }

    @Test
    fun countForPartner() {
        every { partnerReportRepository.countAllByPartnerId(PARTNER_ID) } returns 24
        assertThat(persistence.countForPartner(PARTNER_ID)).isEqualTo(24)
    }

    @Test
    fun deletePartnerReportById() {
        every { partnerReportRepository.deleteById(PARTNER_ID) } answers {}
        persistence.deletePartnerReportById(PARTNER_ID)
        verify(exactly = 1) { partnerReportRepository.deleteById(PARTNER_ID) }
    }

    @Test
    fun getAvailableReports() {

        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns query
        val slotFrom = slot<EntityPath<Any>>()
        every { query.from(capture(slotFrom)) } returns query
        val slotLeftJoin = mutableListOf<EntityPath<Any>>()
        every { query.leftJoin(capture(slotLeftJoin)) } returns query
        val slotLeftJoinOn = mutableListOf<BooleanOperation>()
        every { query.on(capture(slotLeftJoinOn)) } returns query
        val slotWhere = slot<BooleanOperation>()
        every { query.where(capture(slotWhere)) } returns query
        every { query.groupBy(any()) } returns query

        val tuple = mockk<Tuple>()
        every { tuple.get(0, Long::class.java) } returns PARTNER_ID
        every { tuple.get(1, Long::class.java) } returns PARTNER_REPORT_ID
        every { tuple.get(2, Int::class.java) } returns 1 // report number
        every { tuple.get(3, Long::class.java) } returns PROJECT_REPORT_ID
        every { tuple.get(4, Int::class.java) } returns 2
        every { tuple.get(5, ProgrammeFundEntity::class.java) } returns programmeFundEntity
        every { tuple.get(6, Long::class.java) } returns EC_PAYMENT_ID
        every { tuple.get(7, PaymentEcStatus::class.java) } returns PaymentEcStatus.Draft
        every { tuple.get(8, AccountingYearEntity::class.java) } returns accountingYearEntity

        val result = mockk<List<Tuple>>()
        every { result.size } returns 1
        every { query.fetch() } returns listOf(tuple)

        assertThat(persistence.getAvailableReports(setOf(PARTNER_ID, PARTNER_ID_2)))
            .isEqualTo(listOf(expectedCorrectionAvailableReportTmp))

        assertThat(slotFrom.captured).isInstanceOf(QProjectPartnerReportEntity::class.java)
        assertThat(slotLeftJoin.size).isEqualTo(7)
        assertThat(slotLeftJoin[0]).isInstanceOf(QProjectReportEntity::class.java)
        assertThat(slotLeftJoinOn[0].toString()).isEqualTo("projectReportEntity = projectPartnerReportEntity.projectReport")
        assertThat(slotLeftJoin[1]).isInstanceOf(QProjectPartnerReportCoFinancingEntity::class.java)
        assertThat(slotLeftJoinOn[1].toString())
            .isEqualTo("projectPartnerReportCoFinancingEntity.id.report = projectPartnerReportEntity && " +
                    "projectPartnerReportCoFinancingEntity.programmeFund is not null")
        assertThat(slotLeftJoin[2]).isInstanceOf(QProgrammeFundEntity::class.java)
        assertThat(slotLeftJoinOn[2].toString()).isEqualTo("programmeFundEntity = projectPartnerReportCoFinancingEntity.programmeFund")
        assertThat(slotLeftJoin[3]).isInstanceOf(QPaymentEntity::class.java)
        assertThat(slotLeftJoinOn[3].toString()).isEqualTo("paymentEntity.fund = programmeFundEntity && paymentEntity.projectReport = projectReportEntity")
        assertThat(slotLeftJoin[4]).isInstanceOf(QPaymentToEcExtensionEntity::class.java)
        assertThat(slotLeftJoinOn[4].toString()).isEqualTo("paymentToEcExtensionEntity.payment = paymentEntity")
        assertThat(slotLeftJoin[5]).isInstanceOf(QPaymentApplicationToEcEntity::class.java)
        assertThat(slotLeftJoinOn[5].toString()).isEqualTo("paymentApplicationToEcEntity = paymentToEcExtensionEntity.paymentApplicationToEc")
        assertThat(slotLeftJoin[6]).isInstanceOf(QAccountingYearEntity::class.java)
        assertThat(slotLeftJoinOn[6].toString()).isEqualTo("accountingYearEntity = paymentApplicationToEcEntity.accountingYear")
        assertThat(slotWhere.captured.toString()).isEqualTo(
            "projectPartnerReportEntity.partnerId in [10, 11] && projectPartnerReportEntity.controlEnd is not null"
        )

    }
}
