package io.cloudflight.jems.server.project.entity.contracting

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "lock_project_contracting_section")
class ProjectContractingSectionLockEntity (

    @EmbeddedId
    val contractingSectionLockId: ProjectContractingSectionLockId,
)