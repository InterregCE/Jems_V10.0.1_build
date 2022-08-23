package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.*

@Repository
interface ProgrammeUnitCostRepository : JpaRepository<ProgrammeUnitCostEntity, Long> {

    fun findByIdAndProjectIdNull(id: Long): Optional<ProgrammeUnitCostEntity>

    fun findByIdAndProjectId(id: Long, projectId: Long): ProgrammeUnitCostEntity

    @Query("""
        SELECT
            uc.id,
            uc.project_id AS projectId,
            uc.is_one_cost_category AS oneCostCategory,
            uc.cost_per_unit AS costPerUnit,
            uc.cost_per_unit_foreign_currency AS costPerUnitForeignCurrency,
            uc.foreign_currency_code AS foreignCurrencyCode,
            translation.language,
            translation.name,
            translation.description,
            translation.type,
            translation.justification,
            category.category
        FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS uc
        LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON uc.id = translation.programme_unit_cost_id
        LEFT JOIN #{#entityName}_budget_category FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS category ON uc.id = category.programme_unit_cost_id
        WHERE uc.id = :id AND uc.project_id = :projectId
    """, nativeQuery = true)
    fun findByIdAndProjectIdAsOfTimestamp(id: Long, projectId: Long, timestamp: Timestamp): Iterable<ProgrammeUnitCostRow>

    fun existsByIdAndProjectId(id: Long, projectId: Long): Boolean

    fun findTop100ByProjectIdNullOrderById(): Iterable<ProgrammeUnitCostEntity>

    fun findAllByIdInAndProjectIdNull(ids: Collection<Long>): MutableList<ProgrammeUnitCostEntity>

    fun findAllByProjectId(projectId: Long): MutableList<ProgrammeUnitCostEntity>

    fun countAllByProjectIdNull(): Long

    fun countAllByProjectId(projectId: Long): Long

    @Query("""
        SELECT uc
        FROM #{#entityName} uc
        WHERE (uc.id IN (
            SELECT pcuc.id.programmeUnitCost.id
            FROM project_call_unit_cost pcuc
            WHERE pcuc.id.projectCall.id = (
                SELECT p.call.id FROM project p WHERE p.id = :projectId
            )
        ) AND uc.projectId IS NULL) OR uc.projectId = :projectId
        ORDER BY uc.projectId, uc.id
    """)
    fun findAllForProjectIdOrCall(projectId: Long): Iterable<ProgrammeUnitCostEntity>

    @Query("""
        SELECT
            uc.id,
            uc.project_id AS projectId,
            uc.is_one_cost_category AS oneCostCategory,
            uc.cost_per_unit AS costPerUnit,
            uc.cost_per_unit_foreign_currency AS costPerUnitForeignCurrency,
            uc.foreign_currency_code AS foreignCurrencyCode,
            translation.language,
            translation.name,
            translation.description,
            translation.type,
            translation.justification,
            category.category
        FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS uc
        LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON uc.id = translation.programme_unit_cost_id
        LEFT JOIN #{#entityName}_budget_category FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS category ON uc.id = category.programme_unit_cost_id
        WHERE (uc.id IN (
            SELECT programme_unit_cost_id
            FROM project_call_unit_cost
            WHERE project_call_id = (
                SELECT project_call_id FROM project WHERE id = :projectId
            )
        ) AND uc.project_id IS NULL) OR uc.project_id = :projectId
        ORDER BY uc.project_id, uc.id;
    """, nativeQuery = true)
    fun findAllForProjectIdOrCallAsOfTimestamp(projectId: Long, timestamp: Timestamp): Iterable<ProgrammeUnitCostRow>

    @Query("""
        SELECT
            uc.id,
            uc.project_id AS projectId,
            uc.is_one_cost_category AS oneCostCategory,
            uc.cost_per_unit AS costPerUnit,
            uc.cost_per_unit_foreign_currency AS costPerUnitForeignCurrency,
            uc.foreign_currency_code AS foreignCurrencyCode,
            translation.language,
            translation.name,
            translation.description,
            translation.type,
            translation.justification,
            category.category
        FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS uc
        LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON uc.id = translation.programme_unit_cost_id
        LEFT JOIN #{#entityName}_budget_category FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS category ON uc.id = category.programme_unit_cost_id
        WHERE uc.project_id = :projectId
        ORDER BY uc.project_id, uc.id;
    """, nativeQuery = true)
    fun findAllByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): Iterable<ProgrammeUnitCostRow>

    fun deleteByIdAndProjectId(id: Long, projectId: Long)

}
