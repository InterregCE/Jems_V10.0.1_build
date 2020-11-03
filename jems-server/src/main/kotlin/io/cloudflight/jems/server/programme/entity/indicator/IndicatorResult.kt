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

@Entity(name = "programme_indicator_result")
data class IndicatorResult(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val identifier: String,

    @Column
    val code: String?,

    @Column(nullable = false)
    val name: String,

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "programme_priority_policy_id")
    val programmePriorityPolicy: ProgrammePriorityPolicy?,

    @Column
    val measurementUnit: String?,

    @Column
    val baseline: BigDecimal? = null,

    @Column
    val referenceYear: String? = null,

    @Column
    val finalTarget: BigDecimal? = null,

    @Column
    val sourceOfData: String? = null,

    @Column
    val comment: String? = null

)
