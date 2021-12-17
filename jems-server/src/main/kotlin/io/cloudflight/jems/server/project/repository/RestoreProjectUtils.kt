package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.repository.workpackage.TableRelation
import org.intellij.lang.annotations.Language
import org.springframework.stereotype.Component
import java.sql.Timestamp
import javax.persistence.EntityManager
import kotlin.streams.toList

@Component
class RestoreProjectUtils(private val entityManager: EntityManager) {

    private val projectTableName = "project"
    private val childProjectIdField = "project_id"
    private val projectIdParam = "projectId"

    fun generateAndExecuteProjectRestoreQueries(projectId: Long, restoreTimestamp: Timestamp) {

        val versionedTablesRelations = getVersionedTablesRelations()
        val queries = mutableListOf<String>()

        getVersionedTablesChangedAfter(restoreTimestamp).let { changedTables ->
            changedTables.map { tableName ->
                generateDeleteQueryForCurrentData(tableName, versionedTablesRelations)?.let { queries.add(it) }
                generateInsertQueryForVersionedData(tableName, restoreTimestamp, versionedTablesRelations)?.let {
                    queries.add(it)
                }
            }
        }

        disableForeignKeyConstraints()
        queries.filter { it.isNotBlank() }.forEach { query ->
            entityManager.createNativeQuery(query)
                .setParameter(projectIdParam, projectId)
                .resultStream.toList()
        }
        enableForeignKeyConstraints()

    }

    private fun getVersionedTablesChangedAfter(timestamp: Timestamp?): List<String> {
        @Language("SQL")
        val changedTablesQuery = """
                SELECT TABLE_NAME
                FROM information_schema.tables
                WHERE TABLE_TYPE = 'SYSTEM VERSIONED' AND UPDATE_TIME > :timestamp
            """
        return entityManager.createNativeQuery(changedTablesQuery)
            .setParameter("timestamp", timestamp)
            .resultStream.toList().map { it as String }
    }

    private fun getVersionedTablesRelations(): List<TableRelation> {
        @Language("SQL")
        val tableRelationsQuery = """
            SELECT fks.table_name            AS childTable,
                   fks.referenced_table_name AS parentTable,
                   group_concat(kcu.referenced_column_name
                                ORDER BY position_in_unique_constraint SEPARATOR ', ')
                                             AS parentColumn,
                   group_concat(kcu.column_name
                                ORDER BY position_in_unique_constraint SEPARATOR ', ')
                                             AS childColumn
            FROM information_schema.referential_constraints fks
                     JOIN information_schema.key_column_usage kcu
                          ON fks.constraint_schema = kcu.table_schema
                              AND fks.table_name = kcu.table_name
                              AND fks.constraint_name = kcu.constraint_name
            WHERE fks.constraint_schema = 'jemsdb'
            GROUP BY fks.constraint_schema,
                     fks.table_name,
                     fks.unique_constraint_schema,
                     fks.referenced_table_name,
                     fks.constraint_name
            ORDER BY fks.constraint_schema,
                     fks.table_name;
            """

        return entityManager.createNativeQuery(tableRelationsQuery, "tableRelationMapping")
            .resultStream.toList().map { it as TableRelation }
    }

    private fun generateDeleteQueryForCurrentData(tableName: String, tableRelations: List<TableRelation>): String? =
        if (tableName == projectTableName)
            "DELETE FROM $projectTableName WHERE id = :$projectIdParam"
        else
            findPathToProjectIdOrProjectTable(tableName, tableRelations).let { path ->
                if (path.isEmpty()) return null
                return if (path.size > 1)
                    "DELETE FROM ${path.first().childTable} WHERE (${path.first().childColumn}) IN ( " +
                        "${generateNestedQuery(path)} )"
                else
                    "DELETE FROM ${path.first().childTable} WHERE ${path.first().childColumn} = :$projectIdParam"
            }

    private fun generateInsertQueryForVersionedData(
        tableName: String, timestamp: Timestamp, tableRelations: List<TableRelation>
    ): String? =
        if (tableName == projectTableName)
            "INSERT INTO $projectTableName SELECT * FROM $projectTableName FOR SYSTEM_TIME AS OF '$timestamp' WHERE id = :$projectIdParam"
        else
            findPathToProjectIdOrProjectTable(tableName, tableRelations).let { path ->
                if (path.isEmpty()) return null
                return if (path.size > 1)
                    "INSERT INTO ${path.first().childTable} SELECT * FROM ${path.first().childTable} FOR SYSTEM_TIME AS OF '$timestamp' WHERE (${path.first().childColumn}) IN ( " +
                        "${generateNestedQuery(path, timestamp)} )"
                else
                    "INSERT INTO ${path.first().childTable} SELECT * FROM ${path.first().childTable} FOR SYSTEM_TIME AS OF '$timestamp' WHERE ${path.first().childColumn} = :$projectIdParam"
            }

    private fun generateNestedQuery(
        path: List<TableRelation>, timestamp: Timestamp? = null
    ): String {
        val versionClause = if (timestamp != null) "FOR SYSTEM_TIME AS OF '$timestamp'" else ""
        return when (path.size) {
            2 -> "SELECT ${path.first().parentColumn} FROM ${path.first().parentTable} $versionClause WHERE ${path[1].childColumn} = :$projectIdParam "
            else -> "SELECT ${path.first().parentColumn} FROM ${path.first().parentTable} $versionClause WHERE (${path[1].childColumn}) IN (" +
                "${generateNestedQuery(path.drop(1), timestamp)})"
        }
    }

    private fun disableForeignKeyConstraints() =
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate()

    private fun enableForeignKeyConstraints() =
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate()

    // this will go through table relations (using FK constraints) graph to find the path from tableName to the first table with project_id field or project table in a recursive way
    private fun findPathToProjectIdOrProjectTable(
        tableName: String, tableRelations: List<TableRelation>
    ): List<TableRelation> =
        tableRelations.filter { it.childTable == tableName }.map { tableRelation ->
            if (tableRelation.childColumn == childProjectIdField || tableRelation.parentTable == projectTableName)
                listOf(tableRelation)
            else
                findPathToProjectIdOrProjectTable(tableRelation.parentTable, tableRelations).let { pathRelations ->
                    if (pathRelations.isNotEmpty()) {
                        listOf(tableRelation, *pathRelations.toTypedArray())
                    } else pathRelations
                }
        }.firstOrNull { it.isNotEmpty() } ?: emptyList()
}
