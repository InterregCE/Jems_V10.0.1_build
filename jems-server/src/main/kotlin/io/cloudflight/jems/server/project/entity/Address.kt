package io.cloudflight.jems.server.project.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Address(

    @Column
    val country: String? = null,

    @Column
    val nutsRegion2: String? = null,

    @Column
    val nutsRegion3: String? = null,

    @Column
    val street: String? = null,

    @Column
    val houseNumber: String? = null,

    @Column
    val postalCode: String? = null,

    @Column
    val city: String? = null,

    @Column
    val homepage: String? = null
)
