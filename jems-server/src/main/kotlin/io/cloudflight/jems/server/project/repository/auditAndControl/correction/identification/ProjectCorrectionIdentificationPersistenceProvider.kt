package io.cloudflight.jems.server.project.repository.auditAndControl.correction.identification

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionIdentificationEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.ProjectCorrectionIdentificationPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentificationUpdate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class ProjectCorrectionIdentificationPersistenceProvider(
    private val correctionIdentificationRepository: CorrectionIdentificationRepository
) : ProjectCorrectionIdentificationPersistence {

    @Transactional(readOnly = true)
    override fun getCorrectionIdentification(
        correctionId: Long
    ): ProjectCorrectionIdentification =
        correctionIdentificationRepository.getByCorrectionId(correctionId).toModel()

    @Transactional
    override fun updateCorrectionIdentification(
        correctionId: Long,
        correctionIdentificationUpdate: ProjectCorrectionIdentificationUpdate
    ): ProjectCorrectionIdentification {
        val correctionIdentification = correctionIdentificationRepository.getByCorrectionId(correctionId = correctionId)

        correctionIdentification.updateWith(correctionIdentificationUpdate)

        return correctionIdentification.toModel()
    }

    private fun ProjectCorrectionIdentificationEntity.updateWith(newData: ProjectCorrectionIdentificationUpdate) {
        followUpOfCorrectionId = newData.followUpOfCorrectionId
        correctionFollowUpType = newData.correctionFollowUpType
        repaymentFrom = newData.repaymentFrom
        lateRepaymentTo = newData.lateRepaymentTo
        partnerId = newData.partnerId
        partnerReportId = newData.partnerReportId
        programmeFundId = newData.programmeFundId
    }


}
