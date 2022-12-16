package io.cloudflight.jems.server.project.service.report.model.partner.identification.control

data class ReportDesignatedController(
    val controlInstitution: String?,
    val controlInstitutionId: Long,
    val controllingUserId: Long?,
    val jobTitle: String?,
    val divisionUnit: String?,
    val address: String?,
    val countryCode: String?,
    val country: String?,
    val telephone: String?,
    val controllerReviewerId: Long?,
)
