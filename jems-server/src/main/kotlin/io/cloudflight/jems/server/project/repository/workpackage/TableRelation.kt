package io.cloudflight.jems.server.project.repository.workpackage

data class TableRelation(
    val childTable: String,
    val childColumn: String,
    val parentTable: String,
    val parentColumn: String
)
