package io.cloudflight.ems.nuts.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "nuts_country")
data class NutsCountry (

    @Id
    override val id: String,

    @Column(nullable = false)
    override val title: String

): NutsBaseEntity
