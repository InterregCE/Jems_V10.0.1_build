package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment_advance")
class AdvancePaymentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val projectId: Long,

    // the project version will be needed to fetch export data
    @field:NotNull
    val projectVersion: String,
    @field:NotNull
    val projectCustomIdentifier: String,
    val projectAcronym: String?,

    @field:NotNull
    val partnerId: Long,
    val partnerAbbreviation: String?,
    @Enumerated(EnumType.STRING)
    @field:NotNull
    val partnerRole: ProjectPartnerRole,
    val partnerSortNumber: Int?,

    // one of three sources of contributions is possible
    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    var programmeFund: ProgrammeFundEntity? = null,
    var partnerContributionId: Long? = null,
    var partnerContributionName: String? = null,
    var partnerContributionSpfId: Long? = null,
    var partnerContributionSpfName: String? = null,

    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate?,
    val comment: String?,

    var isPaymentAuthorizedInfo: Boolean?,
    @ManyToOne(optional = true)
    @JoinColumn(name = "payment_authorized_info_account_id")
    var paymentAuthorizedInfoUser: UserEntity?,
    var paymentAuthorizedDate: LocalDate?,

    var isPaymentConfirmed: Boolean?,
    @ManyToOne(optional = true)
    @JoinColumn(name = "payment_confirmed_account_id")
    var paymentConfirmedUser: UserEntity?,
    var paymentConfirmedDate: LocalDate?

)
