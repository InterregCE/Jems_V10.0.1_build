package io.cloudflight.ems.nuts.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "nuts_region_3")
data class NutsRegion3 (

    @Id
    override val id: String,

    @Column(nullable = false)
    override val title: String,

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "nuts_region_2_id")
    val region2: NutsRegion2

): NutsBaseEntity
