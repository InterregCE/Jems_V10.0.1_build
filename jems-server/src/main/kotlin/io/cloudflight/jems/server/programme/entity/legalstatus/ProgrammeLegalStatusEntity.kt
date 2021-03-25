package io.cloudflight.jems.server.programme.entity.legalstatus

import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
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

@Entity(name = "programme_legal_status")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProgrammeLegalStatusEntity.fetchWithTranslations",
        attributeNodes = [NamedAttributeNode(value = "translatedValues")],
    )
)
class ProgrammeLegalStatusEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val type: ProgrammeLegalStatusType = ProgrammeLegalStatusType.OTHER,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "translationId.sourceEntity",
        fetch = FetchType.EAGER
    )
    val translatedValues: MutableSet<ProgrammeLegalStatusTranslationEntity> = mutableSetOf()

)
