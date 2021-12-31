package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.server.project.entity.partner.PartnerAddressRow
import io.cloudflight.jems.server.project.entity.partner.PartnerContactRow
import io.cloudflight.jems.server.project.entity.partner.PartnerDetailRow
import io.cloudflight.jems.server.project.entity.partner.PartnerIdentityRow
import io.cloudflight.jems.server.project.entity.partner.PartnerMotivationRow
import io.cloudflight.jems.server.project.entity.partner.PartnerSimpleRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPerPeriodRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerContributionRow
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerTotalBudgetEntry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.Optional

@Repository
interface ProjectPartnerRepository : JpaRepository<ProjectPartnerEntity, Long> {

    fun existsByProjectIdAndId(projectId: Long, id: Long): Boolean

    fun findFirstByProjectIdAndId(projectId: Long, id: Long): Optional<ProjectPartnerEntity>

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectPartnerEntity>

    fun findTop30ByProjectId(projectId: Long): Iterable<ProjectPartnerEntity>

    fun findTop30ByProjectId(projectId: Long, sort: Sort): Iterable<ProjectPartnerEntity>

    fun findFirstByProjectIdAndRole(projectId: Long, role: ProjectPartnerRole): Optional<ProjectPartnerEntity>

    fun existsByProjectIdAndAbbreviation(projectId: Long, name: String): Boolean

    @Query(
        """
            SELECT
                partner.id AS id,
                period.number AS periodNumber,
                staffSum AS staffCostsPerPeriod,
                travelSum AS travelAndAccommodationCostsPerPeriod,
                equipmentSum AS equipmentCostsPerPeriod,
                externalSum AS externalExpertiseAndServicesCostsPerPeriod,
                infrastructureSum AS infrastructureAndWorksCostsPerPeriod,
                unitSum AS unitCostsPerPeriod
            FROM project_partner as partner
                left join project_period as period on partner.project_id = period.project_id
                left join (
                    SELECT staffCosts.partner_id as partnerId, staffCostsPeriod.period_number,
                           sum(staffCostsPeriod.amount) as staffSum
                    FROM project_partner_budget_staff_cost_period AS staffCostsPeriod
                        LEFT JOIN project_partner_budget_staff_cost AS staffCosts ON staffCosts.id = staffCostsPeriod.budget_id
                    group by partner_id, period_number) as staff_budget
                    ON staff_budget.period_number = period.number AND partner.id = staff_budget.partnerId
                left join (
                    SELECT unitCosts.partner_id as partnerId, unitCostsPeriod.period_number,
                           sum(unitCostsPeriod.amount) as unitSum
                    FROM project_partner_budget_unit_cost_period AS unitCostsPeriod
                        LEFT JOIN project_partner_budget_unit_cost AS unitCosts ON unitCosts.id = unitCostsPeriod.budget_id
                    group by partner_id, period_number) as unit_budget
                    ON unit_budget.period_number = period.number AND partner.id = unit_budget.partnerId
                left join (
                    SELECT equipmentCosts.partner_id as partnerId, equipmentCostsPeriod.period_number,
                           sum(equipmentCostsPeriod.amount) as equipmentSum
                    FROM project_partner_budget_equipment_period AS equipmentCostsPeriod
                        LEFT JOIN project_partner_budget_equipment AS equipmentCosts ON equipmentCosts.id = equipmentCostsPeriod.budget_id
                    group by partner_id, period_number) as equipment_budget
                    ON equipment_budget.period_number = period.number AND partner.id = equipment_budget.partnerId
                left join (
                    SELECT travelCosts.partner_id as partnerId, travelCostsPeriod.period_number,
                           sum(travelCostsPeriod.amount) as travelSum
                    FROM project_partner_budget_travel_period AS travelCostsPeriod
                        LEFT JOIN project_partner_budget_travel AS travelCosts ON travelCosts.id = travelCostsPeriod.budget_id
                    group by partner_id, period_number) as travel_budget
                    ON travel_budget.period_number = period.number AND partner.id = travel_budget.partnerId
                left join (
                    SELECT infrastructureCosts.partner_id as partnerId, infrastructureCostsPeriod.period_number,
                           sum(infrastructureCostsPeriod.amount) as infrastructureSum
                    FROM project_partner_budget_infrastructure_period AS infrastructureCostsPeriod
                        LEFT JOIN project_partner_budget_infrastructure AS infrastructureCosts ON infrastructureCosts.id = infrastructureCostsPeriod.budget_id
                    group by partner_id, period_number) as infrastructure_budget
                    ON infrastructure_budget.period_number = period.number AND partner.id = infrastructure_budget.partnerId
                left join (
                    SELECT externalCosts.partner_id as partnerId, externalCostsPeriod.period_number,
                           sum(externalCostsPeriod.amount) as externalSum
                    FROM project_partner_budget_external_period AS externalCostsPeriod
                        LEFT JOIN project_partner_budget_external AS externalCosts ON externalCosts.id = externalCostsPeriod.budget_id
                    group by partner_id, period_number) as external_budget
                    ON external_budget.period_number = period.number AND partner.id = external_budget.partnerId
            WHERE partner.id IN :ids
            GROUP BY partner.id, period.number
            """,
        nativeQuery = true
    )
    fun getAllBudgetsByIds(ids: Set<Long>): List<ProjectPartnerBudgetPerPeriodRow>

