package io.cloudflight.jems.server.project.service.checklist.clone.closure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.ZonedDateTime

class CloneClosureChecklistInstanceTest: UnitTest() {

    private val checklistId = 100L
    private val creatorId = 3L
    private val programmeChecklistId = 4L
    private val reportId = 6L
    private val TODAY = ZonedDateTime.now()

    private val createControlChecklist = CreateChecklistInstanceModel(
        relatedToId = reportId,
        programmeChecklistId = programmeChecklistId
    )

    private val clonedClosureChecklistDetail = ChecklistInstanceDetail(
        checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CLOSURE,
        name = "name",
        relatedToId = reportId,
        creatorEmail = "a@a",
        creatorId = creatorId,
        createdAt = TODAY,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = true,
        components = mutableListOf(
            ChecklistComponentInstance(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                1,
                HeadlineMetadata("headline"),
                null
            ),
            ChecklistComponentInstance(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                2,
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe", ""),
                null
            ),
            ChecklistComponentInstance(
                4L,
                ProgrammeChecklistComponentType.TEXT_INPUT,
                3,
                TextInputMetadata("Question to be answered", "Label", 2000),
                null
            )
        )
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
    lateinit var cloneClosureChecklistInstance: CloneClosureChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence, projectReportPersistence, securityService)
    }

    @ParameterizedTest(name = "create closure checklist - OK - {0}")
    @EnumSource(
        value = ProjectReportStatus::class, mode = EnumSource.Mode.EXCLUDE,
        names = ["Submitted", "InVerification", "Finalized", "ReOpenFinalized"]
    )
    fun `clone closure checklist - OK`(status: ProjectReportStatus) {
        every { projectReportPersistence.getReportByIdUnSecured(reportId) } returns
            report(status)
        every { securityService.getUserIdOrThrow() } returns creatorId
        every { persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CLOSURE, reportId) } returns clonedClosureChecklistDetail
        every { persistence.create(createControlChecklist, creatorId) } returns clonedClosureChecklistDetail
        every { persistence.update(any()) } returns clonedClosureChecklistDetail

        Assertions.assertThat(cloneClosureChecklistInstance.clone(reportId, checklistId)).isEqualTo(clonedClosureChecklistDetail)
    }

    @ParameterizedTest(name = "create closure checklist - failed - report is closed")
    @EnumSource(
        value = ProjectReportStatus::class, mode = EnumSource.Mode.INCLUDE,
        names = ["Submitted", "InVerification", "Finalized", "ReOpenFinalized"]
    )
    fun `clone closure checklist - failed - report is locked`(status: ProjectReportStatus) {
        every { projectReportPersistence.getReportByIdUnSecured(reportId) } returns report(status)
        assertThrows<CloneClosureChecklistInstanceStatusNotAllowedException> {
            cloneClosureChecklistInstance.clone(reportId, checklistId)
        }
        verify(exactly = 0) { persistence.create(any(), any()) }
    }

}
