package io.cloudflight.jems.server.programme.entity.indicator

import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "programme_indicator_result")
class ResultIndicatorEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    @field:NotNull
    val identifier: String,

    val code: String? = null,

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "programme_priority_policy_id")
    val programmePriorityPolicyEntity: ProgrammeSpecificObjectiveEntity?,

    val baseline: BigDecimal? = null,

    val referenceYear: String? = null,

    val finalTarget: BigDecimal? = null,

    val comment: String? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ResultIndicatorTranslEntity> = mutableSetOf(),

    )
