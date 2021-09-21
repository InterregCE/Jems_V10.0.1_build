package io.cloudflight.jems.server.project.entity.workpackage.activity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_activity_partner")
class WorkPackageActivityPartnerEntity(

    @EmbeddedId
    val id: WorkPackageActivityPartnerId

)
