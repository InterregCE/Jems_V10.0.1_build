package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class PaymentEcCumulativeId(

    @ManyToOne
    @JoinColumn(name = "payment_application_to_ec_id")
    @field:NotNull
    val paymentApplicationToEc: PaymentApplicationToEcEntity,

    @ManyToOne(optional = true)
    @JoinColumn(name = "priority_axis_id")
    val priorityAxis: ProgrammePriorityEntity?,

    ): Serializable {

    override fun equals(other: Any?): Boolean =
        this === other || other is PaymentEcCumulativeId
                && paymentApplicationToEc == other.paymentApplicationToEc
                && priorityAxis == other.priorityAxis


    override fun hashCode(): Int = Objects.hash(paymentApplicationToEc, priorityAxis)
}