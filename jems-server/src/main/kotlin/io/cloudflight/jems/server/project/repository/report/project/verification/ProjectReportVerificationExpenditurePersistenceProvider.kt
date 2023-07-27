package io.cloudflight.jems.server.project.repository.report.project.verification

import io.cloudflight.jems.server.project.entity.report.verification.expenditure.ProjectReportVerificationExpenditureEntity
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportVerificationExpenditurePersistenceProvider(
    private val expenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val expenditureVerificationRepository: ProjectReportVerificationExpenditureRepository,
    private val procurementRepository: ProjectPartnerReportProcurementRepository,
    private val projectReportRepository: ProjectReportRepository,
    private val projectReportCertificatePersistence: ProjectReportCertificatePersistence
) : ProjectReportVerificationExpenditurePersistence {

    @Transactional(readOnly = true)
    override fun getExpenditureVerificationRiskBasedData(
        projectId: Long,
        projectReportId: Long
    ): ProjectReportVerificationRiskBased =
        projectReportRepository.getByIdAndProjectId(projectReportId, projectId).toRiskBasedModel()


    @Transactional(readOnly = true)
    override fun getProjectReportExpenditureVerification(
        projectReportId: Long
    ): List<ProjectReportVerificationExpenditureLine> {
        val certificateIds = projectReportCertificatePersistence.listCertificatesOfProjectReport(projectReportId)
            .mapTo(HashSet()) { it.partnerId }

        val procurementsById = procurementRepository.findByReportEntityIdIn(certificateIds, Pageable.unpaged())
            .content.associateBy { it.id }

        return expenditureVerificationRepository
            .findAllByExpenditurePartnerReportProjectReportId(projectReportId)
            .toModels(procurementsById)
    }

    @Transactional(readOnly = true)
    override fun getParkedProjectReportExpenditureVerification(
        projectReportId: Long
    ): List<ProjectReportVerificationExpenditureLine> {
        val certificateIds = projectReportCertificatePersistence.listCertificatesOfProjectReport(projectReportId)
            .mapTo(HashSet()) { it.partnerId }

        val procurementsById = procurementRepository.findByReportEntityIdIn(certificateIds, Pageable.unpaged())
            .content.associateBy { it.id }

        return expenditureVerificationRepository
            .findAllByExpenditurePartnerReportProjectReportIdAndParkedIsTrue(projectReportId)
            .toModels(procurementsById)
    }

    @Transactional
    override fun initiateEmptyVerificationForProjectReport(
        projectReportId: Long
    ) {
        val expenditures = expenditureRepository.findAllByPartnerReportProjectReportId(projectReportId)
        expenditureVerificationRepository.saveAll(expenditures.map {
            it.toEmptyVerificationEntity()
        })
    }

    @Transactional
    override fun updateProjectReportExpenditureVerification(
        projectReportId: Long,
        expenditureVerification: List<ProjectReportVerificationExpenditureLineUpdate>
    ): List<ProjectReportVerificationExpenditureLine> {
        val existingEntities =
            expenditureVerificationRepository.findAllByExpenditurePartnerReportProjectReportId(projectReportId = projectReportId)
                .associateBy { it.expenditure.id }
        val certificateIds = projectReportCertificatePersistence.listCertificatesOfProjectReport(projectReportId)
            .mapTo(HashSet()) { it.partnerId }

        val procurementsById = procurementRepository.findByReportEntityIdIn(certificateIds, Pageable.unpaged())
            .content.associateBy { it.id }

        expenditureVerification.forEach {
            existingEntities.getValue(it.expenditureId).updateWith(it, existingEntities[it.expenditureId]!!)
        }

        return existingEntities.values.toExtendedModel(procurementsById)
    }

    @Transactional
    override fun updateProjectReportExpenditureVerificationRiskBased(
        projectId: Long,
        projectReportId: Long,
        riskBasedData: ProjectReportVerificationRiskBased
    ): ProjectReportVerificationRiskBased {
        val savedProjectReport = projectReportRepository.getByIdAndProjectId(projectReportId, projectId)

        savedProjectReport.riskBasedVerification = riskBasedData.riskBasedVerification
        savedProjectReport.riskBasedVerificationDescription = riskBasedData.riskBasedVerificationDescription

        return savedProjectReport.toRiskBasedModel()
    }

    private fun ProjectReportVerificationExpenditureEntity.updateWith(
        newData: ProjectReportVerificationExpenditureLineUpdate,
        savedData: ProjectReportVerificationExpenditureEntity
    ) {
        deductedByJs = newData.deductedByJs
        deductedByMa = newData.deductedByMa
        amountAfterVerification = computeAmountAfterVerification(newData, savedData)
        partOfVerificationSample = newData.partOfVerificationSample
        typologyOfErrorId = newData.typologyOfErrorId
        parked = newData.parked
        verificationComment = newData.verificationComment
    }

    private fun computeAmountAfterVerification(
        newData: ProjectReportVerificationExpenditureLineUpdate,
        savedData: ProjectReportVerificationExpenditureEntity
    ) = savedData.expenditure.certifiedAmount.minus(newData.deductedByMa).minus(newData.deductedByJs)
}
