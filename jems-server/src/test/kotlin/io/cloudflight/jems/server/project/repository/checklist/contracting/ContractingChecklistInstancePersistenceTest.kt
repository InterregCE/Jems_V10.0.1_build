package io.cloudflight.jems.server.project.repository.checklist.contracting

import com.querydsl.core.types.Predicate
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.repository.checklist.ProgrammeChecklistRepository
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.repository.checklist.ChecklistInstanceRepository
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ContractingChecklistInstancePersistenceTest : UnitTest() {

    @RelaxedMockK
    lateinit var repository: ChecklistInstanceRepository

    @MockK
    lateinit var userRepo: UserRepository

    @MockK
    lateinit var programmeChecklistRepository: ProgrammeChecklistRepository

    @InjectMockKs
    private lateinit var persistence: ContractingChecklistInstancePersistenceProvider

    @Test
    fun `find contracting checklists`() {
        val predicate = slot<Predicate>()
        persistence.findChecklistInstances(
            ChecklistInstanceSearchRequest(
                relatedToId = 1,
                type = ProgrammeChecklistType.CONTRACTING,
                status = ChecklistInstanceStatus.FINISHED,
                visible = true,
            )
        )

        verify { repository.findAll(capture(predicate)) }
        Assertions.assertThat(predicate.captured.toString()).isEqualTo(
            "checklistInstanceEntity.relatedToId = 1 " +
                    "&& checklistInstanceEntity.programmeChecklist.type = CONTRACTING " +
                    "&& checklistInstanceEntity.status = FINISHED " +
                    "&& checklistInstanceEntity.visible = true"
        )
    }
}
