package io.cloudflight.jems.server.project.service.checklist.update.control

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
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
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
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

internal class UpdateControlChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val programmeChecklistId = 4L
    private val creatorId = 1L
    private val notCreatorId = 2L
    private val partnerId = 5L
    private val reportId = 6L
    private val creatorEmail = "a@a"
    private val projectId = 7L
    private val controlReportId = 2
    private val controlChecklistDetail = controlChecklistInstanceDetail()
    private val TODAY = ZonedDateTime.now()

    private fun controlChecklistInstanceDetail(status: ChecklistInstanceStatus = ChecklistInstanceStatus.DRAFT) =
        ChecklistInstanceDetail(
            id = checklistId,
            programmeChecklistId = programmeChecklistId,
            status = status,
            type = ProgrammeChecklistType.CONTROL,
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

    private val controlChecklistDetailWithErrorOnTextInput = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTROL,
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

    private fun controlChecklistInstance(status: ChecklistInstanceStatus, creatorEmail: String = "user@applicant.dev") =
        ChecklistInstance(
            id = checklistId,
            programmeChecklistId = programmeChecklistId,
            status = status,
            type = ProgrammeChecklistType.CONTROL,
            name = "name",
            relatedToId = reportId,
            creatorEmail = creatorEmail,
            finishedDate = null,
            consolidated = false,
            visible = true,
            description = "test"
        )

    private val controlChecklistDetailWithErrorOnScore = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTROL,
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

    private val controlChecklistDetailWithErrorOnOptionsToggle = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTROL,
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

    private val projectPartner = ProjectPartnerDetail(
        projectId = 7,
        id = 2L,
        active = true,
        abbreviation = "partner",
        role = ProjectPartnerRole.PARTNER,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        createdAt = ZonedDateTime.now(),
        sortNumber = 2,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
        nace = NaceGroupLevel.A,
        otherIdentifierNumber = null,
        otherIdentifierDescription = emptySet(),
        pic = null,
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        legalStatusId = 3L,
        addresses = listOf(
            ProjectPartnerAddress(
                type = ProjectPartnerAddressType.Organization,
                country = "country",
                nutsRegion2 = "nutsRegion2",
                nutsRegion3 = "nutsRegion3",
                street = "street",
                houseNumber = "houseNumber",
                postalCode = "postalCode",
                city = "city",
                homepage = "homepage"
            )
        ),
        motivation = ProjectPartnerMotivation(
            organizationRelevance = setOf(InputTranslation(SystemLanguage.EN, "organizationRelevance")),
            organizationExperience = setOf(InputTranslation(SystemLanguage.EN, "organizationExperience")),
            organizationRole = setOf(InputTranslation(SystemLanguage.EN, "organizationRole"))
        )
    )

    private fun getProjectPartnerReport(status: ReportStatus) =
        ProjectPartnerReport(
            id = 2,
            reportNumber = 1,
            status = status,
            version = "1.0",
            firstSubmission = null,
            lastResubmission = null,
            controlEnd = TODAY,
            lastControlReopening = null,
            projectReportId = 24L,
            projectReportNumber = 240,
            identification = PartnerReportIdentification(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 2,
                partnerAbbreviation = "",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = null,
                nameInEnglish = null,
                legalStatus = null,
                partnerType = null,
                vatRecovery = null,
                country = null,
                currency = null,
                coFinancing = emptyList(),
            )
        )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    lateinit var updateControlChecklistInstance: UpdateControlChecklistInstance

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
        updateControlChecklistInstance =
            UpdateControlChecklistInstance(
                persistence,
                auditPublisher,
                checklistInstanceValidator,
                partnerPersistence,
                reportPersistence,
                securityService
            )
    }

    @Test
    fun `update - successful`() {
        every { securityService.currentUser?.user?.id } returns creatorId
        every { persistence.update(controlChecklistDetail) } returns controlChecklistDetail
        every {
            persistence.getChecklistDetail(
                controlChecklistDetail.id,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)
        Assertions.assertThat(updateControlChecklistInstance.update(partnerId, reportId, controlChecklistDetail))
            .isEqualTo(controlChecklistDetail)
    }

    @Test
    fun `update - failed (the attempt is to set to FINISHED status)`() {
        every { securityService.currentUser?.user?.id } returns notCreatorId
        every { persistence.update(controlChecklistDetail) } returns controlChecklistDetail
        every {
            persistence.getChecklistDetail(
                controlChecklistDetail.id,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)

        assertThrows<UpdateControlChecklistInstanceStatusNotAllowedException> {
            updateControlChecklistInstance.update(
                partnerId,
                reportId,
                controlChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED)
            )
        }
    }

    @Test
    fun `update - failed (user is not the creator)`() {
        every { securityService.currentUser?.user?.id } returns notCreatorId

        every { persistence.update(controlChecklistDetail) } returns controlChecklistDetail
        every {
            persistence.getChecklistDetail(
                controlChecklistDetail.id,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)

        assertThrows<UpdateControlChecklistInstanceStatusNotAllowedException> {
            updateControlChecklistInstance.update(
                partnerId,
                reportId,
                controlChecklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
            )
        }
    }

    @ParameterizedTest
    @EnumSource(value = ReportStatus::class, names = ["InControl"], mode = EnumSource.Mode.EXCLUDE)
    fun `update - failed (report not open)`(status: ReportStatus) {
        every { securityService.currentUser?.user?.id } returns notCreatorId

        every { persistence.update(controlChecklistDetail) } returns controlChecklistDetail
        every {
            persistence.getChecklistDetail(
                controlChecklistDetail.id,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(status)

        assertThrows<UpdateControlChecklistInstanceStatusNotAllowedException> {
            updateControlChecklistInstance.update(
                partnerId,
                reportId,
                controlChecklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
            )
        }
    }

    @Test
    fun `change status (should trigger an audit log)`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { securityService.currentUser?.user?.id } returns creatorId
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(controlChecklistDetail) } returns controlChecklistDetail
        every { securityService.getUserIdOrThrow() } returns creatorId
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { partnerPersistence.getById(partnerId) } returns projectPartner
        val report = mockk<ProjectPartnerReport>()
        every { reportPersistence.getPartnerReportById(partnerId, reportId) } returns report
        every { report.reportNumber} returns controlReportId
        every { report.status} returns ReportStatus.InControl
        every { report.controlEnd} returns TODAY
        every { report.lastControlReopening} returns TODAY

        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetail
        every {
            persistence.changeStatus(
                checklistId,
                ChecklistInstanceStatus.FINISHED
            )
        } returns controlChecklistInstance(
            ChecklistInstanceStatus.FINISHED
        )

        updateControlChecklistInstance.changeStatus(partnerId, reportId, checklistId, ChecklistInstanceStatus.FINISHED)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CHECKLIST_STATUS_CHANGE,
                project = AuditProject(id = projectId.toString()),
                description = "Checklist ${controlChecklistDetail.id} type ${controlChecklistDetail.type} name ${controlChecklistDetail.name} " +
                        "for partner PP2 and partner report R.$controlReportId changed status from 'DRAFT' to 'FINISHED'"
            )
        )
    }

    @Test
    fun `update - controlChecklistDetail is already in FINISHED status`() {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetail
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)
        assertThrows<UpdateControlChecklistInstanceStatusNotAllowedException> {
            updateControlChecklistInstance.update(
                partnerId,
                reportId,
                controlChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED)
            )
        }
    }

    @ParameterizedTest
    @EnumSource(value = ReportStatus::class, names = ["InControl", "Certified"], mode = EnumSource.Mode.EXCLUDE)
    fun `update - report control is locked`(status: ReportStatus) {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetail
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(status)
        assertThrows<UpdateControlChecklistInstanceStatusNotAllowedException> {
            updateControlChecklistInstance.update(
                partnerId,
                reportId,
                controlChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED)
            )
        }
    }

    @Test
    fun `update - text input component max length exception`() {
        every { securityService.currentUser?.user?.id } returns creatorId
        every {
            persistence.getChecklistDetail(
                controlChecklistDetailWithErrorOnTextInput.id,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetailWithErrorOnTextInput
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)

        assertThrows<AppInputValidationException> {
            updateControlChecklistInstance.update(
                partnerId,
                reportId,
                controlChecklistDetailWithErrorOnTextInput
            )
        }
    }

    @Test
    fun `update - options toggle justification field max length exception`() {
        every {
            persistence.getChecklistDetail(
                controlChecklistDetailWithErrorOnOptionsToggle.id,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetailWithErrorOnOptionsToggle
        every { persistence.update(controlChecklistDetailWithErrorOnOptionsToggle) } returns controlChecklistDetailWithErrorOnOptionsToggle
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)

        assertThrows<AppInputValidationException> {
            updateControlChecklistInstance.update(
                partnerId,
                reportId,
                controlChecklistDetailWithErrorOnOptionsToggle
            )
        }
    }

    @Test
    fun `update - score justification field max length exception`() {
        every { securityService.currentUser?.user?.id } returns creatorId
        every {
            persistence.getChecklistDetail(
                controlChecklistDetailWithErrorOnScore.id,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetailWithErrorOnScore
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)

        assertThrows<AppInputValidationException> {
            updateControlChecklistInstance.update(
                partnerId,
                reportId,
                controlChecklistDetailWithErrorOnScore
            )
        }
    }

    @Test
    fun `update description`() {
        every { persistence.updateDescription(checklistId, "test") } returns
            controlChecklistInstance(ChecklistInstanceStatus.FINISHED)
        every { persistence.getChecklistSummary(checklistId) } returns controlChecklistInstance(ChecklistInstanceStatus.DRAFT)
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)

        Assertions.assertThat(updateControlChecklistInstance.updateDescription(partnerId, reportId, checklistId, "test"))
            .isEqualTo(controlChecklistInstance(ChecklistInstanceStatus.FINISHED))
    }

    @Test
    fun `update description - invalid case`() {
        every { persistence.getChecklistSummary(checklistId) } returns controlChecklistInstance(ChecklistInstanceStatus.DRAFT)
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)

        assertThrows<UpdateControlChecklistInstanceNotFoundException> {
            updateControlChecklistInstance.updateDescription(
                partnerId,
                99L,
                checklistId,
                "test-update"
            )
        }
    }
}
