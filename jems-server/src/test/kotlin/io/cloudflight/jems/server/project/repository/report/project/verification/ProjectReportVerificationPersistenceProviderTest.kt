package io.cloudflight.jems.server.project.repository.report.project.verification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportVerificationClarificationEntity
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportVerificationPersistenceProviderTest : UnitTest() {

    companion object {

        private const val PROJECT_ID = 123L
        private const val REPORT_ID = 19L

        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TODAY = ZonedDateTime.now()

        private fun reportEntity(id: Long, projectId: Long, deadline: ProjectContractingReportingEntity? = null) =
            ProjectReportEntity(
                id = id,
                projectId = projectId,
                number = 1,
                status = ProjectReportStatus.InVerification,
                applicationFormVersion = "4.0",
                startDate = YESTERDAY,
                endDate = null,

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
                firstSubmission = LAST_WEEK.plusDays(1),
                lastReSubmission = mockk(),
                verificationDate = YESTERDAY.plusDays(2),
                verificationEndDate = null,
                verificationConclusionJs = null,
                verificationConclusionMa = null,
                verificationFollowup = null,
                lastVerificationReOpening = mockk(),
                riskBasedVerification = false,
                riskBasedVerificationDescription = "Risk based description"
            )

        private val verificationConclusion = ProjectReportVerificationConclusion(
            startDate = TODAY.toLocalDate(),
            conclusionJS = "Joint Secretariat conclusion",
            conclusionMA = "Managing Authority conclusion",
            verificationFollowUp = "None identified"
        )

        private val clarifications = listOf(
            ProjectReportVerificationClarificationEntity(
                id = 1L,
                number = 1,
                projectReport = reportEntity(REPORT_ID, PROJECT_ID),
                requestDate = YESTERDAY,
                answerDate = YESTERDAY,
                comment = "first"
            ),
            ProjectReportVerificationClarificationEntity(
                id = 2L,
                number = 2,
                projectReport = reportEntity(REPORT_ID, PROJECT_ID),
                requestDate = YESTERDAY,
                answerDate = YESTERDAY,
                comment = "second"
            )
        )
    }

    @MockK
    lateinit var projectReportRepository: ProjectReportRepository

    @MockK
    lateinit var projectReportVerificationClarificationRepository: ProjectReportVerificationClarificationRepository

    @InjectMockKs
    lateinit var projectReportVerificationPersistence: ProjectReportVerificationPersistenceProvider

    @Test
    fun `update verification conclusion`() {
        every {
            projectReportRepository.getByIdAndProjectId(
                id = REPORT_ID,
                projectId = PROJECT_ID
            )
        } returns reportEntity(REPORT_ID, PROJECT_ID)

        assertThat(
            projectReportVerificationPersistence.updateVerificationConclusion(
                projectId = PROJECT_ID,
                reportId = REPORT_ID,
                verificationConclusion
            )
        ).isEqualTo(
            verificationConclusion.copy(
                startDate = TODAY.toLocalDate(),
                conclusionJS  = "Joint Secretariat conclusion",
                conclusionMA = "Managing Authority conclusion",
                verificationFollowUp = "None identified"
            )
        )
    }

    @Test
    fun getVerificationClarifications() {
        every { projectReportVerificationClarificationRepository.findByProjectReportIdOrderByNumber(REPORT_ID) } returns clarifications

        assertThat(projectReportVerificationPersistence.getVerificationClarifications(REPORT_ID)).containsAll(
            listOf(
                ProjectReportVerificationClarification(
                    id = 1L,
                    number = 1,
                    requestDate = YESTERDAY,
                    answerDate = YESTERDAY,
                    comment = "first"
                ),
                ProjectReportVerificationClarification(
                    id = 2L,
                    number = 2,
                    requestDate = YESTERDAY,
                    answerDate = YESTERDAY,
                    comment = "second"
                )
            )
        )
    }

    @Test
    fun updateVerificationClarifications() {

        val clarificationsToUpdate = listOf(
            ProjectReportVerificationClarification(
                id = 1L,
                number = 1,
                requestDate = YESTERDAY,
                answerDate = YESTERDAY,
                comment = "first"
            ),
            ProjectReportVerificationClarification(
                id = 0L,
                number = 0,
                requestDate = YESTERDAY,
                answerDate = YESTERDAY,
                comment = "third"
            )
        )

        every { projectReportVerificationClarificationRepository.findByProjectReportIdOrderByNumber(REPORT_ID) } returns clarifications

        every {
            projectReportRepository.getByIdAndProjectId(
                id = REPORT_ID,
                projectId = PROJECT_ID
            )
        } returns reportEntity(REPORT_ID, PROJECT_ID)

        val toDeleteIdSlot = slot<MutableList<Long>>()
        every { projectReportVerificationClarificationRepository.deleteAllByIdInBatch(capture(toDeleteIdSlot)) } just Runs

        val toSaveSlot = slot<MutableList<ProjectReportVerificationClarificationEntity>>()
        every { projectReportVerificationClarificationRepository.saveAll(capture(toSaveSlot)) } returns listOf(
            ProjectReportVerificationClarificationEntity(
                id = 3L,
                projectReport = reportEntity(REPORT_ID, PROJECT_ID),
                number = 2,
                requestDate = LAST_WEEK.toLocalDate(),
                answerDate = YESTERDAY,
                comment = "third"
            )
        )
        every { projectReportVerificationClarificationRepository.findByProjectReportIdOrderByNumber(REPORT_ID) } returns listOf(
            ProjectReportVerificationClarificationEntity(
                id = 1L,
                projectReport = reportEntity(REPORT_ID, PROJECT_ID),
                number = 1,
                requestDate = YESTERDAY,
                answerDate = YESTERDAY,
                comment = "first"
            ),
            ProjectReportVerificationClarificationEntity(
                id = 3L,
                projectReport = reportEntity(REPORT_ID, PROJECT_ID),
                number = 2,
                requestDate = LAST_WEEK.toLocalDate(),
                answerDate = YESTERDAY,
                comment = "third"
            )
        )

        assertThat(
            projectReportVerificationPersistence.updateVerificationClarifications(
                PROJECT_ID,
                REPORT_ID,
                clarifications = clarificationsToUpdate
            )
        ).containsExactly(
            ProjectReportVerificationClarification(
                id = 1L,
                number = 1,
                requestDate = YESTERDAY,
                answerDate = YESTERDAY,
                comment = "first"
            ),
            ProjectReportVerificationClarification(
                id = 3L,
                number = 2,
                requestDate = LAST_WEEK.toLocalDate(),
                answerDate = YESTERDAY,
                comment = "third"
            )

        )
    }

}
