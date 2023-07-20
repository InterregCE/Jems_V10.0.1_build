package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationClarification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.report.project.verification.ProjectReportVerificationPersistenceProvider
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import java.time.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils

class UpdateProjectReportVerificationClarificationTest: UnitTest() {

    companion object {

        private val TODAY = LocalDate.now()

        private const val REPORT_ID = 3301L
        private const val PROJECT_ID = 726L

        private val clarifications = listOf(
            ProjectReportVerificationClarification(
                id = 1L,
                number = 1,
                requestDate = TODAY.minusDays(2),
                answerDate = TODAY.minusDays(1),
                comment = "first"
            ),
            ProjectReportVerificationClarification(
                id = 2L,
                number = 2,
                requestDate = TODAY.minusDays(1),
                answerDate = TODAY,
                comment = "second"
            ),
            ProjectReportVerificationClarification(
                id = 3L,
                number = 3,
                requestDate = TODAY.minusDays(1),
                answerDate = TODAY,
                comment = "third"
            )
        )

        private val invalidClarification = ProjectReportVerificationClarification(
            id = 4L,
            number = 4,
            requestDate = TODAY.minusDays(1),
            answerDate = TODAY,
            comment = RandomStringUtils.random(3001)
        )

        fun report(id: Long, status: ProjectReportStatus): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.id } returns id
            every { report.status } returns status
            return report
        }
    }

    @MockK
    private lateinit var projectReportVerificationPersistenceProvider: ProjectReportVerificationPersistenceProvider

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    private lateinit var interactor: UpdateProjectReportVerificationClarification

    @Test
    fun updateClarifications() {
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report(REPORT_ID, ProjectReportStatus.InVerification)
        val clarificationsExpected = listOf(
            ProjectReportVerificationClarification(
                id = 1L,
                number = 1,
                requestDate = TODAY.minusDays(2),
                answerDate = TODAY.minusDays(1),
                comment = "first"
            ),
            ProjectReportVerificationClarification(
                id = 3L,
                number = 2,
                requestDate = TODAY.minusDays(1),
                answerDate = TODAY,
                comment = "third"
            ),
            ProjectReportVerificationClarification(
                id = 4L,
                number = 3,
                requestDate = TODAY,
                answerDate = null,
                comment = "forth"
            ),
        )

        every { projectReportVerificationPersistenceProvider.getVerificationClarifications(REPORT_ID) } returns clarifications

        every { projectReportVerificationPersistenceProvider.updateVerificationClarifications(
            projectId = PROJECT_ID,
            reportId = REPORT_ID,
            clarifications = any()
        )
        } returns clarificationsExpected

        interactor.updateClarifications(PROJECT_ID, REPORT_ID, listOf(
            ProjectReportVerificationClarification(
                id = 1L,
                number = 1,
                requestDate = TODAY.minusDays(2),
                answerDate = TODAY.minusDays(1),
                comment = "first"
            ),
            ProjectReportVerificationClarification(
                id = 3L,
                number = 3,
                requestDate = TODAY.minusDays(1),
                answerDate = TODAY,
                comment = "third"
            ),
            ProjectReportVerificationClarification(
                id = 0L,
                number = 0,
                requestDate = TODAY,
                answerDate = null,
                comment = "forth"
            ),
        )).containsAll(clarificationsExpected)
    }

    @Test
    fun `update clarifications - incorrect report status should throw exception`() {
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report(
            REPORT_ID,
            ProjectReportStatus.Draft
        )
        assertThrows<ReportVerificationStatusNotValidException> {
            interactor.updateClarifications(PROJECT_ID, REPORT_ID, listOf())
        }
    }

    @Test
    fun `update clarifications - invalid input should throw exception`() {
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report(
            REPORT_ID,
            ProjectReportStatus.InVerification
        )
        assertThrows<ReportVerificationInvalidInputException> {
            interactor.updateClarifications(PROJECT_ID, REPORT_ID, listOf(invalidClarification))
        }
    }
}
