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
    val baseline: BigDecimal? = null,

    @Column
    val targetValue: BigDecimal? = null,

    )
