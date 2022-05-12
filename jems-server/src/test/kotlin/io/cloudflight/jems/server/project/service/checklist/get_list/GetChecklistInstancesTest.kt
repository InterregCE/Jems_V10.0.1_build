package io.cloudflight.jems.server.project.service.checklist.get_list

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstances
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class GetChecklistInstancesTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val CREATOR_ID = 3L
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

    @Test
    fun getChecklistInstancesOfCurrentUserByTypeAndRelatedId() {
        val currentUser = LocalCurrentUser(
            AuthorizationUtil.userApplicant, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.CallRetrieve.key))
        )
        every { securityService.currentUser } returns currentUser
        every { persistence.getChecklistsByRelationAndCreatorAndType(RELATED_TO_ID, CREATOR_ID,
            ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT) } returns listOf(checklist)
        assertThat(getChecklistInstances.getChecklistInstancesOfCurrentUserByTypeAndRelatedId(RELATED_TO_ID,
            ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)).containsExactly(checklist)
    }

    @Test
    fun `get all instances`() {
        every { checklistAuthorization.canConsolidate(1) } returns true

        getChecklistInstances.getChecklistInstancesByTypeAndRelatedId(1, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)

        verify { persistence.getChecklistsByRelatedIdAndType(1, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT) }
    }
}
