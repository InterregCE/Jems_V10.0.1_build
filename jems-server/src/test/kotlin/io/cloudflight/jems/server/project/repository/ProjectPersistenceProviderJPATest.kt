package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.JPATest
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_DURATION
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_PERIODS
import io.cloudflight.jems.server.dataGenerator.project.versionedInputTranslation
import io.cloudflight.jems.server.dataGenerator.project.versionedString
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectFull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.beans.factory.annotation.Autowired

internal class ProjectPersistenceProviderJPATest : JPATest() {

    @Autowired
    private lateinit var persistenceProvider: ProjectPersistenceProvider

    @Autowired
    private lateinit var projectStatusHistoryRepository: ProjectStatusHistoryRepository


    @Test
    @ExpectSelect(24)
    @ExpectInsert(0)
    @ExpectUpdate(0)
    @ExpectDelete(0)
    fun `should return correct version of project data for version 2`() {
        val version = "2.0"
        val expectedStatus = projectStatusHistoryRepository.findAllByProjectIdAndStatusInOrderByUpdatedDesc(
            CONTRACTED_PROJECT_ID, listOf(ApplicationStatus.APPROVED)
        ).elementAt(1)

        assertThat(
            persistenceProvider.getProject(CONTRACTED_PROJECT_ID, version)
        ).isEqualTo(
            ProjectFull(
                id = CONTRACTED_PROJECT_ID,
                customIdentifier = CONTRACTED_PROJECT.customIdentifier,
                callSettings = CONTRACTED_PROJECT.callSettings,
                acronym = versionedString("acronym", version),
                applicant = CONTRACTED_PROJECT.applicant,
                projectStatus = expectedStatus.toProjectStatus(),
                firstSubmission = CONTRACTED_PROJECT.firstSubmission,
                lastResubmission = CONTRACTED_PROJECT.lastResubmission,
                assessmentStep1 = CONTRACTED_PROJECT.assessmentStep1,
                assessmentStep2 = CONTRACTED_PROJECT.assessmentStep2,
                contractedDecision = CONTRACTED_PROJECT.contractedDecision,
                title = versionedInputTranslation("title", version),
                intro = versionedInputTranslation("intro", version),
                duration = CONTRACTED_PROJECT_DURATION,
                specificObjective = CONTRACTED_PROJECT.specificObjective,
                programmePriority = CONTRACTED_PROJECT.programmePriority,
                periods = CONTRACTED_PROJECT_PERIODS
            )
        )
    }


}
