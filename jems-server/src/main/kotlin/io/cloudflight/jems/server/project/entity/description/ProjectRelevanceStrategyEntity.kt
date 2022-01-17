package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import java.util.Objects
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_description_c2_relevance_strategy")
class ProjectRelevanceStrategyEntity(

    @Id
    val id: UUID,

    @Column
    @field:NotNull
    val sortNumber: Int,

    @ManyToOne
    @JoinColumn(name = "project_relevance_id", insertable = false, updatable = false)
    val projectRelevance: ProjectRelevanceEntity? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val strategy: ProgrammeStrategy?,

    // specification
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectRelevanceStrategyTransl> = mutableSetOf()

)
