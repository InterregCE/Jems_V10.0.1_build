package io.cloudflight.jems.server.project.service.report.model.project.base.create

import java.math.BigDecimal

data class PreviouslyProjectReportedCoFinancing(
    val fundsSorted: List<PreviouslyProjectReportedFund>,

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
