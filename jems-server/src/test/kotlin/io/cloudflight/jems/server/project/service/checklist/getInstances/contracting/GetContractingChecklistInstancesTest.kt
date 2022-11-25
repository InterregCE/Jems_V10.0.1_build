package io.cloudflight.jems.server.project.service.checklist.getInstances.contracting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ContractingChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetContractingChecklistInstancesTest : UnitTest() {

    private val projectId = 1L

    @RelaxedMockK
    lateinit var persistence: ContractingChecklistInstancePersistence

    @MockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var checklistAuthorization: ProjectChecklistAuthorization

    @InjectMockKs
    lateinit var getContractingChecklistInstances: GetContractingChecklistInstances

    @BeforeEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun `get all contracting checklist instances`() {
        val searchRequest = slot<ChecklistInstanceSearchRequest>()
        every { checklistAuthorization.canConsolidate(projectId) } returns true

        getContractingChecklistInstances.getContractingChecklistInstances(projectId)

        verify { persistence.findChecklistInstances(capture(searchRequest)) }
        Assertions.assertThat(searchRequest.captured.relatedToId).isEqualTo(projectId)
        Assertions.assertThat(searchRequest.captured.type).isEqualTo(ProgrammeChecklistType.CONTRACTING)
    }
}
