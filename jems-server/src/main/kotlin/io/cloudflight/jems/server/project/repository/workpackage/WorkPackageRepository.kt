package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageDetailRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputRow
import java.sql.Timestamp
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkPackageRepository: PagingAndSortingRepository<WorkPackageEntity, Long> {

    @EntityGraph(value = "WorkPackageEntity.withTranslatedValues")
    fun findAllByProjectId(projectId: Long): List<WorkPackageEntity>

    fun findAllByProjectId(projectId: Long, sort: Sort): Iterable<WorkPackageEntity>

    fun countAllByProjectId(projectId: Long): Long

    @Query(
        value ="""
             SELECT
             entity.id AS id,
             entity.number as number,
             workPackageTransl.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageTransl ON entity.id = workPackageTransl.source_entity_id
             WHERE entity.project_id = :projectId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdAsOfTimestamp(
        projectId: Long,
        timestamp: Timestamp,
    ): List<WorkPackageRow>

    @Query(
        value = """
             SELECT
             entity.id AS id,
             entity.number as number,
             workPackageTransl.*,
             workPackageTransl.specific_objective as specificObjective,
             workPackageTransl.objective_and_audience as objectiveAndAudience
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageTransl ON entity.id = workPackageTransl.source_entity_id
             WHERE entity.id = :workPackageId
             """,
        nativeQuery = true
    )
    fun findByIdAsOfTimestamp(
        workPackageId: Long,
        timestamp: Timestamp
    ): List<WorkPackageRow>

    @Query(
        value ="""
            SELECT
                entity.work_package_id AS workPackageId,
                entity.output_number AS outputNumber,
                entity.indicator_output_id as programmeOutputIndicatorId,
                programmeOutputIndicatorIdentifier.identifier as programmeOutputIndicatorIdentifier,
                programmeOutputIndicatorIdentifierTransl.language as programmeOutputIndicatorLanguage,
                programmeOutputIndicatorIdentifierTransl.name as programmeOutputIndicatorName,
                programmeOutputIndicatorIdentifierTransl.measurement_unit as programmeOutputIndicatorMeasurementUnit,
                entity.target_value as targetValue,
                CONVERT(entity.period_number, INT) as periodNumber,
                workPackageOutputTransl.*
             FROM #{#entityName}_output FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_output_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageOutputTransl ON entity.work_package_id = workPackageOutputTransl.work_package_id AND entity.output_number = workPackageOutputTransl.output_number
             LEFT JOIN programme_indicator_output AS programmeOutputIndicatorIdentifier ON entity.indicator_output_id = programmeOutputIndicatorIdentifier.id
             LEFT JOIN programme_indicator_output_transl AS programmeOutputIndicatorIdentifierTransl ON programmeOutputIndicatorIdentifier.id = programmeOutputIndicatorIdentifierTransl.source_entity_id
             WHERE entity.work_package_id = :workPackageId
             ORDER BY entity.output_number
             """,
        nativeQuery = true
    )
    fun findOutputsByWorkPackageIdAsOfTimestamp(
        workPackageId: Long,
        timestamp: Timestamp,
    ): List<WorkPackageOutputRow>

    @Query(
        value ="""
             SELECT
             entity.id,
             entity.number,

             workPackageTransl.name,
             workPackageTransl.specific_objective AS specificObjective,
             workPackageTransl.objective_and_audience AS objectiveAndAudience,
             workPackageTransl.language,

             activity.id as activityId,
             activity.activity_number as activityNumber,
             activity.start_period as startPeriod,
             activity.end_period as endPeriod,
             activityTransl.title as activityTitle,
             activityTransl.description as activityDescription,
             activityTransl.language as activityLanguage,
             activityPartner.project_partner_id as partnerId,
             deliverable.id as deliverableId,
             deliverable.deliverable_number as deliverableNumber,
             deliverable.start_period as deliverableStartPeriod,
             deliverableTransl.description as deliverableDescription,
             deliverableTransl.title as deliverableTitle,
             deliverableTransl.language as deliverableLanguage,

             output.output_number as outputNumber,
             output.indicator_output_id as programmeOutputIndicatorId,
             output.period_number as outputPeriodNumber,
             output.target_value as targetValue,
             outputTransl.language as outputLanguage,
             outputTransl.title as outputTitle,
             outputTransl.description as outputDescription,
             programmeOutput.identifier as programmeOutputIndicatorIdentifier,
             programmeOutputTransl.language as programmeOutputIndicatorLanguage,
             programmeOutputTransl.name as programmeOutputIndicatorName,
             programmeOutputTransl.measurement_unit as programmeOutputIndicatorMeasurementUnit,

             investment.id as investmentId,
             investment.investment_number as investmentNumber,
             investment.country as investmentCountry,
             investment.nuts_region2 as investmentNutsRegion2,
             investment.nuts_region3 as investmentNutsRegion3,
             investment.street as investmentStreet,
             investment.house_number as investmentHouseNumber,
             investment.postal_code as investmentPostalCode,
             investment.city as investmentCity,
             investment.expected_delivery_period as investmentExpectedDeliveryPeriod,
             investmentTransl.title as investmentTitle,
             investmentTransl.justification_explanation as justificationExplanation,
             investmentTransl.justification_transactional_relevance as justificationTransactionalRelevance,
             investmentTransl.justification_benefits as justificationBenefits,
             investmentTransl.justification_pilot as justificationPilot,
             investmentTransl.risk as investmentRisk,
             investmentTransl.documentation as investmentDocumentation,
             investmentTransl.documentation_expected_impacts as investmentDocumentationExpectedImpacts,
             investmentTransl.ownership_site_location as ownershipSiteLocation,
             investmentTransl.ownership_retain as ownershipRetain,
             investmentTransl.ownership_maintenance as ownershipMaintenance,
             investmentTransl.language as investmentLanguage

             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS workPackageTransl ON entity.id = workPackageTransl.source_entity_id

             LEFT JOIN #{#entityName}_activity FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS activity ON entity.id = activity.work_package_id
             LEFT JOIN #{#entityName}_activity_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS activityTransl ON activity.id = activityTransl.source_entity_id
             LEFT JOIN #{#entityName}_activity_partner FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS activityPartner ON activity.id = activityPartner.activity_id
             LEFT JOIN #{#entityName}_activity_deliverable FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS deliverable ON activity.id = deliverable.activity_id
             LEFT JOIN #{#entityName}_activity_deliverable_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS deliverableTransl ON deliverable.id = deliverableTransl.source_entity_id

             LEFT JOIN #{#entityName}_output FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS output ON entity.id = output.work_package_id
             LEFT JOIN #{#entityName}_output_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS outputTransl ON output.work_package_id = outputTransl.work_package_id AND output.output_number = outputTransl.output_number
             LEFT JOIN programme_indicator_output AS programmeOutput ON output.indicator_output_id = programmeOutput.id
             LEFT JOIN programme_indicator_output_transl AS programmeOutputTransl ON programmeOutput.id = programmeOutputTransl.source_entity_id

             LEFT JOIN #{#entityName}_investment FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS investment ON entity.id = investment.work_package_id
             LEFT JOIN #{#entityName}_investment_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS investmentTransl ON investment.id = investmentTransl.investment_id

             WHERE entity.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun findWorkPackagesByProjectIdAsOfTimestamp(
        projectId: Long,
        timestamp: Timestamp,
    ): List<WorkPackageDetailRow>
}
