package io.cloudflight.jems.server.project.service.checklist.create.verification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class CreateVerificationChecklistInstanceTest : UnitTest() {

    private val relatedToId = 2L
    private val creatorId = 3L
    private val programmeChecklistId = 4L
    private val projectId = 5L
    private val reportId = 6L

    private val createControlChecklist = CreateChecklistInstanceModel(
        relatedToId,
        programmeChecklistId
    )

    private fun report(status: ProjectReportStatus): ProjectReportModel {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        return report
    }

    @MockK
    private lateinit var persistence: ChecklistInstancePersistence

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var createVerificationChecklistInstance: CreateVerificationChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence, projectReportPersistence, securityService)
    }

    @ParameterizedTest(name = "create verification checklist - OK - {0}")
    @EnumSource(
        value = ProjectReportStatus::class,
        names = ["InVerification", "VerificationReOpenedLast", "VerificationReOpenedLimited", "Finalized", "ReOpenFinalized"]
    )
    fun `create verification checklist - OK`() {
        every { projectReportPersistence.getReportById(projectId, reportId) } returns
                report(ProjectReportStatus.InVerification)
        every { securityService.getUserIdOrThrow() } returns AuthorizationUtil.userApplicant.id
        val result = mockk<ChecklistInstanceDetail>()
        every { persistence.create(createControlChecklist, creatorId) } returns result

        assertThat(createVerificationChecklistInstance.create(projectId, reportId, createControlChecklist)).isEqualTo(result)
    }

    @ParameterizedTest(name = "create verification checklist - failed - report is locked {0}")
    @EnumSource(
        value = ProjectReportStatus::class, mode = EnumSource.Mode.EXCLUDE,
        names = ["InVerification", "VerificationReOpenedLast", "VerificationReOpenedLimited", "Finalized", "ReOpenFinalized"]
    )
    fun `create verification checklist - failed - report is locked`(status: ProjectReportStatus) {
        every { projectReportPersistence.getReportById(projectId, reportId) } returns report(status)
        assertThrows<CreateVerificationChecklistInstanceStatusNotAllowedException> {
            createVerificationChecklistInstance.create(projectId, reportId, createControlChecklist)
        }
        verify(exactly = 0) { persistence.create(any(), any()) }
    }
}
