import user from '../../../fixtures/users.json';
import application from '../../../fixtures/api/application/application.json';
import call from "../../../fixtures/api/call/1.step.call.json";

context('Project partners tests', () => {

  before(() => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createCall(call, user.programmeUser.email).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId, user.programmeUser.email);
      cy.createApplication(application).then(applicationId => {
        cy.updateProjectIdentification(applicationId, application.identification);
      });
    });
  });

  beforeEach(function() {
    cy.loginByRequest(user.applicantUser.email);
    cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});
  });

  it('TB-581 Applicant can add lead partner to the project', function () {
    cy.fixture('project/application-form/project-partners/TB-581.json').then(testData => {
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
    cy.fixture('project/application-form/project-partners/TB-608.json').then(testData => {
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
      cy.contains('Partner organisation details saved successfully.').should('be.visible');
    });
  });

  it('TB-609 Applicant can edit partners contact info', function () {
    cy.fixture('project/application-form/project-partners/TB-609.json').then(testData => {
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
    cy.fixture('project/application-form/project-partners/TB-610.json').then(testData => {
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
    cy.fixture('project/application-form/project-partners/TB-628.json').then(testData => {
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

  it('TB-633 Applicant can edit partners confinancing info', function () {
    cy.fixture('project/application-form/project-partners/TB-633.json').then(testData => {
      cy.contains(this.partnerAbbreviation).click();
      cy.get('.mat-tab-header-pagination-after').click();
      cy.wait(500);
      cy.contains('a', 'Co-financing').click();

      cy.contains('div.jems-table-config', 'Source').within(() => {
        cy.contains('Co-financing source').click();
        cy.root().closest('body').contains(testData.coFinancing.fund).click();

        cy.get('input').type(testData.coFinancing.rate);
        cy.contains('Partner contribution').next().should('contain.text', testData.coFinancing.partnerContribution.amount)
          .next().should('contain.text', testData.coFinancing.partnerContribution.rate);
      });

      let alertMessage = `The total of contribution must match the total partner contribution (difference "${testData.coFinancing.originOfPartnerContributionDifference}")`;
      cy.contains(alertMessage).should('be.visible');

      cy.contains('Source of contribution').parent().next().within(() => {
        cy.contains('Legal status').click();
        cy.root().closest('body').contains(testData.coFinancing.originsOfPartnerContribution[0].legalStatus).click();
        cy.get('input').type(formatAmount(testData.coFinancing.originsOfPartnerContribution[0].amount));
      });

      cy.contains('Add new contribution origin').click();
      cy.contains('Source of contribution').parent().next().next().within(() => {
        cy.contains('mat-form-field', 'Source of contribution').find('input').type(testData.coFinancing.originsOfPartnerContribution[1].source);
        cy.contains('Legal status').click();
        cy.root().closest('body').contains(testData.coFinancing.originsOfPartnerContribution[1].legalStatus).click();
        cy.get('input').eq(1).type(formatAmount(testData.coFinancing.originsOfPartnerContribution[1].amount));
      });

      cy.contains('Add new contribution origin').click();
      cy.contains('Source of contribution').parent().next().next().next().within(() => {
        cy.contains('mat-form-field', 'Source of contribution').find('input').type(testData.coFinancing.originsOfPartnerContribution[2].source);
        cy.contains('Legal status').click();
        cy.root().closest('body').contains(testData.coFinancing.originsOfPartnerContribution[2].legalStatus).click();
        cy.get('input').eq(1).type(formatAmount(testData.coFinancing.originsOfPartnerContribution[2].amount));
      });

      alertMessage = 'The total of contribution must match the total partner contribution';
      cy.contains(alertMessage).should('not.exist');

      cy.contains('Save changes').click();
      cy.contains('Co-financing and partner contributions saved successfully').should('be.visible');
    });
  });

  it('TB-634 Applicant can edit partners state aid info', function () {
    cy.fixture('project/application-form/project-partners/TB-634.json').then(testData => {
      cy.contains(this.partnerAbbreviation).click();
      cy.get('.mat-tab-header-pagination-after').click();
      cy.wait(500);
      cy.contains('a', 'State Aid').click();

      cy.contains('div', testData.stateAid.question1.text).within(() => {
        cy.contains(testData.stateAid.question1.answer).click();
        testData.stateAid.question1.justification.forEach(justification => {
          cy.root().closest('jems-multi-language-container').contains('button', justification.language).click();
          cy.get('textarea').type(justification.translation);
        });
      });

      cy.contains('div', testData.stateAid.question2.text).within(() => {
        cy.contains(testData.stateAid.question2.answer).click();
        testData.stateAid.question2.justification.forEach(justification => {
          cy.root().closest('jems-multi-language-container').contains('button', justification.language).click();
          cy.get('textarea').type(justification.translation);
        });
      });

      cy.contains('div', testData.stateAid.question3.text).within(() => {
        cy.contains(testData.stateAid.question3.answer).click();
        testData.stateAid.question3.justification.forEach(justification => {
          cy.root().closest('jems-multi-language-container').contains('button', justification.language).click();
          cy.get('textarea').type(justification.translation);
        });
      });

      cy.contains('div', testData.stateAid.question4.text).within(() => {
        cy.contains(testData.stateAid.question4.answer).click();
        testData.stateAid.question4.justification.forEach(justification => {
          cy.root().closest('jems-multi-language-container').contains('button', justification.language).click();
          cy.get('textarea').type(justification.translation);
        });
      });

      cy.contains('GBER scheme / de minimis').click({force: true});
      cy.contains('General de minimis').click();

      cy.contains('Save changes').click();
      cy.contains('Partner state aid was saved successfully.').should('be.visible');
    });
  });

  it('TB-638 Applicant can add associated organisation', function () {
    cy.fixture('project/application-form/project-partners/TB-638.json').then(testData => {
      cy.contains('Associated organisations').click();

      cy.contains('Add new associated organisation').click();

      cy.contains('div', 'Name of the organisation in original language').find('input').type(testData.associatedOrganisation.nameInOriginalLanguage);
      cy.contains('div', 'Name of the organisation in english').find('input').type(testData.associatedOrganisation.nameInEnglish);

      cy.contains('mat-form-field', 'Partner').click();
      cy.contains('mat-option', this.partnerAbbreviation).click();

      cy.contains('div', 'Country').click();
      cy.contains(testData.associatedOrganisation.address.country).click();
      cy.contains('div', 'NUTS 2').click();
      cy.contains(testData.associatedOrganisation.address.nutsRegion2).click();
      cy.contains('div', 'NUTS 3').click();
      cy.contains(testData.associatedOrganisation.address.nutsRegion3).click();

      cy.contains('div', 'Street').find('input').type(testData.associatedOrganisation.address.street);
      cy.contains('div', 'House number').find('input').type(testData.associatedOrganisation.address.houseNumber);
      cy.contains('div', 'Postal code').find('input').type(testData.associatedOrganisation.address.postalCode);
      cy.contains('div', 'City').find('input').type(testData.associatedOrganisation.address.city);

      cy.get('mat-form-field:contains("Title")').eq(0).find('input').type(testData.associatedOrganisation.legalRepresentative.title);
      cy.get('mat-form-field:contains("First name")').eq(0).find('input').type(testData.associatedOrganisation.legalRepresentative.firstName);
      cy.get('mat-form-field:contains("Last name")').eq(0).find('input').type(testData.associatedOrganisation.legalRepresentative.lastName);

      cy.get('mat-form-field:contains("Title")').eq(1).find('input').type(testData.associatedOrganisation.contactPerson.title);
      cy.get('mat-form-field:contains("First name")').eq(1).find('input').type(testData.associatedOrganisation.contactPerson.firstName);
      cy.get('mat-form-field:contains("Last name")').eq(1).find('input').type(testData.associatedOrganisation.contactPerson.lastName);
      cy.contains('div', 'E-mail address').find('input').type(testData.associatedOrganisation.contactPerson.email);
      cy.contains('div', 'Telephone no.').find('input').type(testData.associatedOrganisation.contactPerson.telephone);

      testData.associatedOrganisation.roleDescription.forEach(roleDescription => {
        cy.get('jems-multi-language-container').then(roleDescriptionSection => {
          cy.wrap(roleDescriptionSection).contains('button', roleDescription.language).click();
          cy.wrap(roleDescriptionSection).find('textarea').type(roleDescription.translation);
        });
      });

      cy.contains('button', 'Create').click();
      cy.contains('Associated organisations').click();

      cy.wait(1000);

      cy.get('jems-project-application-form-associated-organizations-list').within(() => {
        cy.contains('AO1').should('be.visible');
        cy.contains('Active').should('be.visible');
      });
    });
  });

  function setTranslatedField(values, index) {
    values.forEach(value => {
      cy.contains('button', value.language).click();
      cy.get('mat-row').last().find('mat-cell').eq(index).scrollIntoView().type(value.translation);
    });
  }

  function setAmountField(amount, index) {
    const formattedAmount = formatAmount(amount);
    cy.get('mat-row').last().find('mat-cell').eq(index).find('input').scrollIntoView().type(formattedAmount, {force: true});
  }

  function selectUnitCost(unitCost) {
    cy.get('mat-row').last().find('mat-select').first().click();
    cy.root().closest('body').find('mat-option').contains(unitCost).click();
  }

  function formatAmount(amount) {
    return new Intl.NumberFormat('de-DE').format(amount);
  }
});
