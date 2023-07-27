package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

data class ExpenditureParkingMetadata(
    val reportOfOriginId: Long,
    val reportProjectOfOriginId: Long?,
    val reportOfOriginNumber: Int,
    val originalExpenditureNumber: Int
)
