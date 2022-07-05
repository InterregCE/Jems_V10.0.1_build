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

  beforeEach(() => {
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-519 A project can be set to contracted', () => {
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

      cy.wait(1000); // TODO remove after MP2-2391 is fixed
      
      cy.contains('Contract monitoring').click();
      cy.contains('Set project to contracted').click();
      cy.contains('button', 'Confirm').click();
      cy.get('jems-alert').should('contain', 'You have successfully contracted project');

      cy.contains('Project overview').click();
      cy.get('#status').contains('Contracted').should('be.visible');

      cy.contains('div', 'Partner reports').should(reportingSection => {
        expect(reportingSection).to.contain(application.partners[0].details.abbreviation);
      });
      cy.contains(application.partners[0].details.abbreviation).click();
      cy.contains('Add Partner Report').click();
      cy.contains('Partner progress report identification').should('be.visible');

      cy.loginByRequest(user.applicantUser.email);
      cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
      cy.contains('.link', 'A - Project identification').click();
      cy.contains('div', 'Project title').find('textarea').should('have.attr', 'readonly');
    });
  });
});
