package io.cloudflight.jems.server.project.repository.report.partner.control.expenditure

import io.cloudflight.jems.server.project.entity.report.control.expenditure.PartnerReportParkedExpenditureEntity
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartnerReportParkedExpenditureRepository : JpaRepository<PartnerReportParkedExpenditureEntity, Long> {

    fun findAllByParkedFromPartnerReportPartnerIdAndParkedFromPartnerReportStatus(
        partnerId: Long,
        status: ReportStatus,
    ): Iterable<PartnerReportParkedExpenditureEntity>

    fun findByParkedFromPartnerReportPartnerIdAndParkedFromPartnerReportStatusAndParkedFromExpenditureId(
        partnerId: Long,
        status: ReportStatus,
        id: Long,
    ): PartnerReportParkedExpenditureEntity

}
