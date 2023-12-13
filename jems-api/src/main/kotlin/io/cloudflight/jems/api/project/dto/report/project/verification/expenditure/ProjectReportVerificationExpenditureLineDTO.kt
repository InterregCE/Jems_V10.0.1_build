package io.cloudflight.jems.api.project.dto.report.project.verification.expenditure

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ExpenditureParkingMetadataDTO
import java.math.BigDecimal
import java.time.ZonedDateTime

data class ProjectReportVerificationExpenditureLineDTO(
    val expenditure: ProjectPartnerReportExpenditureItemDTO,
    val partOfVerificationSample: Boolean,
    val deductedByJs: BigDecimal,
    val deductedByMa: BigDecimal,
    val amountAfterVerification: BigDecimal,
    val typologyOfErrorId: Long?,
    val parked: Boolean,
    val parkingMetadata: ExpenditureParkingMetadataDTO?,
    val verificationComment: String?,
    val parkedOn: ZonedDateTime?
)
