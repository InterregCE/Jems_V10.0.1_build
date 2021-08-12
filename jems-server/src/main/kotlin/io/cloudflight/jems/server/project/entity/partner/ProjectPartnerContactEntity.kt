package io.cloudflight.jems.server.project.entity.partner;

import io.cloudflight.jems.server.project.entity.Contact
import javax.persistence.Embedded
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_contact")
data class ProjectPartnerContactEntity(

    @EmbeddedId
    val contactId: ProjectPartnerContactId,

    @Embedded
    val contact: Contact?

)
