package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.auditAndControl.ProjectAuditAndControlApi
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ProjectAuditControlUpdateDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData.CorrectionAvailablePartnerDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.AvailableCorrectionsForPaymentDTO
import io.cloudflight.jems.server.project.controller.auditAndControl.correction.toDto
import io.cloudflight.jems.server.project.controller.auditAndControl.correction.toSimpleDto
import io.cloudflight.jems.server.project.service.auditAndControl.base.closeAuditControl.CloseAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.createAuditControl.CreateAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.getAuditControl.GetAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.listAuditControl.ListAuditControlIntetractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.reopenAuditControl.ReopenAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.updateAuditControl.UpdateAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForModification.GetAvailableCorrectionsForModificationInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForPayment.GetAvailableCorrectionsForPaymentInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl.GetPartnerAndPartnerReportDataInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class AuditControlController(
    private val createAuditControl: CreateAuditControlInteractor,
    private val updateProjectAuditControl: UpdateAuditControlInteractor,
    private val listProjectAudits: ListAuditControlIntetractor,
    private val getAuditDetails: GetAuditControlInteractor,
    private val closeProjectAuditControl: CloseAuditControlInteractor,
    private val partnerData: GetPartnerAndPartnerReportDataInteractor,
    private val reopenAuditControl: ReopenAuditControlInteractor,
    private val getCorrectionsForModification: GetAvailableCorrectionsForModificationInteractor,
    private val getCorrectionsForPayment: GetAvailableCorrectionsForPaymentInteractor,
): ProjectAuditAndControlApi {

    override fun createProjectAudit(projectId: Long, auditData: ProjectAuditControlUpdateDTO): AuditControlDTO =
       createAuditControl.createAudit(projectId, auditData.toModel()).toDto()

    override fun updateProjectAudit(
        projectId: Long,
        auditControlId: Long,
        auditData: ProjectAuditControlUpdateDTO
    ): AuditControlDTO {
       return updateProjectAuditControl.updateAudit(
            auditControlId = auditControlId,
            auditControlData = auditData.toModel()
        ).toDto()
    }

    override fun listAuditsForProject(projectId: Long, pageable: Pageable): Page<AuditControlDTO> =
        listProjectAudits.listForProject(projectId, pageable).map { it.toDto() }

    override fun getAuditDetail(projectId: Long, auditControlId: Long): AuditControlDTO =
        getAuditDetails.getDetails(auditControlId).toDto()

    override fun closeAuditControl(projectId: Long, auditControlId: Long): AuditStatusDTO =
        closeProjectAuditControl.closeAuditControl(auditControlId = auditControlId).toDto()

    override fun reopenAuditControl(projectId: Long, auditControlId: Long): AuditStatusDTO =
        reopenAuditControl.reopenAuditControl(auditControlId).toDto()

    override fun getPartnerAndPartnerReportData(projectId: Long): List<CorrectionAvailablePartnerDTO> =
        partnerData.getPartnerAndPartnerReportData(projectId).toDto()

    override fun getAvailableCorrectionsForModification(projectId: Long): List<AuditControlCorrectionDTO> =
        getCorrectionsForModification.getAvailableCorrections(projectId = projectId).map { it.toSimpleDto() }

    override fun getAvailableCorrectionsForPayment(projectId: Long, paymentId: Long): List<AvailableCorrectionsForPaymentDTO> =
        getCorrectionsForPayment.getAvailableCorrections(paymentId = paymentId).toDto()

}
