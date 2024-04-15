package io.cloudflight.jems.server.plugin.services.auditAndControl


import io.cloudflight.jems.plugin.contract.models.common.paging.Page
import io.cloudflight.jems.plugin.contract.models.common.paging.Pageable
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlCorrectionBulkObjectData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlCorrectionDetailData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlCorrectionLineData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.ProjectCorrectionFinancialDescriptionData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.ProjectCorrectionProgrammeMeasureData
import io.cloudflight.jems.plugin.contract.services.auditAndControl.AuditAndControlDataProvider
import io.cloudflight.jems.server.plugin.services.toJpaPage
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection.AuditControlCorrectionPagingService
import io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.AuditControlCorrectionMeasurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream
import io.cloudflight.jems.plugin.contract.models.common.paging.Page as PluginPage


@Service
class AuditAndControlDataProviderImpl(
    private val auditControlPersistence: AuditControlPersistenceProvider,
    private val auditControlCorrectionPagingService: AuditControlCorrectionPagingService,
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val programmeMeasurePersistence: AuditControlCorrectionMeasurePersistence,
    private val financialDescriptionPersistence: AuditControlCorrectionFinancePersistence,
    private val correctionRepository: AuditControlCorrectionRepository,
): AuditAndControlDataProvider {

    @Transactional(readOnly = true)
    override fun fetchAllCorrectionsForExport(fundId: Long): Stream<AuditControlCorrectionBulkObjectData> =
        correctionRepository.findAllCorrectionsForExport(fundId).map { it.toDataModel() }

    @Transactional(readOnly = true)
    override fun listForProject(projectId: Long, pageable: Pageable): PluginPage<AuditControlData> {
        return auditControlPersistence.findAllProjectAudits(projectId, pageable.toJpaPage()).toModelData()
    }

    @Transactional(readOnly = true)
    override fun getAuditControlDetails(auditControlId: Long): AuditControlData =
        auditControlPersistence.getById(auditControlId = auditControlId).toDataModel()


    @Transactional(readOnly = true)
    override fun listCorrections(auditControlId: Long, pageable: Pageable): Page<AuditControlCorrectionLineData> =
        auditControlCorrectionPagingService.listCorrections(auditControlId, pageable.toJpaPage()).toLineModelData()


    @Transactional(readOnly = true)
    override fun getCorrection(correctionId: Long): AuditControlCorrectionDetailData =
        correctionPersistence.getByCorrectionId(correctionId).toDataModel()

    @Transactional(readOnly = true)
    override fun getCorrectionFinancialDescription(correctionId: Long): ProjectCorrectionFinancialDescriptionData =
        financialDescriptionPersistence.getCorrectionFinancialDescription(correctionId).toDataModel()

    @Transactional(readOnly = true)
    override fun getCorrectionProgrammeMeasure(correctionId: Long): ProjectCorrectionProgrammeMeasureData =
        programmeMeasurePersistence.getProgrammeMeasure(correctionId).toDataModel()


}
