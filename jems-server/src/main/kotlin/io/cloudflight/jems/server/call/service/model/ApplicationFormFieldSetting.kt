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
    PROJECT_SUMMARY(
        "application.config.project.summary",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),


    PARTNER_ROLE("application.config.project.partner.role", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PARTNER_ABBREVIATED_ORGANISATION_NAME(
        "application.config.project.partner.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)
    ),
    PARTNER_ORIGINAL_NAME_OF_ORGANISATION(
        "application.config.project.organization.original.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_ENGLISH_NAME_OF_ORGANISATION(
        "application.config.project.organization.english.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_DEPARTMENT_UNIT_DIVISION(
        "application.config.project.organization.department",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_TYPE(
        "application.config.project.partner.type",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_LEGAL_STATUS(
        "application.config.project.partner.legal.status",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)
    ),
    PARTNER_VAT_IDENTIFIER(
        "application.config.project.partner.vat",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_VAT_RECOVERY(
        "application.config.project.partner.recoverVat",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_MAIN_ADDRESS_COUNTRY_AND_NUTS(
        "application.config.project.partner.main-address.country.and.nuts",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_MAIN_ADDRESS_STREET(
        "application.config.project.partner.main-address.street",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_MAIN_ADDRESS_HOUSE_NUMBER(
        "application.config.project.partner.main-address.housenumber",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_MAIN_ADDRESS_POSTAL_CODE(
        "application.config.project.partner.main-address.postalcode",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_MAIN_ADDRESS_CITY(
        "application.config.project.partner.main-address.city",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_MAIN_ADDRESS_HOMEPAGE(
        "application.config.project.partner.main-address.homepage",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_SECONDARY_ADDRESS_COUNTRY_AND_NUTS(
        "application.config.project.partner.secondary-address.country.and.nuts",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_SECONDARY_ADDRESS_STREET(
        "application.config.project.partner.secondary-address.street",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_SECONDARY_ADDRESS_HOUSE_NUMBER(
        "application.config.project.partner.secondary-address.housenumber",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_SECONDARY_ADDRESS_POSTAL_CODE(
        "application.config.project.partner.secondary-address.postalcode",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_SECONDARY_ADDRESS_CITY(
        "application.config.project.partner.secondary-address.city",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_LEGAL_REPRESENTATIVE_TITLE(
        "application.config.project.partner.representative.title",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_LEGAL_REPRESENTATIVE_FIRST_NAME(
        "application.config.project.partner.representative.first.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_LEGAL_REPRESENTATIVE_LAST_NAME(
        "application.config.project.partner.representative.last.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_CONTACT_PERSON_TITLE(
        "application.config.project.partner.contact.title",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_CONTACT_PERSON_FIRST_NAME(
        "application.config.project.partner.contact.first.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_CONTACT_PERSON_LAST_NAME(
        "application.config.project.partner.contact.last.name",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_CONTACT_PERSON_EMAIL(
        "application.config.project.partner.contact.email",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_CONTACT_PERSON_TELEPHONE(
        "application.config.project.partner.contact.telephone",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_MOTIVATION_COMPETENCES(
        "application.config.project.partner.motivation.organization.relevance",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_MOTIVATION_ROLE(
        "application.config.project.partner.motivation.organization.role",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_ROLE_EXPERIENCE(
        "application.config.project.partner.motivation.organization.experience",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),


    PROJECT_OVERALL_OBJECTIVE(
        "application.config.project.overall.objective",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_TERRITORIAL_CHALLENGES(
        "application.config.project.territorial.challenges",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_HOW_ARE_CHALLENGES_AND_OPPORTUNITIES_TACKLED(
        "application.config.project.how.are.challenges.and.opportunities.tackled",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_WHY_IS_COOPERATION_NEEDED(
        "application.config.project.why.is.cooperation.needed",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_TARGET_GROUP(
        "application.config.project.target.group",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),

    PROJECT_STRATEGY_CONTRIBUTION(
        "application.config.project.strategy.contribution",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_PROJECT_OR_INITIATIVE(
        "application.config.project.project.or.initiative",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_SYNERGIES(
        "application.config.project.synergies",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_HOW_BUILDS_PROJECT_ON_AVAILABLE_KNOWLEDGE(
        "application.config.project.how.builds.project.on.available.knowledge",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_PARTNERSHIP(
        "application.config.project.partnership",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
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
    PROJECT_COORDINATION(
        "application.config.project.coordination",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_QUALITY_MEASURES(
        "application.config.project.quality.measures",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_COMMUNICATION_APPROACH(
        "application.config.project.communication.approach",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_FINANCIAL_MANAGEMENT_AND_REPORTING(
        "application.config.project.financial.management.and.reporting",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_COOPERATION_CRITERIA(
        "application.config.project.cooperation.criteria",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_HORIZONTAL_PRINCIPLES(
        "application.config.project.horizontal.principles",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_OWNERSHIP(
        "application.config.project.ownership",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_DURABILITY(
        "application.config.project.durability",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_TRANSFERABILITY(
        "application.config.project.transferability",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    );

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
