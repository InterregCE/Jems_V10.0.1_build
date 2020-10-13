package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "associated_organization_contact")
data class AssociatedOrganizationContact (

    @EmbeddedId
    val associatedOrganizationContactId: ProjectPartnerContactId,

    @Column
    val title: String?,

    @Column
    val firstName: String?,

    @Column
    val lastName: String?,

    @Column
    val email: String?,

    @Column
    val telephone: String?

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(" +
            "id=$associatedOrganizationContactId," +
            "title=$title," +
            "firstName=$firstName," +
            "lastName=$lastName," +
            "title=$title" +
            "email=$email" +
            "telephone=$telephone)"
    }
}
