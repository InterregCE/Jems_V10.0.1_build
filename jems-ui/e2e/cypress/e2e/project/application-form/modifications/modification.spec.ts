import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';

context('Application modification tests', () => {

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
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
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
        cy.contains('mat-form-field', 'Decision date').find('button').click();
        cy.get('.mat-calendar-body-today').click();
        cy.contains('mat-form-field', 'Entry into force').find('button').click();
        cy.get('.mat-calendar-body-today').click();
        cy.get('jems-modification-confirmation').contains('div', 'Explanatory notes').find('textarea').type(testData.approved.note);
        cy.get('jems-modification-confirmation').contains('Save changes').click();
        
        cy.wait(1000); // TODO remove after MP2-2391 is fixed

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
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.startModification(applicationId, user.programmeUser.email);
      cy.runPreSubmissionCheck(applicationId);
      cy.submitProjectApplication(applicationId);

      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false});

      cy.contains('Reject modification').click();
      cy.contains('mat-form-field', 'Decision date').find('button').click();
      cy.get('.mat-calendar-body-today').click();
      cy.contains('mat-form-field', 'Entry into force').find('button').click();
      cy.get('.mat-calendar-body-today').click();
      cy.contains('div', 'Explanatory notes').find('textarea').type('Random note text');

      cy.contains('Save changes').click();

      cy.wait(1000); // TODO remove after MP2-2391 is fixed

      cy.contains('Past modifications').next().should(pastModificationsSection => {
        expect(pastModificationsSection).to.contain('Rejected');
        expect(pastModificationsSection).to.contain('Modification 1');
        expect(pastModificationsSection).to.contain('Project Version 2.0');
      });

      cy.contains('div', 'Project version').should('contain', '(current) V. 1.0');
    });
  });

  it('TB-356 Edit project in modification and resubmit', () => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.startModification(applicationId, user.programmeUser.email);

      cy.loginByRequest(user.applicantUser.email);
      cy.visit(`app/project/detail/${applicationId}/applicationFormIdentification`, {failOnStatusCode: false});
      cy.contains('div', 'Project title').find('textarea').clear().type('New title');
      cy.contains('Save changes').click();
      cy.contains('Project identification saved successfully.').should('be.visible');
      
      cy.runPreSubmissionCheck(applicationId);
      cy.submitProjectApplication(applicationId);
      cy.reload();

      cy.contains('Project overview').click();
      cy.get('jems-project-application-information').find('div').should('contain.text', 'Modification precontracted submitted');

      cy.contains('.link', 'A - Project identification').click();
      cy.wait(1000); // TODO remove after MP2-2391 is fixed
      cy.get('textarea').should('have.value', 'New title');

      cy.contains('(current) V. 2.0').click();
      cy.wait(1000); // TODO remove after MP2-2391 is fixed
      cy.contains('V. 1.0').should('be.visible').click();
      cy.contains('You are currently viewing an old version of this application').should('be.visible');
      cy.get('textarea').should('have.value', 'API generated application title DE');
    });
  });
});
