import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import date from 'date-and-time';

context('Application modifications', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-359 Open new modification and approve it', () => {
    cy.fixture('project/application-form/modifications/TB-359.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createFullApplication(application, user.programmeUser.email).then(applicationId => {
        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false});
        cy.contains('Open new modification').click();
        cy.get('jems-confirm-dialog').should('be.visible');
        cy.get('jems-confirm-dialog').find('.mat-dialog-actions').contains('Confirm').click();
        cy.contains('You have successfully opened a modification').should('be.visible');

        cy.loginByRequest(user.applicantUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.get('jems-project-application-information').find('div').should('contain.text', 'In modification precontracted');
        cy.get('jems-side-nav').find('mat-select-trigger').find('span').should('contain.text', ' (current) V. 2.0');
        cy.runPreSubmissionCheck(applicationId);
        cy.submitProjectApplication(applicationId);

        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.get('jems-project-application-information').find('div').should('contain.text', 'Modification precontracted submitted');
        cy.contains('span', 'Modification').click();
        cy.contains('Approve modification').click();
        cy.get('jems-modification-confirmation').contains('div', 'Decision date (MM/DD/YYYY)').find('input').type(testData.approved.entryIntoForceDate);
        cy.get('jems-modification-confirmation').contains('div', 'Entry into force (MM/DD/YYYY)').find('input').type(testData.approved.entryIntoForceDate);
        cy.get('jems-modification-confirmation').contains('div', 'Explanatory notes').find('textarea').type(testData.approved.note);
        cy.get('jems-modification-confirmation').contains('Save changes').click();

        cy.contains('Past modifications').next().should(pastModificationsSection => {
          expect(pastModificationsSection).to.contain('Approved');
          expect(pastModificationsSection).to.contain('Modification 1');
          expect(pastModificationsSection).to.contain('Project Version 2.0');
        });

        cy.contains('Project overview').click();
        cy.get('jems-project-application-information').find('div').should('contain.text', 'Approved');
      });
    });
  });

  it('TB-357 Reject modification', () => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createFullApplication(application, user.programmeUser.email).then(applicationId => {
      cy.startModification(applicationId, user.programmeUser.email);
      cy.runPreSubmissionCheck(applicationId);
      cy.submitProjectApplication(applicationId);

      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false});

      const today = new Date();
      const formattedToday = date.format(today, 'MM/DD/YYYY');

      cy.contains('Reject modification').click();
      cy.contains('div', 'Decision date').find('input').type(formattedToday);
      cy.contains('div', 'Entry into force').find('input').type(formattedToday);
      cy.contains('div', 'Explanatory notes').find('textarea').type('Random note text');

      cy.contains('Save changes').click();

      cy.contains('Past modifications').next().should(pastModificationsSection => {
        expect(pastModificationsSection).to.contain('Rejected');
        expect(pastModificationsSection).to.contain('Modification 1');
        expect(pastModificationsSection).to.contain('Project Version 2.0');
      });

      cy.contains('div', 'Project version').should('contain', '(current) V. 1.0');
    });
  });
});
