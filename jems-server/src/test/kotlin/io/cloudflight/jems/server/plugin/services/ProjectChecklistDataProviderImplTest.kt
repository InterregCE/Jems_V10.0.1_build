package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.checklist.*
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ScoreMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.ScoreInstanceMetadata
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

internal class ProjectChecklistDataProviderImplTest : UnitTest() {

    companion object {
        private val TODAY = LocalDate.now()

        private val detail = ChecklistInstanceDetail(
            id = 14L,
            programmeChecklistId = 55L,
            status = ChecklistInstanceStatus.DRAFT,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            creatorEmail = "creator@email",
            creatorId = 32L,
            finishedDate = TODAY,
            relatedToId = 18L,
            consolidated = true,
            visible = false,
            minScore = BigDecimal.ZERO,
            maxScore = BigDecimal.TEN,
            allowsDecimalScore = true,
            components = listOf(
                ChecklistComponentInstance(
                    id = 1L,
                    type = ProgrammeChecklistComponentType.SCORE,
                    position = 1,
                    programmeMetadata = ScoreMetadata(question = "is it?", weight = BigDecimal.ONE),
                    instanceMetadata = ScoreInstanceMetadata(score = BigDecimal.ONE, justification = "not provided"),
                ),
            ),
        )

        private val expectedDetail = ChecklistInstanceData(
            id = 14L,
            status = ChecklistStatusData.DRAFT,
            type = ChecklistTypeData.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            creatorEmail = "creator@email",
            creatorId = 32L,
            finishedDate = TODAY,
            relatedToId = 18L,
            consolidated = true,
            visible = false,
            minScore = BigDecimal.ZERO,
            maxScore = BigDecimal.TEN,
            allowsDecimalScore = true,
            questions = listOf(
                ChecklistQuestionData(
                    id = 1L,
                    type = ChecklistQuestionTypeData.SCORE,
                    position = 1,
                    question = "is it?",
                    weight = BigDecimal.ONE,
                    score = BigDecimal.ONE,
                    questionMetadataJson = "{\"score\":1,\"justification\":\"not provided\"}",
                    answerMetadataJson = "{\"question\":\"is it?\",\"weight\":1}",
                ),
            ),
        )

        private val summary = ChecklistInstance(
            id = 16L,
            programmeChecklistId = 55L,
            status = ChecklistInstanceStatus.DRAFT,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            creatorEmail = "creator@email",
            finishedDate = TODAY,
            relatedToId = 18L,
            consolidated = true,
            visible = false,
        )

        private val expectedSummary = ChecklistSummaryData(
            id = 16L,
            status = ChecklistStatusData.DRAFT,
            type = ChecklistTypeData.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            creatorEmail = "creator@email",
            finishedDate = TODAY,
            relatedToId = 18L,
            consolidated = true,
            visible = false,
        )

    }

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @InjectMockKs
    lateinit var dataProvider: ProjectChecklistDataProviderImpl

    @Test
    fun getChecklistDetail() {
        every { persistence.getChecklistDetail(14L) } returns detail
        assertThat(dataProvider.getChecklistDetail(14L)).isEqualTo(expectedDetail)
    }

    @Test
    fun getChecklistsForProject() {
        val slotSearch = slot<ChecklistInstanceSearchRequest>()
        every { persistence.findChecklistInstances(capture(slotSearch)) } returns listOf(summary)
        assertThat(dataProvider.getChecklistsForProject(18L, ChecklistTypeData.APPLICATION_FORM_ASSESSMENT))
            .containsExactly(expectedSummary)

        assertThat(slotSearch.captured).isEqualTo(
            ChecklistInstanceSearchRequest(
                relatedToId = 18L,
                type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            )
        )
    }

}
