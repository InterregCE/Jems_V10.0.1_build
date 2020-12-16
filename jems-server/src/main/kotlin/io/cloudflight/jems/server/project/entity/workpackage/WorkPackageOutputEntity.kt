package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.project.entity.ProjectPeriod
import java.util.UUID
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package_output")
data class WorkPackageOutputEntity(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @field:NotNull
    val workPackage: WorkPackageEntity,

    @ManyToOne
    val period: ProjectPeriod? = null,

    @Column
    val outputNumber: Int,

    @ManyToOne
    @JoinColumn(name = "indicator_output_id")
    val programmeOutputIndicator: IndicatorOutput? = null,

    @Column
    val title: String? = null,

    @Column
    val targetValue: String? = null,

    @Column
    val description: String? = null

) {
    override fun hashCode(): Int {
        return Objects.hash(workPackage.id, outputNumber)
    }

    override fun equals(other: Any?): Boolean = (other is WorkPackageOutputEntity)
            && workPackage.id == other.workPackage.id
            && period == other.period
            && outputNumber == other.outputNumber
            && title == other.title
            && targetValue == other.targetValue
            && description == other.description

    override fun toString(): String {
        return "${this.javaClass.simpleName}(workPackage.id=${workPackage.id}, outputNumber=$outputNumber)"
    }
}
