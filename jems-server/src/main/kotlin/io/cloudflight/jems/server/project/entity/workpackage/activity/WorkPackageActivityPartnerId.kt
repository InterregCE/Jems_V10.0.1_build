package io.cloudflight.jems.server.project.entity.workpackage.activity

import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.validation.constraints.NotNull

@Embeddable
class WorkPackageActivityPartnerId(

    @Embedded
    val workPackageActivityId: WorkPackageActivityId,

    @field:NotNull
    val projectPartnerId: Long

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is WorkPackageActivityPartnerId && workPackageActivityId == other.workPackageActivityId && projectPartnerId == other.projectPartnerId

    override fun hashCode(): Int = Objects.hash(workPackageActivityId, projectPartnerId)

}
