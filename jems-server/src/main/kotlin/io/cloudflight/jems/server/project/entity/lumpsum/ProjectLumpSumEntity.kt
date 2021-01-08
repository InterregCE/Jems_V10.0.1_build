package io.cloudflight.jems.server.project.entity.lumpsum

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_lump_sum")
data class ProjectLumpSumEntity(

    @Id
    val id: UUID,

    @field:NotNull
    val projectId: Long,

    @ManyToOne(optional = false)
    @field:NotNull
    val programmeLumpSum: ProgrammeLumpSumEntity,

    @field:NotNull
    val endPeriod: Int,

    @OneToMany(mappedBy = "id.projectLumpSumId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lumpSumContributions: Set<ProjectPartnerLumpSumEntity> = emptySet(),
)
