package io.cloudflight.jems.server.project.service.checklist.get_detail

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getDetail.GetChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistDetailNotAllowedException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class GetChecklistInstanceDetailTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L
    private val CREATOR_ID = 1L

    private val checkLisDetail = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "a@a",
        creatorId = CREATOR_ID,
        relatedToId = RELATED_TO_ID,
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
    lateinit var checklistAuthorization: ProjectChecklistAuthorization

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getChecklistInstance: GetChecklistInstanceDetail

    @Test
    fun getChecklistDetail() {
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checkLisDetail
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistUpdate, RELATED_TO_ID)} returns true
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistConsolidate, RELATED_TO_ID)} returns true
        every { securityService.getUserIdOrThrow()} returns RELATED_TO_ID
        assertThat(getChecklistInstance.getChecklistInstanceDetail(CHECKLIST_ID, RELATED_TO_ID))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun `get checklist detail error without permission`() {
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checkLisDetail
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistUpdate, RELATED_TO_ID)} returns false
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistConsolidate, RELATED_TO_ID)} returns false
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate)} returns false
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedRetrieve, RELATED_TO_ID)} returns false
        every { securityService.getUserIdOrThrow()} returns RELATED_TO_ID
        assertThrows<GetChecklistDetailNotAllowedException> {
            getChecklistInstance.getChecklistInstanceDetail(CHECKLIST_ID, RELATED_TO_ID)
        }
    }

    @Test
    fun `get checklist detail with selected permission`() {
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checkLisDetail
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistUpdate, RELATED_TO_ID)} returns false
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistConsolidate, RELATED_TO_ID)} returns false
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate)} returns false
        every { checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedRetrieve, RELATED_TO_ID)} returns true
        every { securityService.getUserIdOrThrow()} returns RELATED_TO_ID
        assertThat(getChecklistInstance.getChecklistInstanceDetail(CHECKLIST_ID, RELATED_TO_ID))
            .usingRecursiveComparison()
            .isEqualTo(checkLisDetail)
    }
}
