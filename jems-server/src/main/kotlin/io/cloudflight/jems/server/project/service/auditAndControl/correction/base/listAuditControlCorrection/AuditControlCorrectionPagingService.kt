package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection

import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuditControlCorrectionPagingService(
    private val auditControlPersistence: AuditControlPersistence,
    private val versionPersistence: ProjectVersionPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
) {

    @Transactional(readOnly = true)
    fun listCorrections(auditControlId: Long, pageable: Pageable): Page<AuditControlCorrectionLine> {
        val projectId = auditControlPersistence.getProjectIdForAuditControl(auditControlId = auditControlId)
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId)
        val partners = partnerPersistence.findTop50ByProjectId(projectId, version)
        val deactivatedPartnerIds = partners
            .filter { !it.active }.mapTo(HashSet()) { it.id }

        return auditControlCorrectionPersistence
            .getAllCorrectionsByAuditControlId(auditControlId, pageable).toModel()
            .fillInPartnersForFtlsCorrections(partners)
            .fillInDeletableFlag()
            .fillInDeactivated(deactivatedPartnerIds = deactivatedPartnerIds)
            .fillInTotal()
    }

    private fun Page<AuditControlCorrectionLine>.fillInDeletableFlag() =
        onEach { it.canBeDeleted = !it.status.isClosed() }

    private fun Page<AuditControlCorrectionLine>.fillInDeactivated(deactivatedPartnerIds: Set<Long>) =
        onEach { it.partnerDisabled = it.partnerId in deactivatedPartnerIds }

    private fun Page<AuditControlCorrectionLine>.fillInTotal() = onEach {
        it.total = it.fundAmount
            .plus(it.publicContribution)
            .plus(it.autoPublicContribution)
            .plus(it.privateContribution)
    }

    private fun Page<AuditControlCorrectionLine>.fillInPartnersForFtlsCorrections(partners: Iterable<ProjectPartnerDetail>) =
        onEach {
            if (it.partnerId != null && it.lumpSumOrderNr != null) {
                val partner = partners.find { partner -> it.partnerId == partner.id }
                it.partnerRole = partner?.role
                it.partnerNumber = partner?.sortNumber

            }
        }
}
