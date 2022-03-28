package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementTranslEntity
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
        supplierName = it.supplierName,
        comment = it.translatedValues.extractField { translated -> translated.comment },
    )
}

fun List<ProjectPartnerReportProcurementUpdate>.toEntity(report: ProjectPartnerReportEntity) = map {
    ProjectPartnerReportProcurementEntity(
        id = it.id,
        reportEntity = report,
        contractId = it.contractId,
        contractAmount = it.contractAmount,
        supplierName = it.supplierName,
        translatedValues = mutableSetOf(),
    ).apply {
        translatedValues.addTranslation(this, it.contractType, it.comment)
    }
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
