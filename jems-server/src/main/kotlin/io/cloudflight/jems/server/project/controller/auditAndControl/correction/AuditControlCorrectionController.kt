package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.project.auditAndControl.corrections.ProjectAuditControlCorrectionApi
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionCostItemDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationUpdateDTO
import io.cloudflight.jems.server.common.toDTO
import io.cloudflight.jems.server.project.controller.auditAndControl.toDto
import io.cloudflight.jems.server.project.service.auditAndControl.correction.closeAuditControlCorrection.CloseAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.createAuditControlCorrection.CreateAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.deleteAuditControlCorrection.DeleteAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.getAuditControlCorrection.GetAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection.ListAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.UpdateAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionAvailableProcurements.GetCorrectionAvailableProcurementsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionCostItems.GetCorrectionCostItemsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.listPreviouslyClosedCorrection.ListPreviouslyClosedCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class AuditControlCorrectionController(
    private val createCorrection: CreateAuditControlCorrectionInteractor,
    private val listAuditCorrections: ListAuditControlCorrectionInteractor,
    private val getAuditCorrection: GetAuditControlCorrectionInteractor,
    private val deleteAuditCorrection: DeleteAuditControlCorrectionInteractor,
    private val closeCorrection: CloseAuditControlCorrectionInteractor,
    private val updateCorrection: UpdateAuditControlCorrectionInteractor,
    private val listPreviouslyClosedCorrection: ListPreviouslyClosedCorrectionInteractor,
    private val getAvailableProcurements: GetCorrectionAvailableProcurementsInteractor,
    private val getCorrectionCostItems: GetCorrectionCostItemsInteractor,
) : ProjectAuditControlCorrectionApi {

    override fun createProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        type: AuditControlCorrectionTypeDTO,
    ): ProjectAuditControlCorrectionDTO =
        createCorrection
            .createCorrection(auditControlId, type = AuditControlCorrectionType.valueOf(type.name))
            .toDto()

    override fun listProjectAuditCorrections(
        projectId: Long,
        auditControlId: Long,
        pageable: Pageable
    ): Page<ProjectAuditControlCorrectionLineDTO> =
        listAuditCorrections.listCorrections(auditControlId, pageable).toDto()

    override fun getProjectAuditCorrection(projectId: Long, auditControlId: Long, correctionId: Long) =
        getAuditCorrection.getCorrection(correctionId = correctionId).toDto()

    override fun deleteProjectAuditCorrection(projectId: Long, auditControlId: Long, correctionId: Long) =
        deleteAuditCorrection.deleteCorrection(correctionId)

    override fun closeProjectCorrection(projectId: Long, auditControlId: Long, correctionId: Long): AuditStatusDTO =
        closeCorrection.closeCorrection(correctionId).toDto()

    override fun updateCorrectionIdentification(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        correctionIdentificationUpdate: ProjectCorrectionIdentificationUpdateDTO
    ): ProjectAuditControlCorrectionDTO =
        updateCorrection.updateCorrection(correctionId, correctionIdentificationUpdate.toModel()).toDto()

    override fun getPreviousClosedCorrections(projectId: Long, auditControlId: Long, correctionId: Long) =
        listPreviouslyClosedCorrection.getClosedCorrectionsBefore(correctionId).toSimpleDto()

    override fun listCorrectionAvailableCostItems(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        pageable: Pageable
    ): Page<CorrectionCostItemDTO>  = getCorrectionCostItems.getCostItems(correctionId, pageable).toCorrectionCostItemDTO()

    override fun listCorrectionAvailableProcurements(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): List<IdNamePairDTO> = getAvailableProcurements.getAvailableProcurements(correctionId).toDTO()
}
