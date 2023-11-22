package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim

import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim

fun  List<ProjectReportSpfContributionClaim>.fillInTotalReportedSoFar() = this.forEach {
    it.totalReportedSoFar = it.calculateTotalReportedSoFar()
}
fun ProjectReportSpfContributionClaim.calculateTotalReportedSoFar() =
    this.currentlyReported.plus(previouslyReported)
