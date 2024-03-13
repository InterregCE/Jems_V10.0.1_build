package io.cloudflight.jems.server.project.service.checklist.update.closure

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

class UpdateClosureChecklistInstanceTest: UnitTest() {

    private val checklistId = 100L
    private val programmeChecklistId = 4L
    private val creatorId = 1L
    private val notCreatorId = 2L
    private val reportId = 6L
    private val creatorEmail = "a@a"
    private val projectId = 7L
    private val closureChecklistDetail = closureChecklistInstanceDetail()
    private val TODAY = ZonedDateTime.now()

    private fun closureChecklistInstanceDetail(status: ChecklistInstanceStatus = ChecklistInstanceStatus.DRAFT) =
        ChecklistInstanceDetail(
            id = checklistId,
            programmeChecklistId = programmeChecklistId,
            status = status,
            type = ProgrammeChecklistType.CLOSURE,
            name = "name",
            relatedToId = reportId,
            creatorEmail = creatorEmail,
            creatorId = creatorId,
            createdAt = TODAY,
            finishedDate = null,
            minScore = BigDecimal(0),
            maxScore = BigDecimal(10),
            consolidated = false,
            visible = false,
            components = mutableListOf(
                ChecklistComponentInstance(
                    2L,
                    ProgrammeChecklistComponentType.HEADLINE,
                    1,
                    HeadlineMetadata("headline"),
                    HeadlineInstanceMetadata()
                ),
                ChecklistComponentInstance(
                    3L,
                    ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                    2,
                    OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe", ""),
                    OptionsToggleInstanceMetadata("yes", "test")
                ),
                ChecklistComponentInstance(
                    4L,
                    ProgrammeChecklistComponentType.TEXT_INPUT,
                    2,
                    TextInputMetadata("Question to be answered", "Label", 2000),
                    TextInputInstanceMetadata("Explanation")
                )
            )
        )

    private val textInputComponentInstance = ChecklistComponentInstance(
        4L,
        ProgrammeChecklistComponentType.TEXT_INPUT,
        0,
        TextInputMetadata("Question to be answered", "Label", 2000),
        TextInputInstanceMetadata("A".repeat(3000))
    )

