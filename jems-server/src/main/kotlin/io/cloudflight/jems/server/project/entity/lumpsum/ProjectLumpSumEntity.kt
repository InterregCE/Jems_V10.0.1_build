package io.cloudflight.jems.server.project.entity.lumpsum

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import javax.persistence.CascadeType
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_lump_sum")
data class ProjectLumpSumEntity(

    @EmbeddedId
    val id: ProjectLumpSumId,

    @ManyToOne(optional = false)
    @field:NotNull
    val programmeLumpSum: ProgrammeLumpSumEntity,

    val endPeriod: Int? = null,

    @OneToMany(mappedBy = "id.projectLumpSumId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lumpSumContributions: Set<ProjectPartnerLumpSumEntity> = emptySet(),
)
