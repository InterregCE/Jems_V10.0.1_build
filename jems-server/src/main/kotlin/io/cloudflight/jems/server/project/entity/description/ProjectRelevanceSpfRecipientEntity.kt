package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
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

@Entity(name = "project_description_c2_relevance_spf_recipient")
class ProjectRelevanceSpfRecipientEntity(
    @Id
    val id: UUID,

    @Column
    @field: NotNull
    val sortNumber: Int,

    @ManyToOne
    @JoinColumn(name = "project_relevance_id", insertable = false, updatable = false)
    val projectRelevance: ProjectRelevanceEntity? = null,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val recipientGroup: ProjectTargetGroupDTO,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectRelevanceSpfRecipientTransl> = mutableSetOf()
)
