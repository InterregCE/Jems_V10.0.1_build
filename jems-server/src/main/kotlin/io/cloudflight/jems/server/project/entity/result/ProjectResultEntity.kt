package io.cloudflight.jems.server.project.entity.result

import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_result")
data class ProjectResultEntity (

    @EmbeddedId
    val resultId: ProjectResultId,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.resultId")
    val translatedValues: Set<ProjectResultTransl> = emptySet(),

    val periodNumber: Int? = null,

    @ManyToOne
    @JoinColumn(name = "indicator_result_id")
    val programmeResultIndicatorEntity: ResultIndicatorEntity? = null,

    @Column
    @field:NotNull
    val baseline: BigDecimal = BigDecimal.ZERO,

    @Column
    val targetValue: BigDecimal? = null,

    )