    private val closureChecklistDetailWithErrorOnTextInput = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CLOSURE,
        name = "name",
        creatorEmail = creatorEmail,
        creatorId = creatorId,
        createdAt = TODAY,
        relatedToId = reportId,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = false,
        components = mutableListOf(textInputComponentInstance)
    )

    private fun closureChecklistInstance(status: ChecklistInstanceStatus, creatorEmail: String = "user@applicant.dev") =
        ChecklistInstance(
            id = checklistId,
            programmeChecklistId = programmeChecklistId,
            status = status,
            type = ProgrammeChecklistType.CLOSURE,
            name = "name",
            relatedToId = reportId,
            creatorEmail = creatorEmail,
            finishedDate = null,
            consolidated = false,
            visible = true,
            description = "test"
        )

    private fun report(status: ProjectReportStatus): ProjectReportModel {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        return report
    }

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    lateinit var updateClosureChecklistInstance: UpdateClosureChecklistInstance

    lateinit var generalValidator: GeneralValidatorService

    lateinit var checklistInstanceValidator: ChecklistInstanceValidator

    @BeforeEach
    fun setup() {
        clearMocks(persistence, auditPublisher)
        MockKAnnotations.init(this)
        every { userAuthorization.hasPermissionForProject(any(), any()) } returns false
        generalValidator = GeneralValidatorDefaultImpl()
        checklistInstanceValidator = mockk()
        checklistInstanceValidator = ChecklistInstanceValidator(generalValidator)
        updateClosureChecklistInstance =
            UpdateClosureChecklistInstance(
                persistence,
                auditPublisher,
                checklistInstanceValidator,
                reportPersistence,
                securityService
            )
    }

    @Test
    fun `update - successful`() {
        every { securityService.currentUser?.user?.id } returns creatorId
        every { persistence.update(closureChecklistDetail) } returns closureChecklistDetail
        every {
            persistence.getChecklistDetail(
                closureChecklistDetail.id,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getReportByIdUnSecured(reportId)
        } returns report(ProjectReportStatus.Draft)
        assertThat(updateClosureChecklistInstance.update(reportId, closureChecklistDetail)).isEqualTo(closureChecklistDetail)
    }

    @Test
    fun `update - failed (the attempt is to set to FINISHED status)`() {
        every { securityService.currentUser?.user?.id } returns notCreatorId
        every { persistence.update(closureChecklistDetail) } returns closureChecklistDetail
        every {
            persistence.getChecklistDetail(
                closureChecklistDetail.id,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getReportByIdUnSecured(reportId)
        } returns report(ProjectReportStatus.Draft)

        assertThrows<UpdateClosureChecklistInstanceNotAllowedException> {
            updateClosureChecklistInstance.update(
                reportId,
                closureChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED)
            )
        }
    }

    @Test
    fun `update - failed (user is not the creator)`() {
        every { securityService.currentUser?.user?.id } returns notCreatorId
        every { persistence.update(closureChecklistDetail) } returns closureChecklistDetail
        every {
            persistence.getChecklistDetail(
                closureChecklistDetail.id,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getReportByIdUnSecured(reportId)
        } returns report(ProjectReportStatus.Draft)

        assertThrows<UpdateClosureChecklistInstanceNotAllowedException> {
            updateClosureChecklistInstance.update(
                reportId,
                closureChecklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
            )
        }
    }

    @ParameterizedTest
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification", "Finalized", "ReOpenFinalized"], mode = EnumSource.Mode.INCLUDE)
    fun `update - failed (report not open)`(status: ProjectReportStatus) {
        every { securityService.currentUser?.user?.id } returns notCreatorId

        every { persistence.update(closureChecklistDetail) } returns closureChecklistDetail
        every {
            persistence.getChecklistDetail(
                closureChecklistDetail.id,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report(status)

        assertThrows<UpdateClosureChecklistInstanceNotAllowedException> {
            updateClosureChecklistInstance.update(
                reportId,
                closureChecklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
            )
        }
    }

    @Test
    fun `change status (should trigger an audit log)`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { securityService.currentUser?.user?.id } returns creatorId
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(closureChecklistDetail) } returns closureChecklistDetail
        every { securityService.getUserIdOrThrow() } returns creatorId
        val report = mockk<ProjectReportModel>()
        every { report.reportNumber} returns 1
        every { report.status} returns ProjectReportStatus.Draft
        every { report.finalReport} returns true
        every { report.projectId} returns projectId
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report

        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistDetail
        every {
            persistence.changeStatus(
                checklistId,
                ChecklistInstanceStatus.FINISHED
            )
        } returns closureChecklistInstance(
            ChecklistInstanceStatus.FINISHED
        )

        updateClosureChecklistInstance.changeStatus(reportId, checklistId, ChecklistInstanceStatus.FINISHED)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CHECKLIST_STATUS_CHANGE,
                project = AuditProject(id = projectId.toString()),
                description = "Checklist ${closureChecklistDetail.id} type ${closureChecklistDetail.type} name ${closureChecklistDetail.name} " +
                    "for project report R.1 changed status from 'DRAFT' to 'FINISHED'"
            )
        )
    }

    @Test
    fun `update - closureChecklistDetail is already in FINISHED status`() {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistDetail
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report(ProjectReportStatus.Draft)

        assertThrows<UpdateClosureChecklistInstanceNotAllowedException> {
            updateClosureChecklistInstance.update(
                reportId,
                closureChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED)
            )
        }
    }

    @Test
    fun `update - text input component max length exception`() {
        every { securityService.currentUser?.user?.id } returns creatorId
        every {
            persistence.getChecklistDetail(
                closureChecklistDetailWithErrorOnTextInput.id,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistDetailWithErrorOnTextInput
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report(ProjectReportStatus.Draft)

        assertThrows<AppInputValidationException> {
            updateClosureChecklistInstance.update(
                reportId,
                closureChecklistDetailWithErrorOnTextInput
            )
        }
    }

    @Test
    fun `update description`() {
        every { persistence.updateDescription(checklistId, "test") } returns
            closureChecklistInstance(ChecklistInstanceStatus.FINISHED)
        every { persistence.getChecklistSummary(checklistId) } returns closureChecklistInstance(ChecklistInstanceStatus.DRAFT)
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report(ProjectReportStatus.Draft)

        assertThat(updateClosureChecklistInstance.updateDescription(reportId, checklistId, "test"))
            .isEqualTo(closureChecklistInstance(ChecklistInstanceStatus.FINISHED))
    }

    @Test
    fun `update description - invalid case`() {
        every { persistence.getChecklistSummary(checklistId) } returns closureChecklistInstance(ChecklistInstanceStatus.DRAFT)
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report(ProjectReportStatus.Draft)

        assertThrows<UpdateClosureChecklistInstanceNotFoundException> {
            updateClosureChecklistInstance.updateDescription(99L, checklistId, "test-update")
        }
    }
}
