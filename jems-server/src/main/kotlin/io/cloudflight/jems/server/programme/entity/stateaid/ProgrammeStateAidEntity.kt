package io.cloudflight.jems.server.programme.entity.stateaid

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import javax.validation.constraints.NotNull
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany

@Entity(name = "programme_state_aid")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProgrammeStateAidEntity.fetchWithTranslations",
        attributeNodes = [NamedAttributeNode(value = "translatedValues")],
    )
)
class ProgrammeStateAidEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val measure: ProgrammeStateAidMeasure = ProgrammeStateAidMeasure.OTHER_1,

    val schemeNumber: String? = null,

    val maxIntensity: BigDecimal?,

    val threshold: BigDecimal?,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "translationId.sourceEntity",
        fetch = FetchType.EAGER
    )
    val translatedValues: MutableSet<ProgrammeStateAidTranslationEntity> = mutableSetOf()
    )
