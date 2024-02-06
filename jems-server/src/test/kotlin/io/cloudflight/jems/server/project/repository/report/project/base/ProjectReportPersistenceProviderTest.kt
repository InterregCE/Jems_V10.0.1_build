package io.cloudflight.jems.server.project.repository.report.project.base

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanOperation
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportBaseData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCoFinancingEntity
import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportSpendingProfileRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportStatusAndType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.stream.Stream

class ProjectReportPersistenceProviderTest : UnitTest() {

    companion object {
        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)
        private val DAY_AGO = ZonedDateTime.now().minusDays(1)
        private val DAY_AGO_2 = ZonedDateTime.now().minusMonths(2)
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val MONTH_AGO = LocalDate.now().minusMonths(1)
        private val WEEK_AGO = LocalDate.now().minusWeeks(1)
        private val YEAR_AGO = LocalDate.now().minusYears(1)

        private fun reportEntity(id: Long, projectId: Long, deadline: ProjectContractingReportingEntity? = null) = ProjectReportEntity(
            id = id,
            projectId = projectId,
            number = 1,
            status = ProjectReportStatus.Draft,
            applicationFormVersion = "3.0",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            type = ContractingDeadlineType.Both,
            deadline = deadline,
            reportingDate = YESTERDAY.minusDays(1),
            periodNumber = 4,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",
            spfPartnerId = null,

            createdAt = LAST_WEEK,
            firstSubmission = LAST_YEAR,
            lastReSubmission = DAY_AGO,
            verificationDate = null,
            verificationEndDate = null,

            verificationConclusionJs = null,
            verificationConclusionMa = null,
            verificationFollowup = null,
            lastVerificationReOpening = DAY_AGO_2,
            riskBasedVerification = false,
            riskBasedVerificationDescription = "RISK BASED DESCRIPTION"
        )

        private fun reportProjectCertificateCoFinancingEntity(): ReportProjectCertificateCoFinancingEntity? {
            val mocked = mockk<ReportProjectCertificateCoFinancingEntity>()
            every { mocked.sumCurrent } returns BigDecimal.ONE
            every { mocked.sumCurrentVerified } returns BigDecimal.ONE
            return mocked
        }

        private fun reportForListing(id: Long, projectId: Long) = ProjectReportModel(
            id = id,
            reportNumber = 1,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "3.0",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            type = ContractingDeadlineType.Both,
            deadlineId = null,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            projectId = projectId,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",
            spfPartnerId = null,

            createdAt = LAST_WEEK,
            firstSubmission = LAST_YEAR,
            lastReSubmission = DAY_AGO,
            verificationDate = null,
            verificationEndDate = null,
            amountRequested = BigDecimal.ONE,
            totalEligibleAfterVerification = BigDecimal.ONE,
            lastVerificationReOpening = DAY_AGO_2,
            riskBasedVerification = false,
            riskBasedVerificationDescription = "RISK BASED DESCRIPTION"
        )

        private fun report(id: Long, projectId: Long) = ProjectReportModel(
            id = id,
            reportNumber = 1,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "3.0",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            type = ContractingDeadlineType.Both,
            deadlineId = null,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            projectId = projectId,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",
            spfPartnerId = null,

            createdAt = LAST_WEEK,
            firstSubmission = LAST_YEAR,
            lastReSubmission = DAY_AGO,
            verificationDate = null,
            verificationEndDate = null,
            amountRequested = null,
            totalEligibleAfterVerification = null,
            lastVerificationReOpening = DAY_AGO_2,
            riskBasedVerification = false,
            riskBasedVerificationDescription = "RISK BASED DESCRIPTION"
        )

