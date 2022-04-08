package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementTranslEntity
import io.cloudflight.jems.server.project.repository.report.toModel
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate

fun List<ProjectPartnerReportProcurementEntity>.toModel() = map {
    ProjectPartnerReportProcurement(
        id = it.id,
        reportId = it.reportEntity.id,
        reportNumber = it.reportEntity.number,
        contractId = it.contractId,
        contractType = it.translatedValues.extractField { translated -> translated.contractType },
        contractAmount = it.contractAmount,
        currencyCode = it.currencyCode,
        supplierName = it.supplierName,
        comment = it.translatedValues.extractField { translated -> translated.comment },
        attachment = it.attachment?.toModel(),
    )
}

fun ProjectPartnerReportProcurementUpdate.toEntity(report: ProjectPartnerReportEntity) =
    ProjectPartnerReportProcurementEntity(
        id = id,
        reportEntity = report,
        contractId = contractId,
        contractAmount = contractAmount,
        currencyCode = currencyCode,
        supplierName = supplierName,
        attachment = null,
        translatedValues = mutableSetOf(),
    ).apply {
        translatedValues.addTranslation(this, contractType, comment)
    }

fun MutableSet<ProjectPartnerReportProcurementTranslEntity>.addTranslation(
    sourceEntity: ProjectPartnerReportProcurementEntity,
    contractType: Set<InputTranslation>,
    comment: Set<InputTranslation>,
) =
    this.addTranslationEntities(
        { language ->
            ProjectPartnerReportProcurementTranslEntity(
                translationId = TranslationId(sourceEntity, language),
                comment = comment.extractTranslation(language),
                contractType = contractType.extractTranslation(language),
            )
        }, arrayOf(comment, contractType)
    )
