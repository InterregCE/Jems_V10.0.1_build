package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.entity.report.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.time.ZonedDateTime

class ProjectReportPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L

        private fun draftReportEntity(id: Long, createdAt: ZonedDateTime = ZonedDateTime.now()) = ProjectPartnerReportEntity(
            id = id,
            partnerId = PARTNER_ID,
            number = 1,
            status = ReportStatus.Draft,
            applicationFormVersion = "3.0",
            firstSubmission = null,
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
            ),
            createdAt = createdAt,
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
                coFinancing = coFinancing,
            ),
        )

        private fun draftReportSummary(id: Long, createdAt: ZonedDateTime) = ProjectPartnerReportSummary(
            id = id,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "3.0",
            firstSubmission = null,
            createdAt = createdAt,
        )

        private fun coFinancingEntities(report: ProjectPartnerReportEntity) = listOf(
            ProjectPartnerReportCoFinancingEntity(
                ProjectPartnerReportCoFinancingIdEntity(report = report, fundSortNumber = 1),
                programmeFund = programmeFundEntity,
                percentage = ONE,
            ),
            ProjectPartnerReportCoFinancingEntity(
                ProjectPartnerReportCoFinancingIdEntity(report = report, fundSortNumber = 1),
                programmeFund = null,
                percentage = TEN,
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

        private val reportToBeCreated = ProjectPartnerReportCreate(
            partnerId = PARTNER_ID,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "6.5",
            identification = PartnerReportIdentificationCreate(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 4,
                partnerAbbreviation = "partnerAbbreviation",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "nameInOriginalLanguage",
                nameInEnglish = "nameInEnglish",
                legalStatusId = legalStatus.id,
                partnerType = ProjectTargetGroup.SectoralAgency,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                coFinancing = coFinancing,
            )
        )

    }

    @MockK
    lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository

    @MockK
    lateinit var legalStatusRepository: ProgrammeLegalStatusRepository

    @MockK
    lateinit var programmeFundRepository: ProgrammeFundRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportPersistenceProvider

    @ParameterizedTest(name = "createPartnerReport, without legal status {0}")
    @ValueSource(booleans = [true, false])
    fun createPartnerReport(withoutLegalStatus: Boolean) {
        val reportSlot = slot<ProjectPartnerReportEntity>()
        val reportCoFinancingSlot = slot<Iterable<ProjectPartnerReportCoFinancingEntity>>()
        every { legalStatusRepository.getById(legalStatusEntity.id) } returns legalStatusEntity
        every { partnerReportRepository.save(capture(reportSlot)) } returnsArgument 0
        every { programmeFundRepository.getById(programmeFundEntity.id) } returns programmeFundEntity
        every { partnerReportCoFinancingRepository.saveAll(capture(reportCoFinancingSlot)) } returnsArgument 0

        val createdReport = persistence.createPartnerReport(reportToBeCreated.copy(
            identification = reportToBeCreated.identification.removeLegalStatusIf(withoutLegalStatus)
        ))

        assertThat(createdReport.createdAt).isNotNull
        assertThat(createdReport.reportNumber).isEqualTo(reportToBeCreated.reportNumber)
        assertThat(createdReport.status).isEqualTo(ReportStatus.Draft)
        assertThat(createdReport.version).isEqualTo(reportToBeCreated.version)
        assertThat(createdReport.firstSubmission).isNull()

        with(reportSlot.captured) {
            assertThat(partnerId).isEqualTo(PARTNER_ID)
            assertThat(number).isEqualTo(reportToBeCreated.reportNumber)
            assertThat(status).isEqualTo(ReportStatus.Draft)
            assertThat(applicationFormVersion).isEqualTo(reportToBeCreated.version)
            assertThat(firstSubmission).isNull()
        }
        with(reportSlot.captured.identification) {
            assertThat(projectIdentifier).isEqualTo("projectIdentifier")
            assertThat(projectAcronym).isEqualTo("projectAcronym")
            assertThat(partnerNumber).isEqualTo(4)
            assertThat(partnerAbbreviation).isEqualTo("partnerAbbreviation")
            assertThat(partnerRole).isEqualTo(ProjectPartnerRole.PARTNER)
            assertThat(nameInOriginalLanguage).isEqualTo("nameInOriginalLanguage")
            assertThat(nameInEnglish).isEqualTo("nameInEnglish")
            if (withoutLegalStatus)
                assertThat(legalStatus).isNull()
            else
                assertThat(legalStatus!!.equals(legalStatusEntity)).isTrue
            assertThat(partnerType).isEqualTo(ProjectTargetGroup.SectoralAgency)
            assertThat(vatRecovery).isEqualTo(ProjectPartnerVatRecovery.Yes)
        }

        assertThat(reportCoFinancingSlot.captured).hasSize(2)
        with(reportCoFinancingSlot.captured.find { it.id.fundSortNumber == 1 }!!) {
            assertThat(programmeFund!!.equals(programmeFundEntity)).isTrue
            assertThat(percentage).isEqualTo(ONE)
        }
        with(reportCoFinancingSlot.captured.find { it.id.fundSortNumber == 2 }!!) {
            assertThat(programmeFund).isNull()
            assertThat(percentage).isEqualTo(TEN)
        }
    }

    private fun PartnerReportIdentificationCreate.removeLegalStatusIf(needed: Boolean) =
        this.copy(legalStatusId = if (needed) null else this.legalStatusId)

    @Test
    fun submitReportById() {
        val NOW = ZonedDateTime.now()
        val report = draftReportEntity(id = 45L)
        every { partnerReportRepository.findByIdAndPartnerId(45L, 10L) } returns report

        val submittedReport = persistence.submitReportById(10L, 45L, NOW)

        assertThat(submittedReport.status).isEqualTo(ReportStatus.Submitted)
        assertThat(submittedReport.firstSubmission).isEqualTo(NOW)
    }

    @Test
    fun getPartnerReportById() {
        val report = draftReportEntity(id = 35L)
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
            PageImpl(listOf(draftReportEntity(id = 18L, createdAt = twoWeeksAgo)))

        assertThat(persistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()).content)
            .containsExactly(draftReportSummary(id = 18L, createdAt = twoWeeksAgo))
    }

    @Test
    fun getCurrentLatestReportNumberForPartner() {
        every { partnerReportRepository.getMaxNumberForPartner(PARTNER_ID) } returns 7
        assertThat(persistence.getCurrentLatestReportNumberForPartner(PARTNER_ID)).isEqualTo(7)
    }
}
