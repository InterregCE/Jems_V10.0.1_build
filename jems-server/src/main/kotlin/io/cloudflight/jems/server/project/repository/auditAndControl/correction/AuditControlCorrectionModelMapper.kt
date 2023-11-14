package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import org.springframework.data.domain.Page

fun AuditControlCorrectionEntity.toModel() = AuditControlCorrectionDetail(
    id = id,
    orderNr = orderNr,
    status = status,
    type = correctionType,
    auditControlId = auditControl.id,
    auditControlNr = auditControl.number,
    followUpOfCorrectionId = followUpOfCorrection?.id,
    correctionFollowUpType = followUpOfCorrectionType,
    repaymentFrom = repaymentDate,
    lateRepaymentTo = lateRepayment,
    partnerId = partnerReport?.partnerId,
    partnerReportId = partnerReport?.id,
    programmeFundId = programmeFund?.id,
)

fun AuditControlCorrectionEntity.toSimpleModel() = AuditControlCorrection(
    id = id,
    orderNr = orderNr,
    status = status,
    type = correctionType,
    auditControlId = auditControl.id,
    auditControlNr = auditControl.number,
)

fun Page<AuditControlCorrection>.toLineModel() =
    map { it.toLineModel() }
fun AuditControlCorrection.toLineModel() = AuditControlCorrectionLine(
    id = id,
    orderNr = orderNr,
    status = status,
    type = type,
    auditControlId = auditControlId,
    auditControlNr = auditControlNr,
    canBeDeleted = !status.isClosed(),
)


fun AuditControlCorrectionCreate.toEntity(auditControlEntity: AuditControlEntity) = AuditControlCorrectionEntity(
    auditControl = auditControlEntity,
    orderNr = orderNr,
    status = status,
    correctionType = type,
    followUpOfCorrection = null,
    followUpOfCorrectionType = followUpOfCorrectionType,
    repaymentDate = null,
    lateRepayment = null,
    partnerReport = null,
    programmeFund = null,
)
