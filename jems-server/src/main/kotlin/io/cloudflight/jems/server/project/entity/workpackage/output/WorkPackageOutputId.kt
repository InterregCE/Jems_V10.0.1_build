package io.cloudflight.jems.server.project.entity.workpackage.output

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class WorkPackageOutputId(

    @Column(name = "work_package_id")
    @field:NotNull
    val workPackageId: Long,

    @Column(name = "output_number")
    @field:NotNull
    val outputNumber: Int

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is WorkPackageOutputId && workPackageId == other.workPackageId && outputNumber == other.outputNumber

    override fun hashCode(): Int = Objects.hash(workPackageId, outputNumber)
}
