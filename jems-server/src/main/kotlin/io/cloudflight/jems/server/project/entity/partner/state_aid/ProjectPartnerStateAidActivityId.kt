package io.cloudflight.jems.server.project.entity.partner.state_aid

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ProjectPartnerStateAidActivityId(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="project_partner_id", referencedColumnName="partner_id")
    @field:NotNull
    val projectPartnerStateAid: ProjectPartnerStateAidEntity,

    @ManyToOne(optional = false)
    @JoinColumn(name="activity_id", referencedColumnName="id")
    @field:NotNull
    val activity: WorkPackageActivityEntity

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectPartnerStateAidActivityId && projectPartnerStateAid.partnerId != 0L &&
        projectPartnerStateAid.partnerId == other.projectPartnerStateAid.partnerId &&
        activity.id == other.activity.id

    override fun hashCode(): Int = Objects.hash(activity.id)

}
