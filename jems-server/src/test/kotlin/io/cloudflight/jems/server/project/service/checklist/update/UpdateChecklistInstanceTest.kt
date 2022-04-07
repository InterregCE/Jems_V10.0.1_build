package io.cloudflight.jems.server.project.service.checklist.update

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateChecklistInstanceStatusNotAllowedException
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateChecklistInstanceTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L

    private val checkLisDetail = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.FINISHED,
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        components = mutableListOf(
            ChecklistComponentInstance(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                HeadlineMetadata("headline"),
                HeadlineInstanceMetadata()
            ),
            ChecklistComponentInstance(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe"),
                OptionsToggleInstanceMetadata("yes")
            )
        )
    )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @InjectMockKs
    lateinit var updateChecklistInstance: UpdateChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
    }

    @Test
    fun `update - successfully`() {
        every { persistence.update(checkLisDetail) } returns checkLisDetail
        every { persistence.getStatus(checkLisDetail.id) } returns ChecklistInstanceStatus.DRAFT
        Assertions.assertThat(updateChecklistInstance.update(checkLisDetail))
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun `update - checklist is already in FINISHED status`() {
        every { persistence.update(checkLisDetail) } returns checkLisDetail
        every { persistence.getStatus(checkLisDetail.id) } returns ChecklistInstanceStatus.FINISHED
        assertThrows<UpdateChecklistInstanceStatusNotAllowedException> { updateChecklistInstance.update(checkLisDetail) }
    }

}
