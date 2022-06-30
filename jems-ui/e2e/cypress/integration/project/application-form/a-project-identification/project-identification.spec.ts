import user from '../../../../fixtures/users.json';
import faker from '@faker-js/faker';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';

context('Project identification tests', () => {

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
    cy.fixture('project/application-form/TB-556').then(testData => {

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
});
