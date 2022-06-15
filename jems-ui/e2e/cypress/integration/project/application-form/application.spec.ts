import user from '../../../fixtures/users.json';
import faker from '@faker-js/faker';
import call from '../../../fixtures/api/call/1.step.call.json';
import application from '../../../fixtures/api/application/application.json';

context('Project management tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  beforeEach(() => {
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-390 Applicant can apply for a call', () => {
    cy.visit('/');

    cy.contains('Call list').should('be.visible');

    cy.contains(call.generalCallSettings.name).click({force: true});
    cy.contains('jems-breadcrumb', call.generalCallSettings.name).should('exist');
    cy.contains('Apply').click();

    application.details.acronym = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
    cy.wrap(application.details.acronym).as('applicationAcronym')
    cy.get('input[name="acronym"]').type(`${application.details.acronym}`);

    cy.contains('Create project application').click();

    cy.get('jems-project-page-template').find('h1').contains(`${application.details.acronym}`);
    cy.location('pathname').then(path => {
      const regex = /detail\/(\d+)\//;
      const match = path.match(regex);
      cy.wrap(match[1]).as('applicationId');
    });
  });

  it('TB-556 Applicant can open and edit his projects', function () {
    cy.fixture('project/application-form/TB-390').then(testData => {

      cy.visit('/');

      cy.contains('My applications').should('be.visible');

      cy.contains(this.applicationAcronym).click({force: true});

      cy.get('span:contains("Project identification")').should('be.visible').eq(2).click();

      application.identification.acronym = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;

      cy.contains('div', 'Project acronym').find('input').clear().type(application.identification.acronym);

      application.identification.title.forEach(title => {
        cy.contains('jems-multi-language-container', 'Project title').then(el => {
          cy.wrap(el).contains('button', title.language).click();
          cy.wrap(el).find('textarea').type(title.translation);
        });
      });

      cy.contains('div', 'Project duration in months').find('input').type(application.identification.duration.toString());

      cy.contains('div', 'Programme priority').find('mat-select').click();
      cy.contains(testData.programmePriority).click();

      cy.contains(testData.specificObjective).click();

      application.identification.intro.forEach(summary => {
        cy.contains('jems-multi-language-container', 'Summary').then(el => {
          cy.wrap(el).contains('button', summary.language).click();
          cy.wrap(el).find('textarea').type(summary.translation);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Project identification saved successfully.').should('exist');
    });
  });

  it('TB-581 Applicant can add lead partner to the project', function () {
    cy.fixture('project/application-form/TB-581').then(testData => {
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});
      cy.contains('Partners overview').click();
      cy.contains('Add new partner').click();
      const leadPartner = testData.partner;
      cy.contains('button', leadPartner.role).click();
      cy.contains('div', 'Abbreviated name of the organisation').find('input').type(leadPartner.abbreviation);
      cy.wrap(leadPartner.abbreviation).as('partnerAbbreviation');
      cy.contains('div', 'Name of the organisation in original language').find('input').type(leadPartner.nameInOriginalLanguage);
      cy.contains('div', 'Name of the organisation in english').find('input').type(leadPartner.nameInEnglish);
      cy.contains('div', 'Department').find('textarea').type(leadPartner.nameInEnglish);

      cy.contains('div', 'Type of partner').find('mat-select').click();
      cy.contains('mat-option', leadPartner.partnerType).click();
      cy.contains('div', 'Subtype of partner').find('mat-select').click();
      cy.contains('mat-option', leadPartner.partnerSubType).click();
      cy.contains('div', 'Legal status').find('mat-select').click();
      cy.contains('mat-option', leadPartner.legalStatus).click();
      cy.contains('div', 'Sector of activity at NACE group level').find('input').click();
      cy.contains('mat-option', leadPartner.nace).click();

      cy.contains('div', 'VAT number').find('input').type(leadPartner.vat);
      cy.get('mat-button-toggle-group[formcontrolname="vatRecovery"]').contains(leadPartner.vatRecovery).click();
      cy.contains('div', 'Other identifier number').find('input').type(leadPartner.otherIdentifierNumber);
      cy.contains('div', 'Other identifier number').find('input').type(leadPartner.otherIdentifierNumber);
      leadPartner.otherIdentifierDescription.forEach(description => {
        cy.contains('jems-multi-language-container', 'Other identifier description').then(el => {
          cy.wrap(el).contains('button', description.language).click();
          cy.wrap(el).find('input').type(description.translation);
        });
      });
      cy.contains('div', 'PIC').find('input').type(leadPartner.pic);

      cy.contains('button', 'Create').click();
      cy.contains('div', 'Project partners').should(partnersSection => {
        expect(partnersSection).to.contain(leadPartner.abbreviation);
      });
    });
  });

  it('TB-608 Applicant can edit partners address', function () {
    cy.fixture('project/application-form/TB-608').then(testData => {
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});
      cy.contains(this.partnerAbbreviation).click();
      cy.contains('a', 'Address').click();

      cy.get('jems-project-application-form-address').eq(0).then((mainAddressSection) => {
        cy.wrap(mainAddressSection).contains('div', 'Country').find('input').click();
        cy.contains('mat-option', testData.mainAddress.country).click();
        cy.wrap(mainAddressSection).contains('div', 'NUTS 2').find('input').click();
        cy.contains('mat-option', testData.mainAddress.nutsRegion2).click();
        cy.wrap(mainAddressSection).contains('div', 'NUTS 3').find('input').click();
        cy.contains('mat-option', testData.mainAddress.nutsRegion3).click();

        cy.wrap(mainAddressSection).contains('div', 'Street').find('input').type(testData.mainAddress.street);
        cy.wrap(mainAddressSection).contains('div', 'House number').find('input').type(testData.mainAddress.houseNumber);
        cy.wrap(mainAddressSection).contains('div', 'Postal code').find('input').type(testData.mainAddress.postalCode);
        cy.wrap(mainAddressSection).contains('div', 'City').find('input').type(testData.mainAddress.city);
        cy.wrap(mainAddressSection).contains('div', 'Homepage').find('input').type(testData.mainAddress.homepage);
      });

      cy.contains('Address of department').next().then((departmentAddressSection) => {
        cy.wrap(departmentAddressSection).contains('div', 'Country').find('input').click();
        cy.contains('mat-option', testData.departmentAddress.country).click();
        cy.wrap(departmentAddressSection).contains('div', 'NUTS 2').find('input').click();
        cy.contains('mat-option', testData.departmentAddress.nutsRegion2).click();
        cy.wrap(departmentAddressSection).contains('div', 'NUTS 3').find('input').click();
        cy.contains('mat-option', testData.departmentAddress.nutsRegion3).click();

        cy.wrap(departmentAddressSection).contains('div', 'Street').find('input').type(testData.departmentAddress.street);
        cy.wrap(departmentAddressSection).contains('div', 'House number').find('input').type(testData.departmentAddress.houseNumber);
        cy.wrap(departmentAddressSection).contains('div', 'Postal code').find('input').type(testData.departmentAddress.postalCode);
        cy.wrap(departmentAddressSection).contains('div', 'City').find('input').type(testData.departmentAddress.city);
      });

      cy.contains('Save changes').click();
      // TODO add an assertion for successful save as soon as bug MP2-2274 is fixed
    });
  });

  it('TB-609 Applicant can edit partners contact info', function () {
    cy.fixture('project/application-form/TB-609').then(testData => {
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});
      cy.contains(this.partnerAbbreviation).click();
      cy.contains('a', 'Contact').click();

      cy.get('input[name="partnerRepresentativeTitle"]').type(testData.legalRepresentative.title)
      cy.get('input[name="partnerRepresentativeFirstName"]').type(testData.legalRepresentative.firstName)
      cy.get('input[name="partnerRepresentativeLastName"]').type(testData.legalRepresentative.lastName);

      cy.get('input[name="partnerContactTitle"]').type(testData.contactPerson.title)
      cy.get('input[name="partnerContactFirstName"]').type(testData.contactPerson.firstName)
      cy.get('input[name="partnerContactLastName"]').type(testData.contactPerson.lastName);
      cy.get('input[name="partnerContactEmail"]').type(testData.contactPerson.email);
      cy.get('input[name="partnerContactTelephone"]').type(testData.contactPerson.number);

      cy.contains('Save changes').click();
      cy.contains('Partner contact saved successfully').should('be.visible');
    });
  });

  it('TB-610 Applicant can edit partners motivation info', function () {
    cy.fixture('project/application-form/TB-610').then(testData => {
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});
      cy.contains(this.partnerAbbreviation).click();
      cy.contains('a', 'Motivation').click();

      testData.motivation.organizationRelevance.forEach(organizationRelevance => {
        cy.get('jems-multi-language-container').eq(0).then(organizationRelevanceSection => {
          cy.wrap(organizationRelevanceSection).contains('button', organizationRelevance.language).click();
          cy.wrap(organizationRelevanceSection).find('textarea').type(organizationRelevance.translation);
        });
      });

      testData.motivation.organizationRole.forEach(organizationRole => {
        cy.get('jems-multi-language-container').eq(1).then(organizationRoleSection => {
          cy.wrap(organizationRoleSection).contains('button', organizationRole.language).click();
          cy.wrap(organizationRoleSection).find('textarea').type(organizationRole.translation);
        });
      });

      testData.motivation.organizationExperience.forEach(organizationExperience => {
        cy.get('jems-multi-language-container').eq(2).then(organizationExperienceSection => {
          cy.wrap(organizationExperienceSection).contains('button', organizationExperience.language).click();
          cy.wrap(organizationExperienceSection).find('textarea').type(organizationExperience.translation);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Partner motivation and contribution saved successfully.').should('be.visible');
    });
  });

  it('TB-628 Applicant can edit partners budget info', function () {
    cy.fixture('project/application-form/TB-628').then(testData => {
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});
      cy.contains(this.partnerAbbreviation).click();
      cy.contains('a', 'Budget').click();

      cy.contains('h4', 'Staff costs').next().within(() => {
        cy.contains('Add').click();
        setTranslatedField(testData.budget.staffCosts[0].staffFunction, 1);
        setTranslatedField(testData.budget.staffCosts[0].comments, 2);
        setTranslatedField(testData.budget.staffCosts[0].unitType, 3);
        setAmountField(testData.budget.staffCosts[0].numberOfUnits, 4);
        setAmountField(testData.budget.staffCosts[0].pricePerUnit, 5);

        testData.budget.staffCosts[0].budgetPeriods.forEach(budgetPeriods => {
          setAmountField(budgetPeriods.amount, budgetPeriods.number + 6);
        });

        cy.get('mat-row').last().get('mat-cell').eq(15).then(gap => {
          expect(gap).to.contain('0,00');
        });
        cy.contains('Please update the budget table: The sum of the amounts per period must match the budget item total.').should('not.exist');

        cy.contains('button', 'add').click();
        selectUnitCost(testData.budget.staffCosts[1].unitCost);
        setTranslatedField(testData.budget.staffCosts[1].comments, 2);
        setAmountField(testData.budget.staffCosts[1].numberOfUnits, 4);
        testData.budget.staffCosts[1].budgetPeriods.forEach(budgetPeriods => {
          setAmountField(budgetPeriods.amount, budgetPeriods.number + 6);
        });
        cy.get('mat-row').last().find('mat-cell').eq(15).then(gap => {
          expect(gap).to.contain('0,01');
        });
        cy.contains('Please update the budget table: The sum of the amounts per period must match the budget item total.').scrollIntoView().should('be.visible');
      });

      cy.contains('Save changes').click();
      cy.contains('Partner budgets were saved successfully').should('be.visible');

      cy.contains('Staff costs flat rate').click();
      cy.contains('mat-list-item', 'Staff costs flat rate').find('input[type="integer"]').type(testData.staffCostsFlatRate);
      cy.contains('Save changes').click();
      cy.contains('Eventual real cost covered by an activated flat rate will be deleted').should('be.visible');
      cy.get('jems-confirm-dialog').contains('Confirm').click();
      cy.contains('Partner budget options were saved successfully.').should('be.visible');
      cy.contains('Partner budget options were saved successfully.').should('not.exist');

      cy.contains('h4', 'Travel and accommodation').next().within(() => {
        cy.contains('Add').click();
        setTranslatedField(testData.budget.travelCosts[0].description, 0);
        setTranslatedField(testData.budget.travelCosts[0].comments, 1);
        setTranslatedField(testData.budget.travelCosts[0].unitType, 2);
        setAmountField(testData.budget.travelCosts[0].numberOfUnits, 3);
        setAmountField(testData.budget.travelCosts[0].pricePerUnit, 4);
        testData.budget.travelCosts[0].budgetPeriods.forEach(budgetPeriods => {
          setAmountField(budgetPeriods.amount, budgetPeriods.number + 4);
        });
      });

      cy.contains('h4', 'External expertise and services').next().within(() => {
        cy.contains('Add').click();
        setTranslatedField(testData.budget.externalCosts[0].description, 0);
        setTranslatedField(testData.budget.externalCosts[0].comments, 1);
        setTranslatedField(testData.budget.externalCosts[0].awardProcedures, 2);
        setTranslatedField(testData.budget.externalCosts[0].unitType, 4);
        setAmountField(testData.budget.externalCosts[0].numberOfUnits, 5);
        setAmountField(testData.budget.externalCosts[0].pricePerUnit, 6);

        testData.budget.externalCosts[0].budgetPeriods.forEach(budgetPeriods => {
          setAmountField(budgetPeriods.amount, budgetPeriods.number + 7);
        });
      });

      cy.contains('h4', 'Equipment').next().within(() => {
        cy.contains('Add').click();
        cy.get('mat-row').last().find('mat-select').first().click();
        cy.root().closest('body').find('mat-option').contains(testData.budget.equipmentCosts[0].unitCost).click();
        setTranslatedField(testData.budget.equipmentCosts[0].comments, 2);
        setTranslatedField(testData.budget.equipmentCosts[0].awardProcedures, 3);
        setAmountField(testData.budget.equipmentCosts[0].numberOfUnits, 6);

        testData.budget.equipmentCosts[0].budgetPeriods.forEach(budgetPeriods => {
          setAmountField(budgetPeriods.amount, budgetPeriods.number + 7);
        });
      });

      cy.contains('h4', 'Infrastructure and works').next().within(() => {
        cy.contains('Add').click();
        setTranslatedField(testData.budget.infrastructureCosts[0].description, 0);
        setTranslatedField(testData.budget.infrastructureCosts[0].comments, 1);
        setTranslatedField(testData.budget.infrastructureCosts[0].awardProcedures, 2);
        setTranslatedField(testData.budget.infrastructureCosts[0].unitType, 4);
        setAmountField(testData.budget.infrastructureCosts[0].numberOfUnits, 5);
        setAmountField(testData.budget.infrastructureCosts[0].pricePerUnit, 6);

        testData.budget.infrastructureCosts[0].budgetPeriods.forEach(budgetPeriods => {
          setAmountField(budgetPeriods.amount, budgetPeriods.number + 7);
        });
      });

      cy.contains('h4', 'Unit costs covering more than one cost category').next().within(() => {
        cy.contains('Add').click();
        selectUnitCost(testData.budget.unitCosts[0].unitCost);
        setAmountField(testData.budget.unitCosts[0].numberOfUnits, 3);
        testData.budget.unitCosts[0].budgetPeriods.forEach(budgetPeriods => {
          setAmountField(budgetPeriods.amount, budgetPeriods.number + 5);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Partner budgets were saved successfully').should('be.visible');
      
      cy.contains('Flat rate for Staff costs').parents('jems-budget-flat-rate-table').should('contain', formatAmount(testData.staffCostsFlatRateAmount));
    });
  });

  function setTranslatedField(values, index) {
    values.forEach(value => {
      cy.contains('button', value.language).click();
      cy.get('mat-row').last().find('mat-cell').eq(index).type(value.translation);
    });
  }

  function setAmountField(amount, index) {
    const formattedAmount = formatAmount(amount);
    cy.get('mat-row').last().find('mat-cell').eq(index).find('input').type(formattedAmount, {force: true});
  }
  
  function selectUnitCost(unitCost) {
    cy.get('mat-row').last().find('mat-select').first().click();
    cy.root().closest('body').find('mat-option').contains(unitCost).click();
  }
  
  function formatAmount(amount) {
     return new Intl.NumberFormat('de-DE').format(amount);
  }
});
