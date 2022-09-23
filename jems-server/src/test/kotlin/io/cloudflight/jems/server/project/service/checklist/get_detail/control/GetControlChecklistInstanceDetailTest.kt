package io.cloudflight.jems.server.project.service.checklist.get_detail.control

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getDetail.control.GetControlChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetControlChecklistInstanceDetailTest : UnitTest() {

    private val checklistId = 100L
    private val relatedToId = 2L
    private val programmeChecklistId = 4L
    private val creatorId = 1L
    private val partnerId = 5L
    private val reportId = 6L

    private val controlCheckLisDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        relatedToId = reportId,
        finishedDate = null,
        consolidated = false,
        visible = true,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
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
                3,
                TextInputMetadata("Question to be answered", "Label", 2000),
                TextInputInstanceMetadata("Explanation")
            )
        )
    )


    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getControlChecklistInstance: GetControlChecklistInstanceDetail

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @RelaxedMockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @Test
    fun getChecklistDetail() {
        every { persistence.getChecklistDetail(checklistId) } returns controlCheckLisDetail
        every { securityService.getUserIdOrThrow()} returns relatedToId
        Assertions.assertThat(getControlChecklistInstance.getControlChecklistInstanceDetail(partnerId, reportId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(controlCheckLisDetail)
    }
}