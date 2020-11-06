package io.cloudflight.jems.server.programme.entity.indicator

import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "programme_indicator_result")
data class IndicatorResult(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    @field:NotNull
    val identifier: String,

    val code: String?,

    @field:NotNull
    val name: String,

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "programme_priority_policy_id")
    val programmePriorityPolicy: ProgrammePriorityPolicy?,

    val measurementUnit: String?,

    val baseline: BigDecimal? = null,

    val referenceYear: String? = null,

    val finalTarget: BigDecimal? = null,

    val sourceOfData: String? = null,

    val comment: String? = null

)
