import user from '../../../fixtures/users.json';
import faker from '@faker-js/faker';
import call from '../../../fixtures/api/call/1.step.call.json';
import application from '../../../fixtures/api/application/application.json';

context('Project management tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      call.generalCallSettings.id = callId;
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
    cy.get('input[name="acronym"]').type(`${application.details.acronym}`);

    cy.contains('Create project application').click();

    cy.get('jems-project-page-template').find('h1').contains(`${application.details.acronym}`);
    cy.location('pathname').then(path => {
      const regex = /detail\/(\d+)\//;
      const match = path.match(regex);
      cy.wrap(match[1]).as('applicationId');
    });
  });

  it('TB-556 Applicant can open and edit his projects', () => {
    cy.fixture('project/application-form/TB-390').then(testData => {

      cy.visit('/');

      cy.contains('My applications').should('be.visible');

      cy.contains(application.details.acronym).click({force: true});

      cy.get('span:contains("Project identification")').eq(2).click();

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

  it('TB-581 Applicant can add partner to the project', function () {
    cy.fixture('project/application-form/TB-581').then(testData => {
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});
      cy.contains('Partners overview').click();
      cy.contains('Add new partner').click();
      const leadPartner = testData.partner;
      cy.contains('button', leadPartner.role).click();
      cy.contains('div', 'Abbreviated name of the organisation').find('input').type(leadPartner.abbreviation);
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
})
