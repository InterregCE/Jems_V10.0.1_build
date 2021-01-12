package io.cloudflight.jems.server.project.entity.result

import io.cloudflight.jems.server.programme.entity.indicator.IndicatorResult
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import java.util.UUID
import java.util.Objects
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_result")
data class ProjectResultEntity (
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @field:NotNull
    val project: ProjectEntity,

    @ManyToOne
    val period: ProjectPeriodEntity? = null,

    @Column
    val resultNumber: Int,

    @ManyToOne
    @JoinColumn(name = "indicator_result_id")
    val programmeResultIndicator: IndicatorResult? = null,

    @Column
    val targetValue: String? = null,

    // description
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.resultId")
    val translatedValues: Set<ProjectResultTransl> = emptySet()

){
    override fun hashCode(): Int = Objects.hash(project.id, resultNumber)

    override fun equals(other: Any?): Boolean = (other is ProjectResultEntity)
        && project.id == other.project.id
        && resultNumber == other.resultNumber
}
