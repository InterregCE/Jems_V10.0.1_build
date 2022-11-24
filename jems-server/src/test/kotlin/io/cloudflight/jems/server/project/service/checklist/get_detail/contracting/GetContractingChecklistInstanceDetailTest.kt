package io.cloudflight.jems.server.project.service.checklist.get_detail.contracting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getDetail.contracting.GetContractingChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.getInstances.contracting.GetContractingChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.getInstances.contracting.GetContractingChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class GetContractingChecklistInstanceDetailTest : UnitTest() {

    private val checklistId = 100L
    private val projectId = 2L
    private val programmeChecklistId = 4L
    private val creatorId = 1L

    private val contractingChecklist = ChecklistInstance(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        creatorEmail = "a@a",
        relatedToId = projectId,
        finishedDate = null,
        consolidated = false,
        visible = true,
        description = "test"
    )

    private val contractingChecklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        relatedToId = projectId,
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
    lateinit var getContractingChecklistInteractor: GetContractingChecklistInstancesInteractor

    @InjectMockKs
    lateinit var getContractingChecklistInstance: GetContractingChecklistInstanceDetail

    @Test
    fun `get contracting checklist detail`() {
        every { getContractingChecklistInteractor.getContractingChecklistInstances(projectId) } returns listOf(
            contractingChecklist
        )
        every { persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CONTRACTING, 2) } returns contractingChecklistDetail
        Assertions.assertThat(
            getContractingChecklistInstance.getContractingChecklistInstanceDetail(
                projectId,
                checklistId
            )
        )
            .usingRecursiveComparison()
            .isEqualTo(contractingChecklistDetail)
    }

    @Test
    fun `get contracting checklist detail - checklist does not belong to the project provided`() {
        every { persistence.getChecklistDetail(101, ProgrammeChecklistType.CONTRACTING, 2) } throws GetContractingChecklistInstanceDetailNotFoundException()
        assertThrows<GetContractingChecklistInstanceDetailNotFoundException> {
            getContractingChecklistInstance.getContractingChecklistInstanceDetail(
                projectId,
                101
            )
        }
    }
}

