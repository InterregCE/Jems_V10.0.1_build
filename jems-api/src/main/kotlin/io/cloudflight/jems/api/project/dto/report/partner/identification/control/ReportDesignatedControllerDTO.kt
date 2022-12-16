package io.cloudflight.jems.api.project.dto.report.partner.identification.control

data class ReportDesignatedControllerDTO(
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
