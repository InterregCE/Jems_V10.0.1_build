package io.cloudflight.jems.server.project.service.checklist.get_detail.closure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getDetail.closure.GetClosureChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.getInstances.closure.GetClosureChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.getInstances.closure.GetClosureChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime

class GetClosureChecklistInstanceDetailTest: UnitTest() {
    private val checklistId = 100L
    private val userId = 2L
    private val programmeChecklistId = 4L
    private val creatorId = 1L
    private val projectId = 5L
    private val reportId = 6L
    private val TODAY = ZonedDateTime.now()

    private val closureChecklist = ChecklistInstance(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CLOSURE,
        name = "name",
        creatorEmail = "a@a",
        relatedToId = reportId,
        finishedDate = null,
        consolidated = false,
        visible = true,
        description = "test"
    )

    private val closureChecklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CLOSURE,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        createdAt = TODAY,
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
    lateinit var getClosureChecklistInteractor: GetClosureChecklistInstancesInteractor

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getClosureChecklistInstance: GetClosureChecklistInstanceDetail

    @Test
    fun `get closure checklist detail`() {
        every { getClosureChecklistInteractor.getClosureChecklistInstances(projectId, reportId) } returns listOf(
            closureChecklist
        )
        every { persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CLOSURE, reportId) } returns closureChecklistDetail
        every { securityService.getUserIdOrThrow() } returns userId
        assertThat(getClosureChecklistInstance.getClosureChecklistInstanceDetail(projectId, reportId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(closureChecklistDetail)
    }

    @Test
    fun `get closure checklist detail - checklist does not belong to the report provided`() {
        every {
            persistence.getChecklistDetail(101, ProgrammeChecklistType.CLOSURE, reportId)
        } throws GetClosureChecklistInstanceDetailNotFoundException()

        assertThrows<GetClosureChecklistInstanceDetailNotFoundException> {
            getClosureChecklistInstance.getClosureChecklistInstanceDetail(projectId, reportId, 101)
        }
    }

}
