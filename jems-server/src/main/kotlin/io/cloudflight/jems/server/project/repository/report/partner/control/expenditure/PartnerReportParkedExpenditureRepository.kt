package io.cloudflight.jems.server.project.repository.report.partner.control.expenditure

import io.cloudflight.jems.server.project.entity.report.control.expenditure.PartnerReportParkedExpenditureEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PartnerReportParkedExpenditureRepository : JpaRepository<PartnerReportParkedExpenditureEntity, Long> {

    @Query("""
        SELECT parked
        FROM #{#entityName} parked
            LEFT JOIN report_project_partner_expenditure rppe ON parked.parkedFromExpenditureId = rppe.id
            LEFT JOIN report_project_partner rpp ON rppe.partnerReport.id = rpp.id
            LEFT JOIN report_project rp ON parked.parkedInProjectReport.id = rp.id
        WHERE rpp.partnerId = :partnerId
            AND rpp.id !=:reportId
            AND (parked.parkedOn < rpp.controlEnd OR parked.parkedOn < rp.verificationEndDate)
    """)
    fun findAllAvailableForPartnerReport(
        partnerId: Long,
        reportId: Long
    ): Iterable<PartnerReportParkedExpenditureEntity>

    @Query("""
        SELECT parked
        FROM #{#entityName} parked
            LEFT JOIN report_project_partner_expenditure rppe ON parked.parkedFromExpenditureId = rppe.id
            LEFT JOIN report_project_partner rpp ON rppe.partnerReport.id = rpp.id
            LEFT JOIN report_project rp ON parked.parkedInProjectReport.id = rp.id
        WHERE rpp.partnerId = :partnerId
            AND (parked.parkedOn < rpp.controlEnd OR parked.parkedOn < rp.verificationEndDate)
            AND parked.parkedFrom.id = :id
    """)
    fun findParkedExpenditure(partnerId: Long, id: Long): PartnerReportParkedExpenditureEntity

    fun findAllByParkedFromExpenditureIdIn(ids: Set<Long>): Iterable<PartnerReportParkedExpenditureEntity>

    @Query("SELECT pe.id FROM #{#entityName} pe WHERE pe.parkedFrom.partnerReport.id = :reportId")
    fun getAvailableParkedExpenditureIdsFromPartnerReport(reportId: Long): Set<Long>
}
