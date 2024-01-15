package io.cloudflight.jems.server.project.service.report.model.partner.base.create

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

    val previouslyReportedParkedPartner: BigDecimal,
    val previouslyReportedParkedPublic: BigDecimal,
    val previouslyReportedParkedAutoPublic: BigDecimal,
    val previouslyReportedParkedPrivate: BigDecimal,
    val previouslyReportedParkedSum: BigDecimal,

    val previouslyReportedSpfPartner: BigDecimal,
    val previouslyReportedSpfPublic: BigDecimal,
    val previouslyReportedSpfAutoPublic: BigDecimal,
    val previouslyReportedSpfPrivate: BigDecimal,
    val previouslyReportedSpfSum: BigDecimal,

    val previouslyValidatedPartner: BigDecimal,
    val previouslyValidatedPublic: BigDecimal,
    val previouslyValidatedAutoPublic: BigDecimal,
    val previouslyValidatedPrivate: BigDecimal,
    val previouslyValidatedSum: BigDecimal,
)
