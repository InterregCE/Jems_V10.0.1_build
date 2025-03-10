package io.cloudflight.jems.server.programme.controller.checklist

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.programme.checklist.ProgrammeChecklistApi
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDetailDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.server.common.toDTO
import io.cloudflight.jems.server.programme.service.checklist.clone.CloneProgrammeChecklistInteractor
import io.cloudflight.jems.server.programme.service.checklist.create.CreateProgrammeChecklistInteractor
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteProgrammeChecklistInteractor
import io.cloudflight.jems.server.programme.service.checklist.getDetail.GetProgrammeChecklistDetailInteractor
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklistInteractor
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateProgrammeChecklistInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeChecklistController(
    private val getChecklistInteractor: GetProgrammeChecklistInteractor,
    private val getChecklistDetailInteractor: GetProgrammeChecklistDetailInteractor,
    private val updateInteractor: UpdateProgrammeChecklistInteractor,
    private val createInteractor: CreateProgrammeChecklistInteractor,
    private val cloneInteractor: CloneProgrammeChecklistInteractor,
    private val deleteInteractor: DeleteProgrammeChecklistInteractor,
) : ProgrammeChecklistApi {

    override fun getProgrammeChecklists(pageable: Pageable): List<ProgrammeChecklistDTO> =
        getChecklistInteractor.getProgrammeChecklist(sort = pageable.sort).toDto()

    override fun getProgrammeChecklistDetail(checklistId: Long): ProgrammeChecklistDetailDTO =
        getChecklistDetailInteractor.getProgrammeChecklistDetail(checklistId).toDetailDto()

    override fun createProgrammeChecklist(checklist: ProgrammeChecklistDetailDTO): ProgrammeChecklistDetailDTO =
        createInteractor.create(checklist.toDetailModel()).toDetailDto()

    override fun cloneProgrammeChecklist(checklistId: Long): ProgrammeChecklistDetailDTO =
        cloneInteractor.clone(checklistId).toDetailDto()

    override fun updateProgrammeChecklist(checklist: ProgrammeChecklistDetailDTO): ProgrammeChecklistDetailDTO =
        updateInteractor.update(checklist.toDetailModel()).toDetailDto()

    override fun deleteChecklist(checklistId: Long) =
        deleteInteractor.deleteProgrammeChecklist(checklistId)

    override fun getProgrammeChecklistsByType(checklistType: ProgrammeChecklistTypeDTO, projectId: Long?): List<IdNamePairDTO> {
        return getChecklistInteractor.getProgrammeChecklistsByType(checklistType.toModel(), projectId).toDTO()
    }
}
