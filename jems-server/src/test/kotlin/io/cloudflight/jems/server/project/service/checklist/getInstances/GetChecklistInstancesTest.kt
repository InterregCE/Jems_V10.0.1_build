package io.cloudflight.jems.server.project.service.checklist.getInstances

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.consolidateInstance.ConsolidateChecklistNotAllowed
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class GetChecklistInstancesTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L

    private val checklist = ChecklistInstance(
        id = CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = RELATED_TO_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        visible = false
    )

    @RelaxedMockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var checklistAuthorization: ProjectChecklistAuthorization

    @InjectMockKs
    lateinit var getChecklistInstances: GetChecklistInstances

    @BeforeEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun getChecklistInstancesOfCurrentUserByTypeAndRelatedId() {
        val searchRequest = slot<ChecklistInstanceSearchRequest>()
        val currentUser = LocalCurrentUser(
            AuthorizationUtil.userApplicant, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.CallRetrieve.key))
        )
        every { securityService.currentUser } returns currentUser
        every { persistence.findChecklistInstances(capture(searchRequest)) } returns listOf(checklist)

        assertThat(getChecklistInstances.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(RELATED_TO_ID,
            ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)).containsExactly(checklist)
        assertThat(searchRequest.captured.relatedToId).isEqualTo(RELATED_TO_ID)
        assertThat(searchRequest.captured.type).isEqualTo(ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)
    }

    @Test
    fun `get all instances`() {
        val searchRequest = slot<ChecklistInstanceSearchRequest>()
        every { checklistAuthorization.canConsolidate(RELATED_TO_ID) } returns true

        getChecklistInstances.getChecklistInstancesByTypeAndRelatedId(RELATED_TO_ID, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)

        verify { persistence.findChecklistInstances(capture(searchRequest)) }
        assertThat(searchRequest.captured.relatedToId).isEqualTo(RELATED_TO_ID)
        assertThat(searchRequest.captured.type).isEqualTo(ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)

    }

    @Test
    fun `get all instances - without permission`() {
        every { checklistAuthorization.canConsolidate(RELATED_TO_ID) } returns false

        assertThrows<ConsolidateChecklistNotAllowed> {
            getChecklistInstances.getChecklistInstancesByTypeAndRelatedId(
                RELATED_TO_ID,
                ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT
            )
        }
    }

    @Test
    fun `get instances for selection - without update permission`() {
        val searchRequest = slot<ChecklistInstanceSearchRequest>()
        every {
            checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate)
        } returns false

        getChecklistInstances.getChecklistInstancesForSelection(RELATED_TO_ID, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)

        verify { persistence.findChecklistInstances(capture(searchRequest)) }
        assertThat(searchRequest.captured.relatedToId).isEqualTo(RELATED_TO_ID)
        assertThat(searchRequest.captured.type).isEqualTo(ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)
        assertThat(searchRequest.captured.status).isEqualTo(ChecklistInstanceStatus.FINISHED)
        assertThat(searchRequest.captured.visible).isTrue
    }

    @Test
    fun `get instances for selection - with update permission`() {
        val searchRequest = slot<ChecklistInstanceSearchRequest>()
        every {
            checklistAuthorization.hasPermission(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate)
        } returns true

        getChecklistInstances.getChecklistInstancesForSelection(RELATED_TO_ID, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)

        verify { persistence.findChecklistInstances(capture(searchRequest)) }
        assertThat(searchRequest.captured.relatedToId).isEqualTo(RELATED_TO_ID)
        assertThat(searchRequest.captured.type).isEqualTo(ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)
        assertThat(searchRequest.captured.status).isEqualTo(ChecklistInstanceStatus.FINISHED)
        assertThat(searchRequest.captured.visible).isNull()
    }
}
