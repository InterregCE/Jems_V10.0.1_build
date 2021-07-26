export type ApplicationFormModel = {
  [key: string]: ApplicationFormModel | string;
};
export const APPLICATION_FORM = {
  SECTION_A: {
    PROJECT_IDENTIFICATION: {
      ID: 'application.config.project.id',
      ACRONYM: 'application.config.project.acronym',
      TITLE: 'application.config.project.title',
      DURATION: 'application.config.project.duration',
      PRIORITY: 'application.config.project.priority',
      OBJECTIVE: 'application.config.project.objective'
    },
    PROJECT_SUMMARY: {
      SUMMARY: 'application.config.project.summary'
    }
  },
  SECTION_B: {
    IDENTITY: {
      ROLE: 'application.config.project.partner.role',
      ABBREVIATED_ORGANISATION_NAME: 'application.config.project.partner.name',
      ORIGINAL_NAME_OF_ORGANISATION: 'application.config.project.organization.original.name',
      ENGLISH_NAME_OF_ORGANISATION: 'application.config.project.organization.english.name',
      DEPARTMENT_UNIT_DIVISION: 'application.config.project.organization.department',
      TYPE: 'application.config.project.partner.type',
      LEGAL_STATUS: 'application.config.project.partner.legal.status',
      VAT_IDENTIFIER: 'application.config.project.partner.vat',
      VAT_RECOVERY: 'application.config.project.partner.recoverVat',
    },
    ADDRESS: {
      MAIN: {
        COUNTRY_AND_NUTS: 'application.config.project.partner.main-address.country.and.nuts',
        STREET: 'application.config.project.partner.main-address.street',
        HOUSE_NUMBER: 'application.config.project.partner.main-address.housenumber',
        POSTAL_CODE: 'application.config.project.partner.main-address.postalcode',
        CITY: 'application.config.project.partner.main-address.city',
        HOMEPAGE: 'application.config.project.partner.main-address.homepage',
      },
      SECONDARY: {
        COUNTRY_AND_NUTS: 'application.config.project.partner.secondary-address.country.and.nuts',
        STREET: 'application.config.project.partner.secondary-address.street',
        HOUSE_NUMBER: 'application.config.project.partner.secondary-address.housenumber',
        POSTAL_CODE: 'application.config.project.partner.secondary-address.postalcode',
        CITY: 'application.config.project.partner.secondary-address.city'
      }
    },
    CONTACT: {
      LEGAL_REPRESENTATIVE: {
        TITLE: 'application.config.project.partner.representative.title',
        FIRST_NAME: 'application.config.project.partner.representative.first.name',
        LAST_NAME: 'application.config.project.partner.representative.last.name'
      },
      CONTACT_PERSON: {
        TITLE: 'application.config.project.partner.contact.title',
        FIRST_NAME: 'application.config.project.partner.contact.first.name',
        LAST_NAME: 'application.config.project.partner.contact.last.name',
        EMAIL: 'application.config.project.partner.contact.email',
        TELEPHONE: 'application.config.project.partner.contact.telephone'
      }
    },
    MOTIVATION: {
      COMPETENCES: 'application.config.project.partner.motivation.organization.relevance',
      ROLE: 'application.config.project.partner.motivation.organization.role',
      ROLE_EXPERIENCE: 'application.config.project.partner.motivation.organization.experience'
    },
    BUDGET_AND_CO_FINANCING: {
      PARTNER_BUDGET_AND_CO_FINANCING: 'application.config.project.partner.budget.and.co.financing',
      PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN: 'application.config.project.partner.co.financing.add.new.contribution.origin',
      PARTNER_BUDGET_PERIODS: 'application.config.project.partner.budget.periods',
      STAFF_COST: {
        STAFF_FUNCTION: 'application.config.project.partner.budget.staff.cost.staff.function',
        COMMENT: 'application.config.project.partner.budget.staff.cost.comment',
        UNIT_TYPE_AND_NUMBER_OF_UNITS: 'application.config.project.partner.budget.staff.cost.unit.type.and.number.of.units',
        PRICE_PER_UNIT: 'application.config.project.partner.budget.staff.cost.price.per.unit'
      },
      TRAVEL_AND_ACCOMMODATION: {
        DESCRIPTION: 'application.config.project.partner.budget.travel.and.accommodation.description',
        UNIT_TYPE_AND_NUMBER_OF_UNITS: 'application.config.project.partner.budget.travel.and.accommodation.unit.type.and.number.of.units',
        PRICE_PER_UNIT: 'application.config.project.partner.budget.travel.and.accommodation.price.per.unit',
      },
      EXTERNAL_EXPERTISE: {
        DESCRIPTION: 'application.config.project.partner.budget.external.expertise.description',
        AWARD_PROCEDURE: 'application.config.project.partner.budget.external.expertise.award.procedure',
        INVESTMENT: 'application.config.project.partner.budget.external.expertise.investment',
        UNIT_TYPE_AND_NUMBER_OF_UNITS: 'application.config.project.partner.budget.external.expertise.unit.type.and.number.of.units',
        PRICE_PER_UNIT: 'application.config.project.partner.budget.external.expertise.price.per.unit'
      },
      EQUIPMENT: {
        DESCRIPTION: 'application.config.project.partner.budget.equipment.description',
        AWARD_PROCEDURE: 'application.config.project.partner.budget.equipment.award.procedure',
        INVESTMENT: 'application.config.project.partner.budget.equipment.investment',
        UNIT_TYPE_AND_NUMBER_OF_UNITS: 'application.config.project.partner.budget.equipment.unit.type.and.number.of.units',
        PRICE_PER_UNIT: 'application.config.project.partner.budget.equipment.price.per.unit'
      },
      INFRASTRUCTURE_AND_WORKS: {
        DESCRIPTION: 'application.config.project.partner.budget.infrastructure.and.works.description',
        AWARD_PROCEDURE: 'application.config.project.partner.budget.infrastructure.and.works.award.procedure',
        INVESTMENT: 'application.config.project.partner.budget.infrastructure.and.works.investment',
        UNIT_TYPE_AND_NUMBER_OF_UNITS: 'application.config.project.partner.budget.infrastructure.and.works.unit.type.and.number.of.units',
        PRICE_PER_UNIT: 'application.config.project.partner.budget.infrastructure.and.works.price.per.unit'
      },
      UNIT_COSTS: {
        PROGRAMME_UNIT_COSTS: 'application.config.project.partner.budget.unit.costs.programme.unit.costs',
        DESCRIPTION: 'application.config.project.partner.budget.unit.costs.description',
        UNIT_TYPE_AND_NUMBER_OF_UNITS: 'application.config.project.partner.budget.unit.costs.unit.type.and.number.of.units',
        PRICE_PER_UNIT: 'application.config.project.partner.budget.unit.costs.price.per.unit'
      }
    },
    PARTNER_ASSOCIATED_ORGANIZATIONS: 'application.config.project.partner.associated.organizations',
  },
  SECTION_C: {
    PROJECT_OVERALL_OBJECTIVE: {
      OVERALL_OBJECTIVE: 'application.config.project.overall.objective'
    },
    PROJECT_RELEVANCE_AND_CONTEXT: {
      TERRITORIAL_CHALLENGES: 'application.config.project.territorial.challenges',
      HOW_ARE_CHALLENGES_AND_OPPORTUNITIES_TACKLED: 'application.config.project.how.are.challenges.and.opportunities.tackled',
      WHY_IS_COOPERATION_NEEDED: 'application.config.project.why.is.cooperation.needed',
      TARGET_GROUP: 'application.config.project.target.group',
      STRATEGY_CONTRIBUTION: 'application.config.project.strategy.contribution',
      SYNERGIES: 'application.config.project.synergies',
      HOW_BUILDS_PROJECT_ON_AVAILABLE_KNOWLEDGE: 'application.config.project.how.builds.project.on.available.knowledge',
    },
    PROJECT_PARTNERSHIP: {
      PARTNERSHIP: 'application.config.project.partnership',
    },
    PROJECT_RESULT: {
      PROGRAMME_RESULT_INDICATOR_AMD_MEASUREMENT_UNIT: 'application.config.project.result.indicator.and.measurement.unit',
      TARGET_VALUE: 'application.config.project.result.target.value',
      DELIVERY_PERIOD: 'application.config.project.result.delivery.period',
      DESCRIPTION: 'application.config.project.result.description'
    },
    PROJECT_MANAGEMENT: {
      COORDINATION: 'application.config.project.coordination',
      QUALITY_MEASURES: 'application.config.project.quality.measures',
      COMMUNICATION_APPROACH: 'application.config.project.communication.approach',
      FINANCIAL_MANAGEMENT_AND_REPORTING: 'application.config.project.financial.management.and.reporting',
      COOPERATION_CRITERIA: 'application.config.project.cooperation.criteria',
      HORIZONTAL_PRINCIPLES: 'application.config.project.horizontal.principles'
    },
    PROJECT_LONG_TERM_PLANS: {
      OWNERSHIP: 'application.config.project.ownership',
      DURABILITY: 'application.config.project.durability',
      TRANSFERABILITY: 'application.config.project.transferability',
    }
  }
};
