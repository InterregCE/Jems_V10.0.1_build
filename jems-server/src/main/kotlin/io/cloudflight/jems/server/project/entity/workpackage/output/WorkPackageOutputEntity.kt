package io.cloudflight.jems.server.project.entity.workpackage.output

import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany

@Entity(name = "project_work_package_output")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "WorkPackageOutputEntity.full",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class WorkPackageOutputEntity(

    @EmbeddedId
    val outputId: WorkPackageOutputId,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<WorkPackageOutputTransl> = mutableSetOf(),

    var periodNumber: Int? = null,

    @ManyToOne
    @JoinColumn(name = "indicator_output_id")
    var programmeOutputIndicatorEntity: OutputIndicatorEntity? = null,

    @Column
    val targetValue: BigDecimal? = null,

)
