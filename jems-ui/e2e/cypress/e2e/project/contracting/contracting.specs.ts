import user from '../../../fixtures/users.json';
import application from '../../../fixtures/api/application/application.json';
import call from "../../../fixtures/api/call/1.step.call.json";

context('Application contracting tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-519 A project can be set to contracted and partner report created', () => {
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.loginByRequest(user.admin.email);
      cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

      cy.wait(1000); // TODO remove after MP2-2391 is fixed

      cy.contains('Modification ').click();
      cy.contains('button', 'Open new modification').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Contract monitoring').click();
      cy.contains('button', 'Set project to contracted').should('be.disabled');
      cy.contains('Check & Submit').click();
      cy.contains('button', 'Run pre-submission check').click();
      cy.contains('button', 'Re-submit project application').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Contract monitoring').click();
      cy.contains('button', 'Set project to contracted').should('be.disabled');
      cy.contains('Modification').scrollIntoView().click();
      cy.get('span.mat-button-toggle-label-content').contains('Approve modification').click();
      cy.get('svg.mat-datepicker-toggle-default-icon:first').click();
      cy.contains('td', '1').click();
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
      cy.contains('mat-expansion-panel', 'Partner reports').contains('Lead Partner').click();
      cy.contains('button', 'Add Partner Report').click();
      cy.contains('Partner progress report identification').should('be.visible');
    });
  });
});
