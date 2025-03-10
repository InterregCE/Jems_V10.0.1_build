package io.cloudflight.jems.server.project.repository.report.partner.file

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.entity.report.partner.procurement.file.ProjectPartnerReportProcurementFileEntity
import io.cloudflight.jems.server.project.entity.report.partner.procurement.file.ProjectPartnerReportProcurementGdprFileEntity
import io.cloudflight.jems.server.project.repository.report.partner.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.repository.report.partner.procurement.attachment.ProjectPartnerReportProcurementAttachmentRepository
import io.cloudflight.jems.server.project.repository.report.partner.procurement.gdprAttachment.ProjectPartnerReportProcurementGdprAttachmentRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.report.partner.file.ProjectPartnerReportFilePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportFilePersistenceProvider(
    private val workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository,
    private val contributionRepository: ProjectPartnerReportContributionRepository,
    private val expenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val reportProcurementAttachmentRepository: ProjectPartnerReportProcurementAttachmentRepository,
    private val reportProcurementGdprAttachmentRepository: ProjectPartnerReportProcurementGdprAttachmentRepository,
    private val procurementRepository: ProjectPartnerReportProcurementRepository,
    private val fileService: JemsProjectFileService,
) : ProjectPartnerReportFilePersistence {

    @Transactional
    override fun updatePartnerReportActivityAttachment(
        activityId: Long,
        file: JemsFileCreate,
    ): JemsFileMetadata {
        val activity = workPlanActivityRepository.findById(activityId).get()
        activity.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { activity.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun updatePartnerReportDeliverableAttachment(
        deliverableId: Long,
        file: JemsFileCreate,
    ): JemsFileMetadata {
        val deliverable = workPlanActivityDeliverableRepository.findById(deliverableId).get()
        deliverable.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { deliverable.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun updatePartnerReportOutputAttachment(
        outputId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata {
        val output = workPlanOutputRepository.findById(outputId).get()
        output.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { output.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun updatePartnerReportContributionAttachment(
        contributionId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata {
        val contribution = contributionRepository.findById(contributionId).get()
        contribution.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { contribution.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun updatePartnerReportExpenditureAttachment(
        expenditureId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata {
        val expenditure = expenditureRepository.findById(expenditureId).get()
        expenditure.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { expenditure.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun addPartnerReportProcurementAttachment(
        reportId: Long,
        procurementId: Long,
        file: JemsFileCreate,
    ): JemsFileMetadata {
        val procurement = procurementRepository.getReferenceById(procurementId)

        return persistFileAndUpdateLink(file = file) {
            reportProcurementAttachmentRepository.save(
                ProjectPartnerReportProcurementFileEntity(
                    procurement = procurement,
                    createdInReportId = reportId,
                    file = it,
                )
            )
        }.toSimple()
    }

    @Transactional
    override fun addPartnerReportProcurementGdprAttachment(
        reportId: Long,
        procurementId: Long,
        file: JemsFileCreate,
    ): JemsFileMetadata {
        val procurement = procurementRepository.getReferenceById(procurementId)

        return persistFileAndUpdateLink(file = file) {
            reportProcurementGdprAttachmentRepository.save(
                ProjectPartnerReportProcurementGdprFileEntity(
                    procurement = procurement,
                    createdInReportId = reportId,
                    file = it,
                )
            )
        }.toSimple()
    }

    @Transactional
    override fun addAttachmentToPartnerReport(file: JemsFileCreate) =
        persistFileAndUpdateLink(file = file) { /* we do not need to update any link */ }


    private fun persistFileAndUpdateLink(file: JemsFileCreate, additionalStep: (JemsFileMetadataEntity) -> Unit) =
        fileService.persistFileAndPerformAction(file = file, additionalStep = additionalStep)

    private fun JemsFileMetadataEntity?.deleteIfPresent() {
        if (this != null) {
            fileService.delete(this)
        }
    }

}
