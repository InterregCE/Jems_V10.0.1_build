package io.cloudflight.jems.server.project.repository.workpackage.investment

import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentRow
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageSummaryRow
import java.sql.Timestamp
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageInvestmentRepository : PagingAndSortingRepository<WorkPackageInvestmentEntity, Long> {

    fun findAllByWorkPackageId(workPackageId: Long): List<WorkPackageInvestmentEntity>

    fun findAllByWorkPackageId(workPackageId: Long, sort: Sort): Iterable<WorkPackageInvestmentEntity>

    @Query("SELECT investment FROM project_work_package_investment investment where investment.workPackage.project.id = :projectId")
    fun findInvestmentsByProjectId(@Param("projectId") projectId: Long): List<WorkPackageInvestmentEntity>

    fun countAllByWorkPackageId(workPackageId: Long): Long

    @Query(
        value ="""
             SELECT
             entity.*,
             entity.investment_number as investmentNumber,
             entity.nuts_region2 as nutsRegion2,
             entity.nuts_region3 as nutsRegion3,
             entity.house_number as houseNumber,
             entity.postal_code as postalCode,
             workPackageInvestmentTransl.*,
             workPackageInvestmentTransl.justification_explanation as justificationExplanation,
             workPackageInvestmentTransl.justification_transactional_relevance as justificationTransactionalRelevance,
             workPackageInvestmentTransl.justification_benefits as justificationBenefits,
             workPackageInvestmentTransl.justification_pilot as justificationPilot,
             workPackageInvestmentTransl.ownership_site_location as ownershipSiteLocation,
             workPackageInvestmentTransl.ownership_retain as ownershipRetain,
             workPackageInvestmentTransl.ownership_maintenance as ownershipMaintenance
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageInvestmentTransl ON entity.id = workPackageInvestmentTransl.investment_id
             WHERE entity.id = :workPackageInvestmentId
             """,
        nativeQuery = true
    )
    fun findByIdAsOfTimestamp(workPackageInvestmentId: Long, timestamp: Timestamp): List<WorkPackageInvestmentRow>

    @Query(
        value ="""
             SELECT
             entity.*,
             entity.investment_number as investmentNumber,
             entity.nuts_region2 as nutsRegion2,
             entity.nuts_region3 as nutsRegion3,
             entity.house_number as houseNumber,
             entity.postal_code as postalCode,
             workPackageInvestmentTransl.*,
             workPackageInvestmentTransl.justification_explanation as justificationExplanation,
             workPackageInvestmentTransl.justification_transactional_relevance as justificationTransactionalRelevance,
             workPackageInvestmentTransl.justification_benefits as justificationBenefits,
             workPackageInvestmentTransl.justification_pilot as justificationPilot,
             workPackageInvestmentTransl.ownership_site_location as ownershipSiteLocation,
             workPackageInvestmentTransl.ownership_retain as ownershipRetain,
             workPackageInvestmentTransl.ownership_maintenance as ownershipMaintenance
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageInvestmentTransl ON entity.id = workPackageInvestmentTransl.investment_id
             WHERE entity.work_package_id = :workPackageId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findAllByWorkPackageIdAsOfTimestamp(workPackageId: Long, timestamp: Timestamp): List<WorkPackageInvestmentRow>

    @Query(
        value ="""
             SELECT
             entity.*,
             entity.investment_number as investmentNumber,
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.work_package_id = :workPackageId
             """,
        nativeQuery = true
    )
    fun findAllSummariesByWorkPackageIdAsOfTimestamp(workPackageId: Long, timestamp: Timestamp): List<WorkPackageSummaryRow>
}
