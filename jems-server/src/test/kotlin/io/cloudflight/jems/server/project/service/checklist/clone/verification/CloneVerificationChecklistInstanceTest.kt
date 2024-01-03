package io.cloudflight.jems.server.project.service.checklist.clone.verification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class CloneVerificationChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val creatorId = 3L
    private val programmeChecklistId = 4L
    private val projectId = 5L
    private val reportId = 6L
    private val TODAY = ZonedDateTime.now()

    private val createControlChecklist = CreateChecklistInstanceModel(
        relatedToId = reportId,
        programmeChecklistId = programmeChecklistId
    )

    private val clonedControlChecklistDetail = ChecklistInstanceDetail(
        checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
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
    lateinit var cloneVerificationChecklistInstance: CloneVerificationChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence, projectReportPersistence, securityService)
    }

    @ParameterizedTest(name = "create verification checklist - OK - {0}")
    @EnumSource(
        value = ProjectReportStatus::class,
        names = ["InVerification", "VerificationReOpenedLast", "VerificationReOpenedLimited", "Finalized", "ReOpenFinalized"]
    )
    fun `clone verification checklist - OK`() {
        every { projectReportPersistence.getReportById(projectId, reportId) } returns
                report(ProjectReportStatus.InVerification)
        every { securityService.getUserIdOrThrow() } returns AuthorizationUtil.userApplicant.id
        every { persistence.getChecklistDetail(checklistId) } returns clonedControlChecklistDetail
        every { persistence.create(createControlChecklist, creatorId) } returns clonedControlChecklistDetail
        every { persistence.update(any()) } returns clonedControlChecklistDetail

        assertThat(cloneVerificationChecklistInstance.clone(projectId, reportId, checklistId)).isEqualTo(clonedControlChecklistDetail)
    }

    @ParameterizedTest(name = "create verification checklist - failed - report is locked {0}")
    @EnumSource(
        value = ProjectReportStatus::class, mode = EnumSource.Mode.EXCLUDE,
        names = ["InVerification", "VerificationReOpenedLast", "VerificationReOpenedLimited", "Finalized", "ReOpenFinalized"]
    )
    fun `clone verification checklist - failed - report is locked`(status: ProjectReportStatus) {
        every { projectReportPersistence.getReportById(projectId, reportId) } returns report(status)
        assertThrows<CloneVerificationChecklistInstanceStatusNotAllowedException> {
            cloneVerificationChecklistInstance.clone(projectId, reportId, checklistId)
        }
        verify(exactly = 0) { persistence.create(any(), any()) }
    }
}
