package io.cloudflight.jems.server.project.service.report.model.create

import java.math.BigDecimal

data class PreviouslyReportedCoFinancing(
    val fundsSorted: List<PreviouslyReportedFund>,

    val totalPartner: BigDecimal,
    val totalPublic: BigDecimal,
    val totalAutoPublic: BigDecimal,
    val totalPrivate: BigDecimal,
    val totalSum: BigDecimal,

    val previouslyReportedPartner: BigDecimal,
    val previouslyReportedPublic: BigDecimal,
    val previouslyReportedAutoPublic: BigDecimal,
    val previouslyReportedPrivate: BigDecimal,
    val previouslyReportedSum: BigDecimal,
)
