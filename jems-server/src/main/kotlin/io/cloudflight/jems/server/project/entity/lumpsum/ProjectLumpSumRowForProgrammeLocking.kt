package io.cloudflight.jems.server.project.entity.lumpsum

data class ProjectLumpSumRowForProgrammeLocking(
    val projectId: Long,
    val orderNr: Int,
    val endPeriod: Int?,
    val programmeLumpSumId: Long,
    val readyForPayment: Boolean,
    val comment: String?
)
