package io.cloudflight.jems.server.project.entity.workpackage.activity

import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class WorkPackageActivityPartnerId(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="activity_id", referencedColumnName="id")
    @field:NotNull
    val activity: WorkPackageActivityEntity,

    @field:NotNull
    val projectPartnerId: Long

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is WorkPackageActivityPartnerId && activity.id != 0L && activity.id == other.activity.id && projectPartnerId == other.projectPartnerId

    override fun hashCode(): Int = Objects.hash(projectPartnerId)

}
