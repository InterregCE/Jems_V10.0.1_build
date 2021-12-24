package io.cloudflight.jems.server.project.entity.associatedorganization

interface AssociatedOrganizationSimpleRow {
    val id: Long
    val active: Boolean
    val partnerAbbreviation: String
    val nameInOriginalLanguage: String?
    val nameInEnglish: String?
    val sortNumber: Int?
    val country: String?
}
