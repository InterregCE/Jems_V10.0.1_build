package io.cloudflight.jems.server.programme.entity.legalstatus

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.FetchType
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany

@Entity(name = "programme_legal_status")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProgrammeLegalStatusEntity.fetchWithTranslations",
        attributeNodes = [NamedAttributeNode(value = "translatedValues")],
    )
)
data class ProgrammeLegalStatusEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "translationId.legalStatus",
        fetch = FetchType.EAGER
    )
    val translatedValues: MutableSet<ProgrammeLegalStatusTranslationEntity> = mutableSetOf()

)
