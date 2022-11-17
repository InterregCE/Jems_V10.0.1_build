package io.cloudflight.jems.server.project.entity.contracting

import io.cloudflight.jems.server.project.entity.Contact
import javax.persistence.Embedded
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_contracting_management")
class ProjectContractingManagementEntity(

    @EmbeddedId
    val managementId: ContractingManagementId,

    @Embedded
    var contact: Contact
)
