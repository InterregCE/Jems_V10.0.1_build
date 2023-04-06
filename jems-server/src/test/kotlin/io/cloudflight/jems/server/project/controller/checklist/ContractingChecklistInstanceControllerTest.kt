package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.*
import io.cloudflight.jems.api.project.dto.checklist.metadata.HeadlineInstanceMetadataDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.controller.contracting.monitoring.ContractingChecklistInstanceController
import io.cloudflight.jems.server.project.service.checklist.create.contracting.CreateContractingChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.delete.contracting.DeleteContractingChecklistInstanceInteractor
import io.cloudflight.jems.server.project.service.checklist.getDetail.contracting.GetContractingChecklistInstanceDetailInteractor
import io.cloudflight.jems.server.project.service.checklist.getInstances.contracting.GetContractingChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.update.contracting.UpdateContractingChecklistInstanceInteractor
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class ContractingChecklistInstanceControllerTest: UnitTest()  {

    companion object {
        const val projectId = 1L
        const val checklistId = 5L
        private val TODAY = ZonedDateTime.now()
    }

    private val checklist = ChecklistInstance(
        id = checklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = 1L,
        programmeChecklistId = 1L,
        visible = false,
        description = "test"
    )

    private val checklistDto = ChecklistInstanceDTO(
        id = checklistId,
        status = ChecklistInstanceStatusDTO.DRAFT,
        type = ProgrammeChecklistTypeDTO.CONTRACTING,
        name = "name",
        creatorEmail = "test@email.com",
        relatedToId = 1L,
        programmeChecklistId = 1L,
        visible = false,
        description = "test",
        finishedDate = null,
        createdAt = null,
    )

    private val checklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        relatedToId = projectId,
        finishedDate = null,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "test",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        creatorEmail = "a@a",
        creatorId = 200L,
        createdAt = TODAY,
        consolidated = false,
        visible = true,
        components = mutableListOf(
            ChecklistComponentInstance(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                0,
                HeadlineMetadata("headline"),
                HeadlineInstanceMetadata()
            )
        )
    )

    private val checklistDetailDTO = ChecklistInstanceDetailDTO(
        id = checklistId,
        status = ChecklistInstanceStatusDTO.DRAFT,
        finishedDate = null,
        name = "test",
        creatorEmail = "a@a",
        createdAt = TODAY,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        components = mutableListOf(
            ChecklistComponentInstanceDTO(
                2L,
                ProgrammeChecklistComponentTypeDTO.HEADLINE,
                0,
                HeadlineMetadataDTO("headline"),
                HeadlineInstanceMetadataDTO()
            )
        )
    )

    private val createChecklist = CreateChecklistInstanceModel(
        projectId,
        1L
    )

    private val createChecklistInstanceDTO = CreateChecklistInstanceDTO(
        projectId,
        1L
    )

    @MockK
    lateinit var updateInteractor: UpdateContractingChecklistInstanceInteractor

    @MockK
    lateinit var getContractingChecklistInteractor: GetContractingChecklistInstancesInteractor

    @MockK
    lateinit var getContractingChecklistDetailInteractor: GetContractingChecklistInstanceDetailInteractor

    @MockK
    lateinit var createInteractor: CreateContractingChecklistInstanceInteractor

    @MockK
    lateinit var deleteInteractor: DeleteContractingChecklistInstanceInteractor

    @InjectMockKs
    lateinit var controller: ContractingChecklistInstanceController

    @Test
    fun `update contracting checklist description`() {
        every { updateInteractor.updateContractingChecklistDescription(projectId, checklistId, "test") } returns checklist
        Assertions.assertThat(controller.updateContractingChecklistDescription(projectId, checklistId, "test"))
            .isEqualTo(checklistDto)
    }

    @Test
    fun `get contracting checklist details`() {
        every { getContractingChecklistDetailInteractor.getContractingChecklistInstanceDetail(projectId, checklistId) } returns checklistDetail
        Assertions.assertThat(controller.getContractingChecklistInstanceDetail(projectId, checklistId))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }

    @Test
    fun `get contracting checklists`() {
        every { getContractingChecklistInteractor.getContractingChecklistInstances(projectId) } returns listOf(checklist)
        Assertions.assertThat(controller.getAllContractingChecklistInstances(projectId))
            .isEqualTo(listOf(checklistDto))
    }

    @Test
    fun `delete contracting checklist`() {
        every { deleteInteractor.deleteById(projectId, checklistId) } just Runs
        assertDoesNotThrow { controller.deleteContractingChecklistInstance(projectId, checklistId) }
    }

    @Test
    fun `create contracting checklist`() {
        every { createInteractor.create(projectId, createChecklist) } returns checklistDetail
        Assertions.assertThat(controller.createContractingChecklistInstance(projectId, createChecklistInstanceDTO))
            .usingRecursiveComparison()
            .isEqualTo(checklistDetailDTO)
    }
}
