package io.cloudflight.jems.server.project.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Contact(

    @Column
    val title: String? = null,

    @Column
    val firstName: String? = null,

    @Column
    val lastName: String? = null,

    @Column
    val email: String? = null,

    @Column
    val telephone: String? = null

)
