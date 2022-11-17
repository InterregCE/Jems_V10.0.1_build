package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "controller_institution")
class ControllerInstitutionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    @field:NotNull
    var name: String,

    @Column
    var description: String? = null,

    @OneToMany
    @JoinTable(
        name = "controller_institution_nuts",
        joinColumns = [JoinColumn(name = "controller_institution_id")],
        inverseJoinColumns = [JoinColumn(name = "nuts_region_3_id")]
    )
    var institutionNuts: MutableSet<NutsRegion3> = mutableSetOf(),

    @Column
    @field:NotNull
    var createdAt: ZonedDateTime

)
