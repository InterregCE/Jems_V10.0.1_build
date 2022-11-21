package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectControlReportExpenditurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectControlReportExpenditurePersistenceProvider(
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository
)
    : ProjectControlReportExpenditurePersistence {
    @Transactional(readOnly = true)
    override fun getPartnerControlReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
    ): List<ProjectPartnerControlReportExpenditureVerification> =
        reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            partnerId = partnerId,
            reportId = reportId,
        ).toExtendedModel()

    @Transactional
    override fun updatePartnerControlReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerControlReportExpenditureVerificationUpdate>,
    ): List<ProjectPartnerControlReportExpenditureVerification> {
        val existingIds = reportExpenditureRepository
            .findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(reportId, partnerId)
            .associateBy { it.id }

        return expenditureVerification.map { newData ->
            existingIds[newData.id].let { existing ->
                existing!!.apply { updateWith(newData) }
            }
        }.toExtendedModel()
    }

    private fun PartnerReportExpenditureCostEntity.updateWith(
        newData: ProjectPartnerControlReportExpenditureVerificationUpdate
    ) {
        certifiedAmount = newData.certifiedAmount
        deductedAmount = newData.deductedAmount
        partOfSample = newData.partOfSample
        typologyOfErrorId = newData.typologyOfErrorId
        verificationComment = newData.verificationComment
    }
}