    @Query(
        """
            SELECT
                partner.id AS id,
                period.number AS periodNumber,
                staffSum AS staffCostsPerPeriod,
                travelSum AS travelAndAccommodationCostsPerPeriod,
                equipmentSum AS equipmentCostsPerPeriod,
                externalSum AS externalExpertiseAndServicesCostsPerPeriod,
                infrastructureSum AS infrastructureAndWorksCostsPerPeriod,
                unitSum AS unitCostsPerPeriod
            FROM project_partner FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS partner
                left join project_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS period on partner.project_id = period.project_id
                left join (
                    SELECT staffCosts.partner_id as partnerId, staffCostsPeriod.period_number,
                           sum(staffCostsPeriod.amount) as staffSum
                    FROM project_partner_budget_staff_cost_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS staffCostsPeriod
                        LEFT JOIN project_partner_budget_staff_cost FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS staffCosts ON staffCosts.id = staffCostsPeriod.budget_id
                    group by partner_id, period_number) as staff_budget
                    ON staff_budget.period_number = period.number AND partner.id = staff_budget.partnerId
                left join (
                    SELECT unitCosts.partner_id as partnerId, unitCostsPeriod.period_number,
                           sum(unitCostsPeriod.amount) as unitSum
                    FROM project_partner_budget_unit_cost_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS unitCostsPeriod
                        LEFT JOIN project_partner_budget_unit_cost FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS unitCosts ON unitCosts.id = unitCostsPeriod.budget_id
                    group by partner_id, period_number) as unit_budget
                    ON unit_budget.period_number = period.number AND partner.id = unit_budget.partnerId
                left join (
                    SELECT equipmentCosts.partner_id as partnerId, equipmentCostsPeriod.period_number,
                           sum(equipmentCostsPeriod.amount) as equipmentSum
                    FROM project_partner_budget_equipment_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS equipmentCostsPeriod
                        LEFT JOIN project_partner_budget_equipment FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS equipmentCosts ON equipmentCosts.id = equipmentCostsPeriod.budget_id
                    group by partner_id, period_number) as equipment_budget
                    ON equipment_budget.period_number = period.number AND partner.id = equipment_budget.partnerId
                left join (
                    SELECT travelCosts.partner_id as partnerId, travelCostsPeriod.period_number,
                           sum(travelCostsPeriod.amount) as travelSum
                    FROM project_partner_budget_travel_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS travelCostsPeriod
                        LEFT JOIN project_partner_budget_travel FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS travelCosts ON travelCosts.id = travelCostsPeriod.budget_id
                    group by partner_id, period_number) as travel_budget
                    ON travel_budget.period_number = period.number AND partner.id = travel_budget.partnerId
                left join (
                    SELECT infrastructureCosts.partner_id as partnerId, infrastructureCostsPeriod.period_number,
                           sum(infrastructureCostsPeriod.amount) as infrastructureSum
                    FROM project_partner_budget_infrastructure_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS infrastructureCostsPeriod
                        LEFT JOIN project_partner_budget_infrastructure FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS infrastructureCosts ON infrastructureCosts.id = infrastructureCostsPeriod.budget_id
                    group by partner_id, period_number) as infrastructure_budget
                    ON infrastructure_budget.period_number = period.number AND partner.id = infrastructure_budget.partnerId
                left join (
                    SELECT externalCosts.partner_id as partnerId, externalCostsPeriod.period_number,
                           sum(externalCostsPeriod.amount) as externalSum
                    FROM project_partner_budget_external_period FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS externalCostsPeriod
                        LEFT JOIN project_partner_budget_external FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS externalCosts ON externalCosts.id = externalCostsPeriod.budget_id
                    group by partner_id, period_number) as external_budget
                    ON external_budget.period_number = period.number AND partner.id = external_budget.partnerId
            WHERE partner.id IN :ids
            GROUP BY partner.id, period.number
            """,
        nativeQuery = true
    )
    fun getAllBudgetsByPartnerIdsAsOfTimestamp(ids: Set<Long>, timestamp: Timestamp): List<ProjectPartnerBudgetPerPeriodRow>