        fun report(id: Long, status: ProjectReportStatus, deadlineType: ContractingDeadlineType?, reportType: ContractingDeadlineType?): ProjectReportEntity {
            val report = mockk<ProjectReportEntity>()
            every { report.id } returns id
            if (deadlineType == null) {
                every { report.deadline } returns null
            } else {
                val deadline = mockk<ProjectContractingReportingEntity>()
                every { deadline.type } returns deadlineType
                every { report.deadline } returns deadline
            }
            every { report.status } returns status
            every { report.type } returns reportType
            return report
        }

        private fun draftReportSubmissionEntity(id: Long, projectId: Long) = ProjectReportSubmissionSummary(
            id = id,
            reportNumber = 1,
            status = ProjectReportStatus.Draft,
            version = "3.0",
            firstSubmission = LAST_YEAR,
            createdAt = LAST_WEEK,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            projectId = projectId,
            periodNumber = 4
        )
    }

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository

    @MockK
    private lateinit var contractingDeadlineRepository: ProjectContractingReportingRepository

    @MockK
    private lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    private lateinit var projectReportSpendingProfileRepository: ProjectReportSpendingProfileRepository

    @MockK
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @InjectMockKs
    private lateinit var persistence: ProjectReportPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(
            projectReportRepository,
            contractingDeadlineRepository,
            partnerReportRepository,
            projectReportSpendingProfileRepository,
            jpaQueryFactory
        )
    }

    @Test
    fun listReports() {
        val projectId = 95L

        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any()) } returns query
        every { query.from(any()) } returns query
        every { query.leftJoin(any<EntityPath<Any>>()) } returns query
        every { query.on(any()) } returns query
        val slotWhere = slot<BooleanOperation>()
        every { query.where(capture(slotWhere)) } returns query
        every { query.offset(any()) } returns query
        every { query.limit(any()) } returns query
        every { query.orderBy(any()) } returns query

        val tuple = mockk<Tuple>()
        every { tuple.get(0, ProjectReportEntity::class.java) } returns reportEntity(42L, projectId)
        every { tuple.get(1, ReportProjectCertificateCoFinancingEntity::class.java) } returns reportProjectCertificateCoFinancingEntity()

        val result = mockk<QueryResults<Tuple>>()
        every { result.total } returns 1
        every { result.results } returns listOf(tuple)
        every { query.fetchResults() } returns result

        assertThat(persistence.listReports(projectId, Pageable.ofSize(1))).containsExactly(reportForListing(42L, projectId))
        assertThat(slotWhere.captured.toString()).isEqualTo("projectReportEntity.projectId = 95")
    }

    @Test
    fun listProjectReports() {
        val projectId = 96L

        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any()) } returns query
        every { query.from(any()) } returns query
        every { query.leftJoin(any<EntityPath<Any>>()) } returns query
        every { query.on(any()) } returns query
        val slotWhere = slot<Predicate>()
        every { query.where(capture(slotWhere)) } returns query
        every { query.offset(any()) } returns query
        every { query.limit(any()) } returns query
        every { query.orderBy(any()) } returns query

        val tuple = mockk<Tuple>()
        every { tuple.get(0, ProjectReportEntity::class.java) } returns reportEntity(42L, projectId)
        every { tuple.get(1, ReportProjectCertificateCoFinancingEntity::class.java) } returns reportProjectCertificateCoFinancingEntity()

        val result = mockk<QueryResults<Tuple>>()
        every { result.total } returns 1
        every { result.results } returns listOf(tuple)
        every { query.  fetchResults() } returns result

        assertThat(persistence.listProjectReports(
            setOf(projectId, 97L),
            setOf(ProjectReportStatus.Submitted, ProjectReportStatus.InVerification),
            Pageable.ofSize(1)
        )).containsExactly(reportForListing(42L, projectId))
        assertThat(slotWhere.captured.toString())
            .isEqualTo("projectReportEntity.projectId in [96, 97] && projectReportEntity.status in [Submitted, InVerification]")
    }

    @Test
    fun `listProjectReports - no project id - generates correct query condition`() {


        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any()) } returns query
        every { query.from(any()) } returns query
        every { query.leftJoin(any<EntityPath<Any>>()) } returns query
        every { query.on(any()) } returns query
        val slotWhere = slot<Predicate>()
        every { query.where(capture(slotWhere)) } returns query
        every { query.offset(any()) } returns query
        every { query.limit(any()) } returns query
        every { query.orderBy(any()) } returns query


        val result = mockk<QueryResults<Tuple>>()
        every { result.total } returns 0
        every { result.results } returns emptyList()
        every { query.  fetchResults() } returns result

        assertThat(persistence.listProjectReports(
            emptySet(),
            setOf(ProjectReportStatus.Submitted, ProjectReportStatus.InVerification),
            Pageable.ofSize(1)
        )).isEmpty()
        assertThat(slotWhere.captured.toString())
            .isEqualTo("projectReportEntity.projectId in [] && projectReportEntity.status in [Submitted, InVerification]")
    }

    @Test
    fun getAllProjectReportsBaseDataByProjectId() {
        val streamData = Stream.of(
            ProjectReportBaseData(80L, "v1.0", 1),
            ProjectReportBaseData(81L, "v1.0", 2),
            ProjectReportBaseData(82L, "v1.0", 1),
        )
        val sequence = sequenceOf(
            ProjectReportBaseData(80L, "v1.0", 1),
            ProjectReportBaseData(81L, "v1.0", 2),
            ProjectReportBaseData(82L, "v1.0", 1),
        )

        every { projectReportRepository.findAllProjectReportsBaseDataByProjectId(75L) } returns streamData
        assertThat(persistence.getAllProjectReportsBaseDataByProjectId(75L).toList())
            .isEqualTo(sequence.toList())
    }

    @Test
    fun getReportById() {
        val projectId = 94L
        val deadline = ProjectContractingReportingEntity(804L, mockk(), ContractingDeadlineType.Finance, 5, MONTH_AGO, "", number = 1)
        val report = reportEntity(45L, projectId, deadline)
        every { projectReportRepository.getByIdAndProjectId(22L, projectId) } returns report
        assertThat(persistence.getReportById(projectId, 22L)).isEqualTo(
            report(45L, projectId).copy(
                deadlineId = 804L,
                type = ContractingDeadlineType.Finance,
                periodNumber = 5,
                reportingDate = MONTH_AGO,
            )
        )
    }

    @Test
    fun updateReport() {
        val projectId = 92L
        val report = reportEntity(14L, projectId, deadline = null)
        every { projectReportRepository.getByIdAndProjectId(14L, projectId) } returns report

        val deadlineEntity = ProjectContractingReportingEntity(84L, mockk(), ContractingDeadlineType.Finance, 5, MONTH_AGO, "", number = 1)
        every { contractingDeadlineRepository.findByProjectIdAndId(projectId, 84L) } returns deadlineEntity

        val deadline = ProjectReportDeadline(
            deadlineId = 84L,
            type = ContractingDeadlineType.Finance,
            periodNumber = 14,
            reportingDate = WEEK_AGO,
        )
        assertThat(
            persistence.updateReport(
                projectId,
                reportId = 14L, startDate = WEEK_AGO, endDate = YEAR_AGO, deadline = deadline
            )
        ).isEqualTo(
            report(14L, projectId = projectId).copy(
                // taken from deadline entity, because deadlineId is not null
                startDate = WEEK_AGO,
                endDate = YEAR_AGO,
                deadlineId = 84L,
                type = ContractingDeadlineType.Finance,
                periodNumber = 5,
                reportingDate = MONTH_AGO,
            )
        )

        // asserting report entity itself
        assertThat(report.startDate).isEqualTo(WEEK_AGO)
        assertThat(report.endDate).isEqualTo(YEAR_AGO)
        assertThat(report.deadline?.id).isEqualTo(84L)
        assertThat(report.type).isEqualTo(ContractingDeadlineType.Finance)
        assertThat(report.periodNumber).isEqualTo(14)
        assertThat(report.reportingDate).isEqualTo(WEEK_AGO)
    }

    @Test
    fun deleteReport() {
        every { projectReportRepository.deleteByProjectIdAndId(any(), any()) } answers { }
        persistence.deleteReport(92L, 47L)
        verify(exactly = 1) { projectReportRepository.deleteByProjectIdAndId(92L, 47L) }
    }

    @Test
    fun getCurrentLatestReportFor() {
        val projectId = 90L
        every { projectReportRepository.findFirstByProjectIdOrderByIdDesc(projectId) } returns reportEntity(48L, projectId)
        assertThat(persistence.getCurrentLatestReportFor(projectId)).isEqualTo(report(48L, projectId))
    }

    @Test
    fun countForProject() {
        val projectId = 89L
        every { projectReportRepository.countAllByProjectId(projectId) } returns 15
        assertThat(persistence.countForProject(projectId)).isEqualTo(15)
    }

    @ParameterizedTest(name = "existsHavingTypeAndStatusIn - with {0} || without {1} - type {2}")
    @CsvSource(value = [
        "35,48,Content,35|48",
        "35,0,Content,35",
        "0,48,Content,48",
        "0,0,Content,",
        "35,48,Finance,35|48",
        "35,0,Finance,35",
        "0,48,Finance,48",
        "0,0,Finance,",
    ])
    fun existsHavingTypeAndStatusIn(reportWithNr: Int, reportWithoutNr: Int, type: ContractingDeadlineType, expectedResultString: String?) {
        val expectedResult = expectedResultString?.split("|")?.map { it.toInt() } ?: emptyList()
        val projectId = 88L
        val statusesWithExistingDeadlines = slot<Set<ProjectReportStatus>>()
        val typesWithExistingDeadlines = slot<Set<ContractingDeadlineType>>()
        val statusesWithoutExistingDeadlines = slot<Set<ProjectReportStatus>>()
        val typesWithoutExistingDeadlines = slot<Set<ContractingDeadlineType>>()

        val reportWith = mockk<ProjectReportEntity>()
        every { reportWith.number } returns 35
        val reportWithout = mockk<ProjectReportEntity>()
        every { reportWithout.number } returns 48

        every { projectReportRepository.findByProjectIdAndStatusInAndTypeInOrderByIdDesc(
            projectId, capture(statusesWithExistingDeadlines), capture(typesWithExistingDeadlines)
        ) } returns if (reportWithNr != 0) listOf(reportWith) else emptyList()
        every { projectReportRepository.findByProjectIdAndStatusInAndDeadlineTypeInOrderByIdDesc(
            projectId, capture(statusesWithoutExistingDeadlines), capture(typesWithoutExistingDeadlines)
        ) } returns if (reportWithoutNr != 0) listOf(reportWithout) else emptyList()

        val mockedSet = mockk<Set<ProjectReportStatus>>()
        assertThat(persistence.existsHavingTypeAndStatusIn(projectId, type, mockedSet)).isEqualTo(expectedResult)

        assertThat(statusesWithExistingDeadlines.captured).isEqualTo(mockedSet)
        assertThat(statusesWithoutExistingDeadlines.captured).isEqualTo(mockedSet)

        assertThat(typesWithExistingDeadlines.captured).containsExactlyInAnyOrder(type, ContractingDeadlineType.Both)
        assertThat(typesWithoutExistingDeadlines.captured).containsExactlyInAnyOrder(type, ContractingDeadlineType.Both)
    }

    @Test
    fun getCurrentSpendingProfile() {
        val projectReportId = 1L
        every { partnerReportRepository.findTotalAfterControlPerPartner(projectReportId) } returns
                listOf(Pair(10L, BigDecimal(400)), Pair(11L, BigDecimal(200)))
        assertThat(persistence.getCurrentSpendingProfile(projectReportId)).isEqualTo(
            mapOf(10L to BigDecimal(400), 11L to BigDecimal(200))
        )
    }

    @Test
    fun submitReportInitially() {
        val projectId = 8L
        val report = reportEntity(id = 49L, projectId)
        val submissionTime = ZonedDateTime.now().minusDays(2).plusHours(5)

        every { projectReportRepository.getByIdAndProjectId(49L, projectId) } returns report

        assertThat(persistence.submitReportInitially(projectId, 49L, submissionTime)).isEqualTo(
            draftReportSubmissionEntity(49L, projectId).copy(
                status = ProjectReportStatus.Submitted,
                firstSubmission = submissionTime,
            )
        )
    }

    @Test
    fun reSubmitReport() {
        val projectId = 9L
        val report = reportEntity(id = 57L, projectId)
        assertThat(report.lastReSubmission).isEqualTo(DAY_AGO)
        val reSubmissionTime = ZonedDateTime.now().minusDays(3).plusHours(7)

        every { projectReportRepository.getByIdAndProjectId(57L, projectId) } returns report

        assertThat(persistence.reSubmitReport(projectId, 57L, ProjectReportStatus.ReOpenFinalized, reSubmissionTime))
            .isEqualTo(draftReportSubmissionEntity(57L, projectId).copy(
                status = ProjectReportStatus.ReOpenFinalized,
            )
        )
        assertThat(report.lastReSubmission).isEqualTo(reSubmissionTime)
    }

    @Test
    fun getSubmittedProjectReportIds() {
        val projectId = 1L
        every {
            projectReportRepository.findAllByProjectIdAndStatusInOrderByNumberDesc(
                projectId, setOf(
                    ProjectReportStatus.Submitted,
                    ProjectReportStatus.InVerification,
                    ProjectReportStatus.Finalized,
                    ProjectReportStatus.ReOpenFinalized,
                    ProjectReportStatus.ReOpenSubmittedLimited,
                    ProjectReportStatus.VerificationReOpenedLimited,
                )
            )
        } returns listOf(
            report(id = 45L, ProjectReportStatus.Draft, deadlineType = null, reportType = ContractingDeadlineType.Content),
            report(id = 46L, ProjectReportStatus.Submitted, deadlineType = ContractingDeadlineType.Both, reportType = ContractingDeadlineType.Content),
            report(id = 47L, ProjectReportStatus.InVerification, deadlineType = ContractingDeadlineType.Finance, reportType = null),
        )
        assertThat(persistence.getSubmittedProjectReports(projectId)).containsExactly(
            ProjectReportStatusAndType(45L, ProjectReportStatus.Draft, ContractingDeadlineType.Content),
            ProjectReportStatusAndType(46L, ProjectReportStatus.Submitted, ContractingDeadlineType.Both),
            ProjectReportStatusAndType(47L, ProjectReportStatus.InVerification, ContractingDeadlineType.Finance),
        )
    }

    @Test
    fun updateNewerReportNumbersIfAllOpen() {
        val projectId = 3L
        val reportNumber = 4
        val reports = listOf(
            reportEntity(5, projectId).apply { number = 5 },
            reportEntity(6, projectId).apply { number = 6 },
            reportEntity(7, projectId).apply { number = 7 },
        )
        every { projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, reportNumber) } returns reports

        persistence.decreaseNewerReportNumbersIfAllOpen(projectId, reportNumber)

        verify(exactly = 1) { projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, reportNumber) }
        assertThat(reports.map { it.number }).containsExactly(4, 5, 6)
    }

    @Test
    fun doNotUpdateNewerReportNumbersIfAnyClosed() {
        val projectId = 3L
        val reportNumber = 4
        val reports = listOf(
            reportEntity(5, projectId).apply { number = 5 },
            reportEntity(6, projectId).apply { number = 6 }.apply { status = ProjectReportStatus.Submitted },
            reportEntity(7, projectId).apply { number = 7 },
        )
        every { projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, reportNumber) } returns reports

        persistence.decreaseNewerReportNumbersIfAllOpen(projectId, reportNumber)

        verify(exactly = 1) { projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, reportNumber) }
        assertThat(reports.map { it.number }).containsExactly(5, 6, 7)
    }

    @Test
    fun startVerificationOnReportById() {
        val projectId = 3L

        val report = reportEntity(id = 47L, projectId)
        every { projectReportRepository.getByIdAndProjectId(47L, projectId) } returns report

        val inVerificationReport = persistence.startVerificationOnReportById(projectId, 47L)

        assertThat(inVerificationReport).isEqualTo(
            draftReportSubmissionEntity(id = 47L, projectId).copy(
                status = ProjectReportStatus.InVerification,
            )
        )
    }

    @Test
    fun finalizeVerificationOnReportById() {
        val projectId = 3L
        val reportId = 48L
        val report = reportEntity(id = reportId, projectId)
        every { projectReportRepository.getByIdAndProjectId(reportId, projectId) } returns report
        assertThat(report.verificationEndDate).isNull()

        assertThat(persistence.finalizeVerificationOnReportById(projectId, reportId, LAST_WEEK)).isEqualTo(
            draftReportSubmissionEntity(reportId, projectId).copy(
                status = ProjectReportStatus.Finalized
            )
        )
        assertThat(report.verificationEndDate).isEqualTo(LAST_WEEK)
    }

    @ParameterizedTest(name = "getCurrentLatestReportOfType {0}")
    @CsvSource(
        value = [
            "Content,Content|Both",
            "Finance,Finance|Both",
            "Both,Content|Finance|Both",
        ]
    )
    fun getCurrentLatestReportOfType(type: ContractingDeadlineType, expectedEqualTypesString: String) {
        val expectedEqualTypes = expectedEqualTypesString.split("|").mapTo(HashSet()) { ContractingDeadlineType.valueOf(it) }
        val projectId = 14L

        val reportWithDeadline = reportEntity(32L, projectId)
        reportWithDeadline.number = 62
        val statusesSlotWithDeadline = slot<Set<ProjectReportStatus>>()
        every { projectReportRepository.findByProjectIdAndStatusInAndTypeInOrderByIdDesc(
            projectId = projectId,
            statuses = capture(statusesSlotWithDeadline),
            types = expectedEqualTypes,
        ) } returns listOf(reportWithDeadline)

        val reportWithoutDeadline = reportEntity(39L, projectId)
        reportWithoutDeadline.number = 69
        val statusesSlotWithoutDeadline = slot<Set<ProjectReportStatus>>()
        every { projectReportRepository.findByProjectIdAndStatusInAndDeadlineTypeInOrderByIdDesc(
            projectId = projectId,
            statuses = capture(statusesSlotWithoutDeadline),
            types = expectedEqualTypes,
        ) } returns listOf(reportWithoutDeadline)

        assertThat(persistence.getCurrentLatestReportOfType(projectId, type))
            .isEqualTo(report(39L, projectId).copy(reportNumber = 69))

        assertThat(statusesSlotWithDeadline.captured).containsAll(ProjectReportStatus.values().toSet())
        assertThat(statusesSlotWithoutDeadline.captured).containsAll(ProjectReportStatus.values().toSet())
    }

    @Test
    fun `getCurrentLatestReportOfType - empty`() {
        val projectId = 14L

        every { projectReportRepository.findByProjectIdAndStatusInAndTypeInOrderByIdDesc(
            projectId = projectId,
            statuses = any(),
            types = any(),
        ) } returns emptyList()

        every { projectReportRepository.findByProjectIdAndStatusInAndDeadlineTypeInOrderByIdDesc(
            projectId = projectId,
            statuses = any(),
            types = any(),
        ) } returns emptyList()

        assertThat(persistence.getCurrentLatestReportOfType(projectId, ContractingDeadlineType.Both)).isNull()
    }

}
