package io.cloudflight.ems.nuts.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "nuts_region_1")
data class NutsRegion1 (

    @Id
    override val id: String,

    @Column(nullable = false)
    override val title: String,

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "nuts_country_id")
    val country: NutsCountry

): NutsBaseEntity
