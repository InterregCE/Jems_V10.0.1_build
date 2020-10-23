package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.server.project.entity.Address
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "project_associated_organization_address")
data class ProjectAssociatedOrganizationAddress (

    @Id
    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Embedded
    val address: Address?

) {
    fun nullIfBlank(): ProjectAssociatedOrganizationAddress? {
        if (address == null || address!!.isBlank())
            return null
        return this
    }
}