    fun countByProjectId(projectId: Long): Long

    @Query("SELECT e.project.id FROM project_partner e WHERE e.id = :partnerId")
    fun getProjectIdForPartner(partnerId: Long): Long?

    @Query(
        """
            SELECT
             entity.project_id AS projectId
             FROM #{#entityName} FOR SYSTEM_TIME ALL AS entity
             WHERE entity.id = :id
             ORDER BY entity.ROW_START DESC
             LIMIT 1
             """,
        nativeQuery = true)
    fun getProjectIdByPartnerIdInFullHistory(id: Long): Long?

    @Query(
        """
            SELECT
             contribution.*
             FROM #{#entityName}_contribution FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS contribution
             WHERE contribution.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerContributionByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerContributionRow>

    @Query(
        """
            SELECT
             entity.*,
             entity.project_id AS projectId,
             entity.legal_status_id AS legalStatusId,
             entity.sort_number AS sortNumber,
             entity.name_in_original_language AS nameInOriginalLanguage,
             entity.name_in_english AS nameInEnglish,
             entity.partner_type AS partnerType,
             entity.vat_recovery AS vatRecovery,
             translation.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.source_entity_id
             WHERE entity.id = :id
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findPartnerIdentityByIdAsOfTimestamp(
        id: Long, timestamp: Timestamp
    ): List<PartnerIdentityRow>

    @Query(
        """
            SELECT
             addresses.*,
             addresses.nuts_region2 AS nutsRegion2,
             addresses.nuts_region3 AS nutsRegion3,
             addresses.house_number AS houseNumber,
             addresses.postal_code AS postalCode
             FROM #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses
             WHERE addresses.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerAddressesByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerAddressRow>

    @Query(
        """
            SELECT
             contacts.*,
             contacts.first_name AS firstName,
             contacts.last_name AS lastName
             FROM #{#entityName}_contact FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS contacts
             WHERE contacts.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerContactsByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerContactRow>

    @Query(
        """
            SELECT
             entity.id AS partnerId,
             motivationTransl.*,
             motivationTransl.organization_relevance AS organizationRelevance,
             motivationTransl.organization_role AS organizationRole,
             motivationTransl.organization_experience AS organizationExperience
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_motivation_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS motivationTransl ON entity.id = motivationTransl.partner_id
             WHERE entity.id = :partnerId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findPartnerMotivationByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerMotivationRow>

    @Query(
        value = """
             SELECT
             entity.id,
             entity.active,
             entity.abbreviation,
             entity.role,
             entity.sort_number as sortNumber,
             addresses.country
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses ON entity.id = addresses.partner_id
             WHERE entity.project_id = :projectId AND (addresses.type = 'Organization' || addresses.type IS NULL)
             """,
        countQuery = """
             SELECT
             count(*)
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdAsOfTimestamp(
        projectId: Long,
        pageable: Pageable,
        timestamp: Timestamp
    ): Page<PartnerSimpleRow>

    @Query(
        """
             SELECT
             entity.*,
             entity.sort_number as sortNumber,
             addresses.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses ON entity.id = addresses.partner_id
             WHERE entity.project_id = :projectId AND (addresses.type = 'Organization' || addresses.type IS NULL)
             ORDER BY entity.sort_number ASC
             LIMIT 30
             """,
        nativeQuery = true
    )
    fun findTop30ByProjectIdSortBySortNumberAsOfTimestamp(
        projectId: Long,
        timestamp: Timestamp
    ): List<PartnerSimpleRow>

    @Query(
        value = """
             SELECT
             entity.*,
             (SELECT
                addresses.country
                from project_partner_address
                FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses
                WHERE entity.id = addresses.partner_id AND addresses.type = 'Organization') as country
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.id = :partnerId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findOneByIdAsOfTimestamp(partnerId: Long, timestamp: Timestamp): PartnerSimpleRow


    @Query(
        """
             SELECT
             entity.*,
             entity.project_id as projectId,
             entity.sort_number as sortNumber,
             entity.name_in_original_language as nameInOriginalLanguage,
             entity.name_in_english as nameInEnglish,
             entity.partner_type as partnerType,
             entity.partner_sub_type as partnerSubType,
             entity.legal_status_id as legalStatusId,
             entity.vat_recovery as vatRecovery,
             entity.other_identifier_number as otherIdentifierNumber,
             translation.language,
             translation.department,
             translation.other_identifier_description as otherIdentifierDescription,

             address.type as addressType,
             address.country,
             address.nuts_region2 as nutsRegion2,
             address.nuts_region3 as nutsRegion3,
             address.street,
             address.house_number as houseNumber,
             address.postal_code as postalCode,
             address.city,
             address.homepage,

             contact.type as contactType,
             contact.title,
             contact.first_name as firstName,
             contact.last_name as lastName,
             contact.email as email,
             contact.telephone as telephone,

             motivationTranslation.language as motivationRowLanguage,
             motivationTranslation.organization_relevance as organizationRelevance,
             motivationTranslation.organization_role as organizationRole,
             motivationTranslation.organization_experience as organizationExperience
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.source_entity_id
             LEFT JOIN #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS address ON entity.id = address.partner_id
             LEFT JOIN #{#entityName}_contact FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS contact ON entity.id = contact.partner_id
             LEFT JOIN #{#entityName}_motivation_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS motivationTranslation ON entity.id = motivationTranslation.partner_id
             WHERE entity.project_id = :projectId
             ORDER BY entity.sort_number ASC
             LIMIT 30
             """,
        nativeQuery = true
    )
    fun findTop30ByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<PartnerDetailRow>


    @Query(
        """
                SELECT
                    entity.id AS partnerId,
                    partnerBudgetOptions.staff_costs_flat_rate AS staffCostsFlatRate,
                    partnerBudgetOptions.office_and_administration_on_staff_costs_flat_rate AS officeAndAdministrationOnStaffCostsFlatRate,
                    partnerBudgetOptions.office_and_administration_on_direct_costs_flat_rate AS officeAndAdministrationOnDirectCostsFlatRate,
                    partnerBudgetOptions.travel_and_accommodation_on_staff_costs_flat_rate AS travelAndAccommodationOnStaffCostsFlatRate,
                    partnerBudgetOptions.other_costs_on_staff_costs_flat_rate AS otherCostsOnStaffCostsFlatRate,
                    unitCost.row_sum AS unitCostTotal,
                    equipmentCost.row_sum AS equipmentCostTotal,
                    externalCost.row_sum AS externalCostTotal,
                    infrastructureCost.row_sum AS infrastructureCostTotal,
                    travelCost.row_sum AS travelCostTotal,
                    staffCost.row_sum AS staffCostTotal,
                    lumpSum.row_sum AS lumpSumsTotal
                FROM project_partner AS entity
                LEFT JOIN (
                    SELECT *
                    FROM project_partner_budget_options
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS partnerBudgetOptions ON entity.id = partnerBudgetOptions.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_unit_cost
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS unitCost ON entity.id = unitCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_equipment
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS equipmentCost ON entity.id = equipmentCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_external
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS externalCost ON entity.id = externalCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_infrastructure
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS infrastructureCost ON entity.id = infrastructureCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_travel
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS travelCost ON entity.id = travelCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_staff_cost
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS staffCost ON entity.id = staffCost.partner_id
                LEFT JOIN (
                    SELECT project_partner_id AS partner_id, SUM(amount) AS row_sum
                    FROM project_partner_lump_sum
                    WHERE project_partner_id IN :partnerIds GROUP BY project_partner_id
                ) AS lumpSum ON entity.id = lumpSum.partner_id
                WHERE entity.id IN :partnerIds
                GROUP BY entity.id
            """,
        nativeQuery = true
    )
    fun getAllPartnerTotalBudgetData(partnerIds: Set<Long>): List<ProjectPartnerTotalBudgetEntry>

    @Query(
        """
            SELECT
                    entity.id AS partnerId,
                    partnerBudgetOptions.staff_costs_flat_rate AS staffCostsFlatRate,
                    partnerBudgetOptions.office_and_administration_on_staff_costs_flat_rate AS officeAndAdministrationOnStaffCostsFlatRate,
                    partnerBudgetOptions.office_and_administration_on_direct_costs_flat_rate AS officeAndAdministrationOnDirectCostsFlatRate,
                    partnerBudgetOptions.travel_and_accommodation_on_staff_costs_flat_rate AS travelAndAccommodationOnStaffCostsFlatRate,
                    partnerBudgetOptions.other_costs_on_staff_costs_flat_rate AS otherCostsOnStaffCostsFlatRate,
                    unitCost.row_sum AS unitCostTotal,
                    equipmentCost.row_sum AS equipmentCostTotal,
                    externalCost.row_sum AS externalCostTotal,
                    infrastructureCost.row_sum AS infrastructureCostTotal,
                    travelCost.row_sum AS travelCostTotal,
                    staffCost.row_sum AS staffCostTotal,
                    lumpSum.row_sum AS lumpSumsTotal
                FROM project_partner FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
                LEFT JOIN (
                    SELECT *
                    FROM project_partner_budget_options FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS partnerBudgetOptions ON entity.id = partnerBudgetOptions.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_unit_cost FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS unitCost ON entity.id = unitCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_equipment FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS equipmentCost ON entity.id = equipmentCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_external FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS externalCost ON entity.id = externalCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_infrastructure FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS infrastructureCost ON entity.id = infrastructureCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_travel FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS travelCost ON entity.id = travelCost.partner_id
                LEFT JOIN (
                    SELECT partner_id, SUM(row_sum) AS row_sum
                    FROM project_partner_budget_staff_cost FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp
                    WHERE partner_id IN :partnerIds GROUP BY partner_id
                ) AS staffCost ON entity.id = staffCost.partner_id
                LEFT JOIN (
                    SELECT project_partner_id AS partner_id, SUM(amount) AS row_sum
                    FROM project_partner_lump_sum FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp
                    WHERE project_partner_id IN :partnerIds GROUP BY project_partner_id
                ) AS lumpSum ON entity.id = lumpSum.partner_id
                WHERE entity.id IN :partnerIds
                GROUP BY entity.id
            """,
        nativeQuery = true
    )
    fun getAllPartnerTotalBudgetDataAsOfTimestamp(partnerIds: Set<Long>, timestamp: Timestamp): List<ProjectPartnerTotalBudgetEntry>
}
