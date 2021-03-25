package io.cloudflight.jems.server.programme.entity.fund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
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
import javax.validation.constraints.NotNull

@Entity(name = "programme_fund")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProgrammeFundEntity.fetchWithTranslations",
        attributeNodes = [NamedAttributeNode(value = "translatedValues")],
    )
)
class ProgrammeFundEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val selected: Boolean,

    @field:NotNull
    val type: ProgrammeFundType = ProgrammeFundType.OTHER,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "translationId.sourceEntity",
        fetch = FetchType.EAGER
    )
    val translatedValues: MutableSet<ProgrammeFundTranslationEntity> = mutableSetOf()

)
