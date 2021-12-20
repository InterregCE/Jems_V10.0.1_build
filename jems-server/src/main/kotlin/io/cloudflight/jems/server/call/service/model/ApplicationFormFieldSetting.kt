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
    PROJECT_A3(
        "application.config.project.section.a.3",
        setOf(FieldVisibilityStatus.STEP_TWO_ONLY),
    ),
    PROJECT_A4(
        "application.config.project.section.a.4",
        setOf(FieldVisibilityStatus.STEP_TWO_ONLY),
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
    PARTNER_SUB_TYPE(
        "application.config.project.partner.sub.type",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_NACE_GROUP_LEVEL(
        "application.config.project.partner.nace.group.level",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_OTHER_IDENTIFIER_NUMBER_AND_DESCRIPTION(
        "application.config.project.partner.other.identifier.number.and.description",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_PIC(
        "application.config.project.partner.pic",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
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
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
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
    PARTNER_BUDGET_AND_CO_FINANCING(
        "application.config.project.partner.budget.and.co.financing",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN(
        "application.config.project.partner.co.financing.add.new.contribution.origin",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_PERIODS(
        "application.config.project.partner.budget.periods",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_LUMP_SUMS_DESCRIPTION(
    "application.config.project.lump.sums.description",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_STAFF_COST_STAFF_FUNCTION(
        "application.config.project.partner.budget.staff.cost.staff.function",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_STAFF_COST_COMMENTS(
        "application.config.project.partner.budget.staff.cost.comment",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_STAFF_COST_UNIT_TYPE_AND_NUMBER_OF_UNITS(
        "application.config.project.partner.budget.staff.cost.unit.type.and.number.of.units",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_STAFF_COST_PRICE_PER_UNIT(
        "application.config.project.partner.budget.staff.cost.price.per.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_DESCRIPTION(
        "application.config.project.partner.budget.travel.and.accommodation.description",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_COMMENTS(
        "application.config.project.partner.budget.travel.and.accommodation.comments",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_UNIT_TYPE_AND_NUMBER_OF_UNITS(
        "application.config.project.partner.budget.travel.and.accommodation.unit.type.and.number.of.units",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_PRICE_PER_UNIT(
        "application.config.project.partner.budget.travel.and.accommodation.price.per.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EXTERNAL_EXPERTISE_DESCRIPTION(
        "application.config.project.partner.budget.external.expertise.description",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EXTERNAL_EXPERTISE_COMMENTS(
        "application.config.project.partner.budget.external.expertise.comments",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EXTERNAL_EXPERTISE_AWARD_PROCEDURE(
        "application.config.project.partner.budget.external.expertise.award.procedure",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EXTERNAL_EXPERTISE_INVESTMENT(
        "application.config.project.partner.budget.external.expertise.investment",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EXTERNAL_EXPERTISE_UNIT_TYPE_AND_NUMBER_OF_UNITS(
        "application.config.project.partner.budget.external.expertise.unit.type.and.number.of.units",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EXTERNAL_EXPERTISE_PRICE_PER_UNIT(
        "application.config.project.partner.budget.external.expertise.price.per.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EQUIPMENT_DESCRIPTION(
        "application.config.project.partner.budget.equipment.description",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EQUIPMENT_COMMENTS(
        "application.config.project.partner.budget.equipment.comments",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EQUIPMENT_AWARD_PROCEDURE(
        "application.config.project.partner.budget.equipment.award.procedure",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EQUIPMENT_INVESTMENT(
        "application.config.project.partner.budget.equipment.investment",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EQUIPMENT_UNIT_TYPE_AND_NUMBER_OF_UNITS(
        "application.config.project.partner.budget.equipment.unit.type.and.number.of.units",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_EQUIPMENT_PRICE_PER_UNIT(
        "application.config.project.partner.budget.equipment.price.per.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_DESCRIPTION(
        "application.config.project.partner.budget.infrastructure.and.works.description",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_COMMENTS(
        "application.config.project.partner.budget.infrastructure.and.works.comments",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_AWARD_PROCEDURE(
        "application.config.project.partner.budget.infrastructure.and.works.award.procedure",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_INVESTMENT(
        "application.config.project.partner.budget.infrastructure.and.works.investment",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_UNIT_TYPE_AND_NUMBER_OF_UNITS(
        "application.config.project.partner.budget.infrastructure.and.works.unit.type.and.number.of.units",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_PRICE_PER_UNIT(
        "application.config.project.partner.budget.infrastructure.and.works.price.per.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),

    PARTNER_BUDGET_UNIT_COSTS_PROGRAMME_UNIT_COSTS(
        "application.config.project.partner.budget.unit.costs.programme.unit.costs",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_UNIT_COSTS_DESCRIPTION(
        "application.config.project.partner.budget.unit.costs.description",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_UNIT_COSTS__UNIT_TYPE_AND_NUMBER_OF_UNITS(
        "application.config.project.partner.budget.unit.costs.unit.type.and.number.of.units",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_BUDGET_UNIT_COSTS_PRICE_PER_UNIT(
        "application.config.project.partner.budget.unit.costs.price.per.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_STATE_AID_CRITERIA_SELF_CHECK(
        "application.config.project.partner.state.aid.criteria.self.check",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_STATE_AID_RELEVANT_ACTIVITIES(
        "application.config.project.partner.state.aid.relevant.activities",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_STATE_AID_SCHEME(
        "application.config.project.partner.partner.state.aid.scheme",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PARTNER_ASSOCIATED_ORGANIZATIONS(
        "application.config.project.partner.associated.organizations",
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
    PROJECT_WORK_PACKAGE_TITLE(
        "application.config.project.work.package.title",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_SPECIFIC_OBJECTIVE(
        "application.config.project.specific.objective",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_COMMUNICATION_OBJECTIVES_AND_TARGET_AUDIENCE(
        "application.config.project.communication.objectives.and.target.audience",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_TITLE(
        "application.config.project.investment.title",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_PERIOD(
        "application.config.project.investment.expected.delivery.period",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_WHY_IS_INVESTMENT_NEEDED(
        "application.config.project.investment.why.is.investment.needed",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_CROSS_BORDER_TRANSNATIONAL_RELEVANCE_OF_INVESTMENT(
        "application.config.project.investment.cross.border.transnational.relevance.of.investment",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_WHO_IS_BENEFITING(
        "application.config.project.investment.who.is.benefiting",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_PILOT_CLARIFICATION(
        "application.config.project.investment.pilot.clarification",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_COUNTRY(
        "application.config.project.investment.country",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_STREET(
        "application.config.project.investment.street",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_HOUSE_NUMBER(
        "application.config.project.investment.house.number",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_POSTAL_CODE(
        "application.config.project.investment.postal.code",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_CITY(
        "application.config.project.investment.city",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_RISK(
        "application.config.project.investment.risk",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_DOCUMENTATION(
        "application.config.project.investment.documentation",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_DOCUMENTATION_EXPECTED_IMPACTS(
        "application.config.project.investment.documentation.expected.impacts",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_WHO_OWNS_THE_INVESTMENT_SITE(
        "application.config.project.investment.who.owns.the.investment.site",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_OWNERSHIP_AFTER_END_OF_PROJECT(
        "application.config.project.investment.ownership.after.end.of.project",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_INVESTMENT_MAINTENANCE(
        "application.config.project.investment.maintenance",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_ACTIVITIES_TITLE(
        "application.config.project.activities.title",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_ACTIVITIES_DESCRIPTION(
        "application.config.project.activities.description",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_ACTIVITIES_START_PERIOD(
        "application.config.project.activities.start.period",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_ACTIVITIES_END_PERIOD(
        "application.config.project.activities.end.period",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_ACTIVITIES_STATE_AID_PARTNERS_INVOLVED(
        "application.config.project.activities.state.aid.partners.involved",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_ACTIVITIES_DELIVERABLES(
        "application.config.project.activities.deliverables",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_OUTPUT_TITLE(
        "application.config.project.output.title",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_OUTPUT_PROGRAMME_OUTPUT_INDICATOR_AND_MEASUREMENT_UNIT(
        "application.config.project.output.programme.output.indicator.and.measurement.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_OUTPUT_TARGET_VALUE(
        "application.config.project.output.target.value",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_OUTPUT_DELIVERY_PERIOD(
        "application.config.project.output.delivery.period",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_OUTPUT_DESCRIPTION(
        "application.config.project.output.description",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_RESULTS_PROGRAMME_RESULT_INDICATOR_AMD_MEASUREMENT_UNIT(
        "application.config.project.result.indicator.and.measurement.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_RESULTS_BASELINE(
        "application.config.project.result.baseline",
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
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_HORIZONTAL_PRINCIPLES(
        "application.config.project.horizontal.principles",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
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

        fun getFieldsThatDependsOnBudgetSetting(): Set<String> =
            setOf(
                PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN.id,
                PARTNER_BUDGET_PERIODS.id,
                PROJECT_LUMP_SUMS_DESCRIPTION.id,
                PARTNER_BUDGET_STAFF_COST_STAFF_FUNCTION.id,
                PARTNER_BUDGET_STAFF_COST_COMMENTS.id,
                PARTNER_BUDGET_STAFF_COST_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                PARTNER_BUDGET_STAFF_COST_PRICE_PER_UNIT.id,
                PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_DESCRIPTION.id,
                PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_COMMENTS.id,
                PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_PRICE_PER_UNIT.id,
                PARTNER_BUDGET_EXTERNAL_EXPERTISE_DESCRIPTION.id,
                PARTNER_BUDGET_EXTERNAL_EXPERTISE_COMMENTS.id,
                PARTNER_BUDGET_EXTERNAL_EXPERTISE_AWARD_PROCEDURE.id,
                PARTNER_BUDGET_EXTERNAL_EXPERTISE_INVESTMENT.id,
                PARTNER_BUDGET_EXTERNAL_EXPERTISE_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                PARTNER_BUDGET_EXTERNAL_EXPERTISE_PRICE_PER_UNIT.id,
                PARTNER_BUDGET_EQUIPMENT_DESCRIPTION.id,
                PARTNER_BUDGET_EQUIPMENT_COMMENTS.id,
                PARTNER_BUDGET_EQUIPMENT_AWARD_PROCEDURE.id,
                PARTNER_BUDGET_EQUIPMENT_INVESTMENT.id,
                PARTNER_BUDGET_EQUIPMENT_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                PARTNER_BUDGET_EQUIPMENT_PRICE_PER_UNIT.id,
                PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_DESCRIPTION.id,
                PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_COMMENTS.id,
                PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_AWARD_PROCEDURE.id,
                PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_INVESTMENT.id,
                PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_PRICE_PER_UNIT.id,
                PARTNER_BUDGET_UNIT_COSTS_PROGRAMME_UNIT_COSTS.id,
                PARTNER_BUDGET_UNIT_COSTS_DESCRIPTION.id,
                PARTNER_BUDGET_UNIT_COSTS__UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                PARTNER_BUDGET_UNIT_COSTS_PRICE_PER_UNIT.id
            )
    }

}
