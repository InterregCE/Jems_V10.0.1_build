import user from '../../../fixtures/users.json';
import application from '../../../fixtures/api/application/application.json';
import call from "../../../fixtures/api/call/1.step.call.json";
import {faker} from "@faker-js/faker";

context('Application contracting tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    call.preSubmissionCheckSettings.pluginKey = "jems-pre-condition-check-off";
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-519 A project can be set to contracted and partner report created', () => {
    cy.loginByRequest(user.admin.email);
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

      cy.wait(1000);

      cy.contains('Modification ').click();
      cy.contains('button', 'Open new modification').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Contract monitoring').click();
      cy.contains('button', 'Set project to contracted').should('be.disabled');
      cy.contains('Check & Submit').click();
      cy.contains('button', 'Run pre-submission check').click();
      cy.contains('button', 'Re-submit project application').click();
      cy.contains('button', 'Confirm').click();
      cy.wait(400);
      cy.contains('Contract monitoring').click({force: true});
      cy.contains('button', 'Set project to contracted').should('be.disabled');
      cy.contains('Modification').click({force: true});
      cy.get('span.mat-button-toggle-label-content').contains('Approve modification').click();
      cy.get('svg.mat-datepicker-toggle-default-icon:first').click();
      cy.contains('td', '1').click({force: true});
      cy.get('svg.mat-datepicker-toggle-default-icon:last').click();
      cy.contains('td', '2').click();
      cy.contains('button', 'Save changes').click();
      cy.contains('Contract monitoring').click();
      cy.contains('button', 'Set project to contracted').should('be.enabled');
      cy.contains('button', 'Set project to contracted').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Project overview').click();
      cy.contains('mat-chip', 'Contracted').should('be.visible');
      cy.contains('mat-panel-title', 'Reporting').should('be.visible');
      cy.wait(1000);
      cy.contains('mat-expansion-panel', 'Partner reports').contains('Lead Partner').click();
      cy.contains('button', 'Add Partner Report').click();
      cy.contains('Partner progress report identification').should('be.visible');
    });
  });

  it('TB-735 Contracting sections appear at the right moment and remains editable', () => {
    cy.loginByRequest(user.admin.email);
    cy.createApplication(application).then(applicationId => {
      cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});

      cy.contains('Check & Submit').click();
      cy.contains('button', 'Run pre-submission check').click();
      cy.contains('button', 'Submit project application').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Assessment & Decision').click();
      cy.contains('Contracting').should('not.exist');
      cy.contains('Enter eligibility assessment').click();
      cy.get('span.mat-radio-inner-circle:first').click();
      cy.contains('button', 'Submit eligibility assessment').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Contracting').should('not.exist');
      cy.contains('Enter quality assessment').click();
      cy.get('span.mat-radio-inner-circle:first').click();
      cy.contains('button', 'Submit quality assessment').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Contracting').should('not.exist');
      cy.contains('Enter eligibility decision').click();
      cy.get('span.mat-radio-inner-circle:first').click();
      cy.get('svg.mat-datepicker-toggle-default-icon').click();
      cy.contains('td', '1').click();
      cy.contains('button', 'Submit eligibility decision').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Contracting').should('not.exist');
      cy.contains('Enter funding decision').click();
      cy.get('span.mat-radio-inner-circle:first').click();
      cy.get('svg.mat-datepicker-toggle-default-icon').click();
      cy.contains('td', '1').click();
      cy.contains('button', 'Submit funding decision').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Contracting').scrollIntoView().should('be.visible');
      cy.contains('Contract monitoring').should('be.visible');
      cy.contains('Project management').should('be.visible');
      cy.contains('Reporting schedule').should('be.visible');
      cy.contains('span', 'Modification').click({force: true});
      cy.contains('button', 'Open new modification').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Contracting').scrollIntoView().should('be.visible');
      cy.contains('Contract monitoring').should('be.visible');
      cy.contains('Project management').should('be.visible');
      cy.contains('Reporting schedule').should('be.visible');
      cy.contains('Contract monitoring').click();
      cy.contains('div', 'Comment').find('textarea').should('not.have.attr', 'readonly');
      cy.contains('Check & Submit').click();
      cy.contains('Run pre-submission check').click();
      cy.contains('Re-submit project application').click();
      cy.contains('Confirm').click();
      cy.contains('Contract monitoring').click({force: true});
      cy.contains('div', 'Comment').find('textarea').should('not.have.attr', 'readonly');
    })
  })

  it('TB-755 Contracting Project management', () => {
    cy.fixture("project/contracting/TB-755.json").then(testData => {
      cy.loginByRequest(user.admin.email);
      testData.applicationUser.email = faker.internet.email();
      cy.createUser(testData.applicationUser);
      testData.anotherUser.email = faker.internet.email();
      cy.createUser(testData.anotherUser);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      })
      cy.loginByRequest(testData.applicationUser.email);
      cy.createApprovedApplication(application, user.admin.email).then(applicationId => {
        cy.loginByRequest(testData.applicationUser.email);
        cy.visit(`/app/project/detail/${applicationId}/projectManagement`, {failOnStatusCode: false});
        cy.get('input[name="title"]').should('not.have.attr', 'disabled');
        cy.contains('Project manager').should('be.visible');
        cy.contains('Finance manager').scrollIntoView().should('be.visible');
        cy.contains('Communication manager').scrollIntoView().should('be.visible');
        cy.contains('div.mat-form-field-flex', 'Title').type(faker.word.noun());
        cy.contains('button', 'Save changes').click();

        cy.loginByRequest(user.admin.email);
        cy.visit(`/app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});

        cy.get('mat-expansion-panel#partner-collaborators-panel').find('input.mat-input-element').type(testData.anotherUser.email);
        cy.contains('button', 'Save changes').click();

        cy.loginByRequest(testData.anotherUser.email);
        cy.visit(`/app/project/detail/${applicationId}/projectManagement`, {failOnStatusCode: false});
        cy.get('input[name="title"]').should('have.attr', 'disabled');

        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.wait(2000);
        cy.contains('mat-expansion-panel', 'Contracting').contains('Project Monitoring').should('not.exist');
      })
    })
  })
});
