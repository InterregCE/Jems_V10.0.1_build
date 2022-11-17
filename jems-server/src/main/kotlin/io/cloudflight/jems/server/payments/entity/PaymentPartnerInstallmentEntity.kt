package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.user.entity.UserEntity
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment_partner_installment")
class PaymentPartnerInstallmentEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_partner_id")
    @field:NotNull
    val paymentPartner: PaymentPartnerEntity,

    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate?,
    val comment: String?,

    val isSavePaymentInfo: Boolean?,
    @ManyToOne(optional = true)
    @JoinColumn(name = "save_payment_info_account_id")
    val savePaymentInfoUser: UserEntity?,
    val savePaymentDate: LocalDate?,

    val isPaymentConfirmed: Boolean?,
    @ManyToOne(optional = true)
    @JoinColumn(name = "payment_confirmed_account_id")
    val paymentConfirmedUser: UserEntity?,
    val paymentConfirmedDate: LocalDate?
)
