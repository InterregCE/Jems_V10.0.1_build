package io.cloudflight.jems.server.project.service.checklist.create.closure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class CreateClosureChecklistInstanceTest: UnitTest() {

    private val creatorId = 3L
    private val programmeChecklistId = 4L
    private val reportId = 6L

    private val createControlChecklist = CreateChecklistInstanceModel(
        reportId,
        programmeChecklistId
    )

    private fun report(status: ProjectReportStatus): ProjectReportModel {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { report.finalReport } returns true
        return report
    }

    @MockK
    private lateinit var persistence: ChecklistInstancePersistence

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var programmeChecklistPersistence: ProgrammeChecklistPersistence

    @MockK
    private lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var createClosureChecklistInstance: CreateClosureChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence, projectReportPersistence, securityService)
    }

    @ParameterizedTest(name = "create verification checklist - OK - {0}")
    @EnumSource(
        value = ProjectReportStatus::class, mode = EnumSource.Mode.EXCLUDE,
        names = ["Submitted", "InVerification", "Finalized", "ReOpenFinalized"]
    )
    fun `create closure checklist - OK`(status: ProjectReportStatus) {
        every { projectReportPersistence.getReportByIdUnSecured(reportId) } returns report(status)
        every { securityService.getUserIdOrThrow() } returns creatorId
        val result = mockk<ChecklistInstanceDetail>()
        every { persistence.create(createControlChecklist, creatorId) } returns result
        every { programmeChecklistPersistence.getChecklistDetail(programmeChecklistId).type } returns ProgrammeChecklistType.CLOSURE

        assertThat(createClosureChecklistInstance.create(reportId, programmeChecklistId)).isEqualTo(result)
    }

    @ParameterizedTest(name = "create verification checklist - failed - report is closed")
    @EnumSource(
        value = ProjectReportStatus::class, mode = EnumSource.Mode.INCLUDE,
        names = ["Submitted", "InVerification", "Finalized", "ReOpenFinalized"]
    )
    fun `create closure checklist - failed - report is closed`(status: ProjectReportStatus) {
        every { projectReportPersistence.getReportByIdUnSecured(reportId) } returns report(status)
        assertThrows<CreateClosureChecklistInstanceStatusNotAllowedException> {
            createClosureChecklistInstance.create(reportId, programmeChecklistId)
        }
        verify(exactly = 0) { persistence.create(any(), any()) }
    }

    @Test
    fun `create closure checklist - failed - report is not final`() {
        val reportNotFinal = mockk<ProjectReportModel>()
        every { reportNotFinal.status } returns ProjectReportStatus.Draft
        every { reportNotFinal.finalReport } returns false
        every { projectReportPersistence.getReportByIdUnSecured(reportId) } returns reportNotFinal
        assertThrows<CreateClosureChecklistInstanceNotFinalReportException> {
            createClosureChecklistInstance.create(reportId, programmeChecklistId)
        }
        verify(exactly = 0) { persistence.create(any(), any()) }
    }
}
