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
    }
  },
  SECTION_C: {
    PROJECT_RESULT: {
      PROGRAMME_RESULT_INDICATOR_AMD_MEASUREMENT_UNIT: 'application.config.project.result.indicator.and.measurement.unit',
      TARGET_VALUE: 'application.config.project.result.target.value',
      DELIVERY_PERIOD: 'application.config.project.result.delivery.period',
      DESCRIPTION: 'application.config.project.result.description'
    }
  }
};
