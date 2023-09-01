package io.cloudflight.jems.server.project.service.checklist.update.verification

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
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ScoreMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.ScoreInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher

internal class UpdateVerificationChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val programmeChecklistId = 4L
    private val creatorId = 1L
    private val notCreatorId = 2L
    private val reportId = 6L
    private val creatorEmail = "a@a"
    private val projectId = 7L
    private val verificationReportId = 2
    private val verificationChecklistDetail = verificationChecklistInstanceDetail()
    private val TODAY = ZonedDateTime.now()

    private fun verificationChecklistInstanceDetail(status: ChecklistInstanceStatus = ChecklistInstanceStatus.DRAFT) =
        ChecklistInstanceDetail(
            id = checklistId,
            programmeChecklistId = programmeChecklistId,
            status = status,
            type = ProgrammeChecklistType.VERIFICATION,
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

    private val optionsToggleComponentInstance = ChecklistComponentInstance(
        4L,
        ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
        0,
        OptionsToggleMetadata("Question to be answered", "yes", "no", "", ""),
        OptionsToggleInstanceMetadata("yes", "A".repeat(5001))
    )

    private val textInputComponentInstance = ChecklistComponentInstance(
        4L,
        ProgrammeChecklistComponentType.TEXT_INPUT,
        0,
        TextInputMetadata("Question to be answered", "Label", 2000),
        TextInputInstanceMetadata("A".repeat(3000))
    )

    private val scoreComponentInstance = ChecklistComponentInstance(
        5L,
        ProgrammeChecklistComponentType.SCORE,
        0,
        ScoreMetadata("Question to be answered", BigDecimal(1)),
        ScoreInstanceMetadata(BigDecimal(5), "A".repeat(5001))
    )

    private val verificationChecklistDetailWithErrorOnTextInput = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.VERIFICATION,
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

    private fun verificationChecklistInstance(status: ChecklistInstanceStatus, creatorEmail: String = "user@applicant.dev") =
        ChecklistInstance(
            id = checklistId,
            programmeChecklistId = programmeChecklistId,
            status = status,
            type = ProgrammeChecklistType.VERIFICATION,
            name = "name",
            relatedToId = reportId,
            creatorEmail = creatorEmail,
            finishedDate = null,
            consolidated = false,
            visible = true,
            description = "test"
        )

    private val verificationChecklistDetailWithErrorOnScore = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.VERIFICATION,
        name = "name",
        relatedToId = reportId,
        finishedDate = null,
        createdAt = TODAY,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = true,
        creatorEmail = creatorEmail,
        creatorId = creatorId,
        components = mutableListOf(scoreComponentInstance)
    )

    private val verificationChecklistDetailWithErrorOnOptionsToggle = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.VERIFICATION,
        name = "name",
        relatedToId = reportId,
        finishedDate = null,
        createdAt = TODAY,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        consolidated = false,
        visible = true,
        creatorEmail = creatorEmail,
        creatorId = creatorId,
        allowsDecimalScore = false,
        components = mutableListOf(optionsToggleComponentInstance)
    )

    private fun report(status:ProjectReportStatus): ProjectReportModel {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { report.verificationEndDate } returns TODAY
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

    lateinit var updateVerificationChecklistInstance: UpdateVerificationChecklistInstance

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
        updateVerificationChecklistInstance =
            UpdateVerificationChecklistInstance(
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
        every { persistence.update(verificationChecklistDetail) } returns verificationChecklistDetail
        every {
            persistence.getChecklistDetail(
                verificationChecklistDetail.id,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)
        Assertions.assertThat(updateVerificationChecklistInstance.update(projectId, reportId, verificationChecklistDetail))
            .isEqualTo(verificationChecklistDetail)
    }

    @Test
    fun `update - failed (the attempt is to set to FINISHED status)`() {
        every { securityService.currentUser?.user?.id } returns notCreatorId
        every { persistence.update(verificationChecklistDetail) } returns verificationChecklistDetail
        every {
            persistence.getChecklistDetail(
                verificationChecklistDetail.id,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)

        assertThrows<UpdateVerificationChecklistInstanceStatusNotAllowedException> {
            updateVerificationChecklistInstance.update(
                projectId,
                reportId,
                verificationChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED)
            )
        }
    }

    @Test
    fun `update - failed (user is not the creator)`() {
        every { securityService.currentUser?.user?.id } returns notCreatorId

        every { persistence.update(verificationChecklistDetail) } returns verificationChecklistDetail
        every {
            persistence.getChecklistDetail(
                verificationChecklistDetail.id,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)

        assertThrows<UpdateVerificationChecklistInstanceStatusNotAllowedException> {
            updateVerificationChecklistInstance.update(
                projectId,
                reportId,
                verificationChecklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
            )
        }
    }

    @ParameterizedTest
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"], mode = EnumSource.Mode.EXCLUDE)
    fun `update - failed (report not open)`(status: ProjectReportStatus) {
        every { securityService.currentUser?.user?.id } returns notCreatorId

        every { persistence.update(verificationChecklistDetail) } returns verificationChecklistDetail
        every {
            persistence.getChecklistDetail(
                verificationChecklistDetail.id,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(status)

        assertThrows<UpdateVerificationChecklistInstanceStatusNotAllowedException> {
            updateVerificationChecklistInstance.update(
                projectId,
                reportId,
                verificationChecklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
            )
        }
    }

    @Test
    fun `change status (should trigger an audit log)`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { securityService.currentUser?.user?.id } returns creatorId
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(verificationChecklistDetail) } returns verificationChecklistDetail
        every { securityService.getUserIdOrThrow() } returns creatorId
        val report = mockk<ProjectReportModel>()
        every { reportPersistence.getReportById(projectId, reportId) } returns report
        every { report.reportNumber} returns verificationReportId
        every { report.status} returns ProjectReportStatus.InVerification
        every { report.verificationEndDate} returns TODAY

        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistDetail
        every {
            persistence.changeStatus(
                checklistId,
                ChecklistInstanceStatus.FINISHED
            )
        } returns verificationChecklistInstance(
            ChecklistInstanceStatus.FINISHED
        )

        updateVerificationChecklistInstance.changeStatus(projectId, reportId, checklistId, ChecklistInstanceStatus.FINISHED)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CHECKLIST_STATUS_CHANGE,
                project = AuditProject(id = projectId.toString()),
                description = "Checklist ${verificationChecklistDetail.id} type ${verificationChecklistDetail.type} name ${verificationChecklistDetail.name} " +
                        "for project report R.$verificationReportId changed status from 'DRAFT' to 'FINISHED'"
            )
        )
    }

    @Test
    fun `update - verificationChecklistDetail is already in FINISHED status`() {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistDetail
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)
        assertThrows<UpdateVerificationChecklistInstanceStatusNotAllowedException> {
            updateVerificationChecklistInstance.update(
                projectId,
                reportId,
                verificationChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED)
            )
        }
    }

    @ParameterizedTest
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification", "Finalized"], mode = EnumSource.Mode.EXCLUDE)
    fun `update - report verification is locked`(status: ProjectReportStatus) {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistDetail
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(status)
        assertThrows<UpdateVerificationChecklistInstanceStatusNotAllowedException> {
            updateVerificationChecklistInstance.update(
                projectId,
                reportId,
                verificationChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED)
            )
        }
    }

    @Test
    fun `update - text input component max length exception`() {
        every { securityService.currentUser?.user?.id } returns creatorId
        every {
            persistence.getChecklistDetail(
                verificationChecklistDetailWithErrorOnTextInput.id,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistDetailWithErrorOnTextInput
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)

        assertThrows<AppInputValidationException> {
            updateVerificationChecklistInstance.update(
                projectId,
                reportId,
                verificationChecklistDetailWithErrorOnTextInput
            )
        }
    }

    @Test
    fun `update - options toggle justification field max length exception`() {
        every {
            persistence.getChecklistDetail(
                verificationChecklistDetailWithErrorOnOptionsToggle.id,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistDetailWithErrorOnOptionsToggle
        every { persistence.update(verificationChecklistDetailWithErrorOnOptionsToggle) } returns verificationChecklistDetailWithErrorOnOptionsToggle
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)

        assertThrows<AppInputValidationException> {
            updateVerificationChecklistInstance.update(
                projectId,
                reportId,
                verificationChecklistDetailWithErrorOnOptionsToggle
            )
        }
    }

    @Test
    fun `update - score justification field max length exception`() {
        every { securityService.currentUser?.user?.id } returns creatorId
        every {
            persistence.getChecklistDetail(
                verificationChecklistDetailWithErrorOnScore.id,
                ProgrammeChecklistType.VERIFICATION,
                reportId
            )
        } returns verificationChecklistDetailWithErrorOnScore
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)

        assertThrows<AppInputValidationException> {
            updateVerificationChecklistInstance.update(
                projectId,
                reportId,
                verificationChecklistDetailWithErrorOnScore
            )
        }
    }

    @Test
    fun `update description`() {
        every { persistence.updateDescription(checklistId, "test") } returns
                verificationChecklistInstance(ChecklistInstanceStatus.FINISHED)
        every { persistence.getChecklistSummary(checklistId) } returns verificationChecklistInstance(ChecklistInstanceStatus.DRAFT)
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)

        Assertions.assertThat(updateVerificationChecklistInstance.updateDescription(projectId, reportId, checklistId, "test"))
            .isEqualTo(verificationChecklistInstance(ChecklistInstanceStatus.FINISHED))
    }

    @Test
    fun `update description - invalid case`() {
        every { persistence.getChecklistSummary(checklistId) } returns verificationChecklistInstance(ChecklistInstanceStatus.DRAFT)
        every{
            reportPersistence.getReportById(projectId, reportId)
        } returns report(ProjectReportStatus.InVerification)

        assertThrows<UpdateVerificationChecklistInstanceNotFoundException> {
            updateVerificationChecklistInstance.updateDescription(
                projectId,
                99L,
                checklistId,
                "test-update"
            )
        }
    }
}
