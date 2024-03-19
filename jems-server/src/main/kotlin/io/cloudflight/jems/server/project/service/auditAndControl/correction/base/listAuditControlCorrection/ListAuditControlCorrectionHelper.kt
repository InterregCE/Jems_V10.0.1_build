package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection

import io.cloudflight.jems.server.project.repository.auditAndControl.correction.tmpModel.AuditControlCorrectionLineTmp
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import org.springframework.data.domain.Page
import java.math.BigDecimal

fun Page<AuditControlCorrectionLineTmp>.toModel() = map {
    AuditControlCorrectionLine(
        id = it.correction.id,
        orderNr = it.correction.orderNr,
        status = it.correction.status,
        type = it.correction.type,
        auditControlId = it.correction.auditControlId,
        auditControlNr = it.correction.auditControlNr,
        canBeDeleted = false,
        partnerRole = it.partnerRole,
        partnerId = it.partnerId ?: it.lumpSumPartnerId,
        partnerNumber = it.partnerNumber,
        partnerDisabled = false,
        partnerReport = it.reportNr,
        lumpSumOrderNr = it.lumpSumOrderNr,
        followUpAuditNr = it.followUpAuditNr,
        followUpCorrectionNr = it.followUpCorrectionNr,
        fund = it.fund,
        fundAmount = it.fundAmount,
        publicContribution = it.publicContribution,
        autoPublicContribution = it.autoPublicContribution,
        privateContribution = it.privateContribution,
        total = BigDecimal.ZERO,
        impactProjectLevel = it.impactProjectLevel,
        scenario = it.scenario,
    )
}

fun mapPartnerRole(role: ProjectPartnerRole?, id: Long?, partners: Iterable<ProjectPartnerDetail>): ProjectPartnerRole? {
    if (role == null && id != null) {
        return partners.filter {it.id == id}.first().role
    }
    return role
}

fun mapPartnerNumber(number: Int?, id: Long?, partners: Iterable<ProjectPartnerDetail>): Int? {
    if (number == null && id != null) {
        return partners.filter {it.id == id}.first().sortNumber
    }
    return number
}
