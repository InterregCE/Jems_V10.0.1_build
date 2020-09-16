package io.cloudflight.ems.project.entity;

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "partner_contact_person")
data class PartnerContactPerson (

    @EmbeddedId
    val partnerContactPersonId: PartnerContactPersonId,

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
            "id=$partnerContactPersonId," +
            "title=$title," +
            "firstName=$firstName," +
            "lastName=$lastName," +
            "title=$title" +
            "email=$email" +
            "telephone=$telephone)"
    }
}
