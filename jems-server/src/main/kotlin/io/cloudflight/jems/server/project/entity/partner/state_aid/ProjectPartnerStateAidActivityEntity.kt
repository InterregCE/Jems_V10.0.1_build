package io.cloudflight.jems.server.project.entity.partner.state_aid

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_state_aid_activity")
class ProjectPartnerStateAidActivityEntity(

    @EmbeddedId
    val id: ProjectPartnerStateAidActivityId

)
