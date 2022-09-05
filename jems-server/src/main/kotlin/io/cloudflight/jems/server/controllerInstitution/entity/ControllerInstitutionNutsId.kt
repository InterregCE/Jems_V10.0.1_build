package io.cloudflight.jems.server.controllerInstitution.entity

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.validation.constraints.NotNull
import javax.persistence.Embeddable

@Embeddable
class ControllerInstitutionNutsId(
    @field:NotNull
    @Column(name = "controller_institution_id")
    var institutionId: Long,

    @field:NotNull
    @Column(name = "nuts_region_3_id")
    var nutsRegion3Id: String
) : Serializable {
    override fun equals(other: Any?): Boolean = this === other ||
        other is ControllerInstitutionNutsId &&
        institutionId == other.institutionId &&
        nutsRegion3Id == other.nutsRegion3Id

    override fun hashCode(): Int = Objects.hash(institutionId, nutsRegion3Id)
}
