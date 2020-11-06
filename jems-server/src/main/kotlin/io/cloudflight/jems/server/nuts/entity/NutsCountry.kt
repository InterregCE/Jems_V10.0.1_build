package io.cloudflight.jems.server.nuts.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "nuts_country")
data class NutsCountry (

    @Id
    override val id: String,

    @field:NotNull
    override val title: String

): NutsBaseEntity
