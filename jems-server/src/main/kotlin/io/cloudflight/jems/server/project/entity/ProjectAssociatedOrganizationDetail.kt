package io.cloudflight.jems.server.project.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne

@Entity(name = "project_associated_organization_details")
class ProjectAssociatedOrganizationDetail (

    @Id
    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @OneToOne(optional = false)
    @MapsId
    val organization: ProjectAssociatedOrganization,

    @Column
    val country: String?,

    @Column
    val nutsRegion2: String?,

    @Column
    val nutsRegion3: String?,

    @Column
    val street: String?,

    @Column
    val houseNumber: String?,

    @Column
    val postalCode: String?,

    @Column
    val city: String?,

    @Column
    val homepage: String?
)
