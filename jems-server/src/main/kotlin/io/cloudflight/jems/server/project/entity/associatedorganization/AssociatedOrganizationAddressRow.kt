package io.cloudflight.jems.server.project.entity.associatedorganization

interface AssociatedOrganizationAddressRow {
    val id: Long
    val country: String?
    val nutsRegion2: String?
    val nutsRegion3: String?
    val street: String?
    val houseNumber: String?
    val postalCode: String?
    val city: String?
    val homepage: String?
}