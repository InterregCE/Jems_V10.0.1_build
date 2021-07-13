package io.cloudflight.jems.server.call.service.model

enum class ApplicationFormFieldSetting(val id: String, val validVisibilityStatusSet: Set<FieldVisibilityStatus>) {

    PROJECT_ID("application.config.project.id", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PROJECT_ACRONYM("application.config.project.acronym", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PROJECT_TITLE(
        "application.config.project.title",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_DURATION("application.config.project.duration", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PROJECT_PRIORITY("application.config.project.priority", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PROJECT_OBJECTIVE(
        "application.config.project.objective",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)
    ),

    PROJECT_RESULTS_PROGRAMME_RESULT_INDICATOR_AMD_MEASUREMENT_UNIT(
        "application.config.project.result.indicator.and.measurement.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_RESULTS_TARGET_VALUE(
        "application.config.project.result.target.value",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_RESULTS_DELIVERY_PERIOD(
        "application.config.project.result.delivery.period",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_RESULTS_DESCRIPTION(
        "application.config.project.result.description",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),

    PARTNER_ROLE("application.config.project.partner.role", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PARTNER_ABBREVIATED_ORGANISATION_NAME("application.config.project.partner.name", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PARTNER_ORIGINAL_NAME_OF_ORGANISATION("application.config.project.organization.original.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_ENGLISH_NAME_OF_ORGANISATION("application.config.project.organization.english.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_DEPARTMENT_UNIT_DIVISION("application.config.project.organization.department",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_TYPE("application.config.project.partner.type",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_LEGAL_STATUS("application.config.project.partner.legal.status", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PARTNER_VAT_IDENTIFIER("application.config.project.partner.vat",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_VAT_RECOVERY("application.config.project.partner.recoverVat",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_MAIN_ADDRESS_COUNTRY_AND_NUTS("application.config.project.partner.main-address.country.and.nuts",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_MAIN_ADDRESS_STREET("application.config.project.partner.main-address.street",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_MAIN_ADDRESS_HOUSE_NUMBER("application.config.project.partner.main-address.housenumber",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_MAIN_ADDRESS_POSTAL_CODE("application.config.project.partner.main-address.postalcode",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_MAIN_ADDRESS_CITY("application.config.project.partner.main-address.city",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_MAIN_ADDRESS_HOMEPAGE("application.config.project.partner.main-address.homepage",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_SECONDARY_ADDRESS_COUNTRY_AND_NUTS("application.config.project.partner.secondary-address.country.and.nuts",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_SECONDARY_ADDRESS_STREET("application.config.project.partner.secondary-address.street",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_SECONDARY_ADDRESS_HOUSE_NUMBER("application.config.project.partner.secondary-address.housenumber",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_SECONDARY_ADDRESS_POSTAL_CODE("application.config.project.partner.secondary-address.postalcode",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_SECONDARY_ADDRESS_CITY("application.config.project.partner.secondary-address.city",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_LEGAL_REPRESENTATIVE_TITLE("application.config.project.partner.representative.title",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_LEGAL_REPRESENTATIVE_FIRST_NAME("application.config.project.partner.representative.first.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_LEGAL_REPRESENTATIVE_LAST_NAME("application.config.project.partner.representative.last.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_CONTACT_PERSON_TITLE("application.config.project.partner.contact.title",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_CONTACT_PERSON_FIRST_NAME("application.config.project.partner.contact.first.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_CONTACT_PERSON_LAST_NAME("application.config.project.partner.contact.last.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_CONTACT_PERSON_EMAIL("application.config.project.partner.contact.email",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_CONTACT_PERSON_TELEPHONE("application.config.project.partner.contact.telephone",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_MOTIVATION_COMPETENCES("application.config.project.partner.motivation.organization.relevance",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_MOTIVATION_ROLE("application.config.project.partner.motivation.organization.role",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)),
    PARTNER_ROLE_EXPERIENCE("application.config.project.partner.motivation.organization.experience",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY));

    companion object {
        fun getValidVisibilityStatusSetById(id: String): Set<FieldVisibilityStatus> =
            values().find { it.id == id }?.validVisibilityStatusSet ?: emptySet()

        fun getDefaultApplicationFormFieldConfigurations() =
            values().map {

                    val defaultVisibilityStatus =
                        when {
                            it.validVisibilityStatusSet.contains(FieldVisibilityStatus.NONE) -> FieldVisibilityStatus.NONE
                            it.validVisibilityStatusSet.contains(FieldVisibilityStatus.STEP_TWO_ONLY) -> FieldVisibilityStatus.STEP_TWO_ONLY
                            else -> FieldVisibilityStatus.STEP_ONE_AND_TWO
                        }
                    ApplicationFormFieldConfiguration(it.id, defaultVisibilityStatus)
            }.toMutableSet()
    }

}
