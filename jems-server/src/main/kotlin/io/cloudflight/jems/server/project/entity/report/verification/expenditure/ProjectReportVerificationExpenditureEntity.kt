package io.cloudflight.jems.server.project.entity.report.verification.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_verification_expenditure")
class ProjectReportVerificationExpenditureEntity(

    @Id
    val expenditureId: Long = 0L,

    @OneToOne
    @MapsId
    @JoinColumn(name = "expenditure_id")
    @field:NotNull
    val expenditure: PartnerReportExpenditureCostEntity,

    @field:NotNull
    var partOfVerificationSample: Boolean,

    @field:NotNull
    var deductedByJs: BigDecimal,

    @field:NotNull
    var deductedByMa: BigDecimal,

    @field:NotNull
    var amountAfterVerification: BigDecimal,

    var typologyOfErrorId: Long?,

    @field:NotNull
    var parked: Boolean,

    var verificationComment: String?,

)
