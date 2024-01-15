package io.cloudflight.jems.server.project.service.checklist.clone.control

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
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
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class CloneControlChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val creatorId = 3L
    private val programmeChecklistId = 4L
    private val partnerId = 5L
    private val reportId = 6L
    private val TODAY = ZonedDateTime.now()

    private val createChecklist = CreateChecklistInstanceModel(
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

    private fun getProjectPartnerReportStatusAndVersion(status: ReportStatus) =
        ProjectPartnerReportStatusAndVersion(
            reportId = 647L,
            status = status,
            version = "1.0"
        )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var cloneControlChecklistInstance: CloneControlChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
                AppInputValidationException(emptyMap())
    }

    @Test
    fun `clone control checklist - OK`() {
        every {
            reportPersistence.getPartnerReportStatusAndVersion(partnerId, reportId)
        } returns getProjectPartnerReportStatusAndVersion(ReportStatus.InControl)
        every { securityService.getUserIdOrThrow() } returns AuthorizationUtil.userApplicant.id
        every { persistence.getChecklistDetail(checklistId) } returns clonedControlChecklistDetail
        every { persistence.create(createChecklist, creatorId) } returns clonedControlChecklistDetail
        every { persistence.update(any()) } returns clonedControlChecklistDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        Assertions.assertThat(cloneControlChecklistInstance.clone(partnerId, reportId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(clonedControlChecklistDetail)
    }

    @ParameterizedTest
    @EnumSource(
        value = ReportStatus::class, mode = EnumSource.Mode.EXCLUDE,
        names = ["InControl", "ReOpenInControlLast", "ReOpenInControlLimited", "ReOpenCertified", "Certified"]
    )
    fun `clone control checklist - failed - report is locked`(status: ReportStatus) {
        every {
            reportPersistence.getPartnerReportStatusAndVersion(partnerId, reportId)
        } returns getProjectPartnerReportStatusAndVersion(status)
        assertThrows<CloneControlChecklistInstanceStatusNotAllowedException> {
            cloneControlChecklistInstance.clone(partnerId, reportId, checklistId)
        }
    }
}
