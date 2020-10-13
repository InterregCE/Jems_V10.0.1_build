package io.cloudflight.jems.server.project.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Contact(

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

)
