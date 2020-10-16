package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.server.project.entity.Contact
import javax.persistence.Embedded
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_associated_organization_contact")
data class ProjectAssociatedOrganizationContact (

    @EmbeddedId
    val contactId: ProjectAssociatedOrganizationContactId,

    @Embedded
    val contact: Contact?

)
