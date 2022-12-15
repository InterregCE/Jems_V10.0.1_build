package io.cloudflight.jems.server.project.repository.report.partner

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.entity.report.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.repository.report.partner.model.ReportSummary
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
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectPartnerReportPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L
        private val LAST_WEEK = LocalDate.now().minusWeeks(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)

        private fun reportEntity(
            id: Long,
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            controlEnd: ZonedDateTime? = null,
            status: ReportStatus = ReportStatus.Draft,
            total: BigDecimal? = TEN,
        ) = ProjectPartnerReportEntity(
            id = id,
            partnerId = PARTNER_ID,
            number = 1,
            status = status,
            applicationFormVersion = "3.0",
            firstSubmission = LAST_YEAR,
            controlEnd = controlEnd,
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
            totalEligibleAfterControl = total,
        )

        private fun reportSummary(
            id: Long,
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            status: ReportStatus = ReportStatus.Draft,
        ) = ReportSummary(
            id = id,
            number = 1,
            status = status,
            version = "3.0",
            firstSubmission = null,
            controlEnd = null,
            createdAt = createdAt,
            totalEligibleAfterControl = TEN,
            periodNumber = 2,
            startDate = LAST_WEEK,
            endDate = NEXT_WEEK,
            periodStart = 4,
            periodEnd = 6,
            periodBudget = ONE,
            periodBudgetCumulative = TEN,
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
            partnerNumber = 4,
            partnerRole = ProjectPartnerRole.PARTNER,
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

        private fun draftReportSummary(id: Long, createdAt: ZonedDateTime) = ProjectPartnerReportSummary(
            id = id,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "3.0",
            firstSubmission = null,
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
            totalEligibleAfterControl = TEN,
            deletable = false,
        )

        private fun coFinancingEntities(report: ProjectPartnerReportEntity) = listOf(
            ProjectPartnerReportCoFinancingEntity(
                ProjectPartnerReportCoFinancingIdEntity(report = report, fundSortNumber = 1),
                programmeFund = programmeFundEntity,
                percentage = ONE,
                total = ZERO,
                current = ONE,
                totalEligibleAfterControl = ZERO,
                previouslyReported = TEN,
                previouslyPaid = ONE,
            ),
            ProjectPartnerReportCoFinancingEntity(
                ProjectPartnerReportCoFinancingIdEntity(report = report, fundSortNumber = 1),
                programmeFund = null,
                percentage = TEN,
                total = TEN,
                current = ZERO,
                totalEligibleAfterControl = ZERO,
                previouslyReported = ONE,
                previouslyPaid = ZERO,
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

    }

    @MockK
    lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportPersistenceProvider

    @Test
    fun submitReportById() {
        val NOW = ZonedDateTime.now()
        val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val report = reportEntity(id = 45L, YESTERDAY)
        every { partnerReportRepository.findByIdAndPartnerId(45L, 10L) } returns report

        val submittedReport = persistence.submitReportById(10L, 45L, NOW)

        assertThat(submittedReport).isEqualTo(
            draftReportSubmissionEntity(id = 45L, YESTERDAY).copy(
                status = ReportStatus.Submitted,
                firstSubmission = NOW,
            )
        )
    }

    @Test
    fun startControlOnReportById() {
        val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val report = reportEntity(id = 47L, YESTERDAY, null, ReportStatus.Submitted)
        every { partnerReportRepository.findByIdAndPartnerId(47L, 15L) } returns report

        assertThat(persistence.startControlOnReportById(15L, 47L)).isEqualTo(
            draftReportSubmissionEntity(id = 47L, YESTERDAY).copy(
                status = ReportStatus.InControl,
            )
        )
    }

    @Test
    fun finalizeControlOnReportById() {
        val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val report = reportEntity(id = 48L, LAST_YEAR, null, ReportStatus.InControl, null)
        every { partnerReportRepository.findByIdAndPartnerId(48L, 16L) } returns report

        assertThat(report.totalEligibleAfterControl).isNull()
        assertThat(persistence.finalizeControlOnReportById(16L, 48L, YESTERDAY, TEN)).isEqualTo(
            draftReportSubmissionEntity(id = 48L, LAST_YEAR).copy(
                status = ReportStatus.Certified,
                controlEnd = YESTERDAY,
            )
        )
        assertThat(report.totalEligibleAfterControl).isEqualTo(TEN)
    }

    @Test
    fun getPartnerReportStatusById() {
        val report = reportEntity(id = 75L)
        every { partnerReportRepository.findByIdAndPartnerId(75L, 20L) } returns report
        assertThat(persistence.getPartnerReportStatusAndVersion(partnerId = 20L, reportId = 75L))
            .isEqualTo(ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "3.0"))
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
    fun listPartnerReports() {
        val twoWeeksAgo = ZonedDateTime.now().minusDays(14)

        every { partnerReportRepository.findAllByPartnerId(PARTNER_ID, Pageable.unpaged()) } returns
            PageImpl(listOf(reportSummary(id = 18L, createdAt = twoWeeksAgo)))

        assertThat(persistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()).content)
            .containsExactly(draftReportSummary(id = 18L, createdAt = twoWeeksAgo))
    }

    @Test
    fun getSubmittedPartnerReportIds() {
        every { partnerReportRepository
            .findAllIdsByPartnerIdAndStatusIn(PARTNER_ID, setOf(ReportStatus.Submitted, ReportStatus.InControl, ReportStatus.Certified))
        } returns setOf(18L)
        assertThat(persistence.getSubmittedPartnerReportIds(PARTNER_ID)).containsExactly(18L)
    }

    @Test
    fun exists() {
        every { partnerReportRepository.existsByPartnerIdAndId(PARTNER_ID, 25L) } returns false
        assertThat(persistence.exists(PARTNER_ID, 25L)).isFalse
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
}
