import user from '../../../fixtures/users.json';
import application from '../../../fixtures/api/application/application.json';
import call from "../../../fixtures/api/call/1.step.call.json";

context('Application contracting', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      call.generalCallSettings.id = callId;
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-519 A project can be set to contracted', () => {

    cy.createFullApplication(application, user.programmeUser.email).then(applicationId => {
      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}/`, {failOnStatusCode: false});
      cy.contains('Contract monitoring').click();
      cy.contains('Set project to contracted').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('Partner reports').should('be.visible');
      cy.contains('Project overview').click();
      cy.get('#status').contains('Contracted').should('be.visible');
      cy.contains('.link', 'A - Project identification').click();
      cy.get('jems-multi-language-form-field').get('[label="project.application.form.field.project.title"]').should('be.visible').get('textarea').should('have.attr', 'readonly', 'true');
    });
  });
});
