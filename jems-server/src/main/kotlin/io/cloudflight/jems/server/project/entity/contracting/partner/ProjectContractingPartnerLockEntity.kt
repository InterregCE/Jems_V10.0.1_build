package io.cloudflight.jems.server.project.entity.contracting.partner

import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "lock_project_contracting_partner")
class ProjectContractingPartnerLockEntity (

    @Id
    var partnerId: Long = 0,

    val projectId: Long = 0
)


