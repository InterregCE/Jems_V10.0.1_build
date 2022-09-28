import user from '../../../../fixtures/users.json';
import application from '../../../../fixtures/api/application/application.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import {faker} from '@faker-js/faker';

context('Application contracting tests', () => {
  it('TB-732 User can upload files to application annexes', () => {
    cy.loginByRequest(user.admin.email);
    cy.fixture('project/application-form/application-annexes/TB-732.json').then(testData => {
      testData.applicationCreator.email = faker.internet.email();
      testData.applicationCollaborator.email = faker.internet.email();
      cy.createUser(testData.applicationCreator);
      cy.createUser(testData.applicationCollaborator);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      })
      cy.loginByRequest(testData.applicationCreator.email);
      cy.createApplication(application).then(applicationId => {
        testData.partner.abbreviation = faker.word.noun();
        cy.createPartner(applicationId, testData.partner).then(() => {
          testData.partner.abbreviation = faker.word.noun();
          cy.createPartner(applicationId, testData.partner);

          cy.loginByRequest(user.admin.email);
          cy.visit(`/app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});
          cy.get('mat-expansion-panel#collaborators-panel').within(() => {
            cy.contains('button', '+').click();
            cy.get('div.mat-form-field-flex').last().type(testData.applicationCollaborator.email);
          })
          cy.contains('button', 'Save changes').click();

          cy.loginByRequest(testData.applicationCreator.email);
          cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
          cy.contains('Application annexes').click();
          cy.contains('Application attachments').click();
          cy.get('input[type=file]').selectFile('cypress/fixtures/project/application-form/application-annexes/application-attachment.txt', {force: true});
          cy.contains("button", "PP1").click();
          cy.get('input[type=file]').selectFile('cypress/fixtures/project/application-form/application-annexes/partner-upload.txt', {force: true});

          cy.contains('Application attachments').click();

          cy.get('mat-table').within(() => {
            cy.contains('application-attachment').should('be.visible');
            cy.contains('partner-upload').should('be.visible');
            cy.contains('mat-row', 'application-attachment').contains('mat-icon', 'download').should('be.visible');
            cy.contains('mat-row', 'application-attachment').contains('mat-icon', 'delete').should('be.visible');
            cy.contains('mat-row', 'application-attachment').contains('mat-icon', 'edit').scrollIntoView().should('be.visible');
            cy.contains('mat-row', 'partner-upload').contains('mat-icon', 'download').scrollIntoView().should('be.visible');
            cy.contains('mat-row', 'partner-upload').contains('mat-icon', 'delete').should('be.visible');
            cy.contains('mat-row', 'partner-upload').contains('mat-icon', 'edit').should('be.visible');
          })

          cy.contains('button', 'PP2').click();
          cy.contains('div', 'There are no files uploaded.').should('be.visible');

          cy.contains('button', 'Application attachments').click();
          cy.get('mat-table').within(()=>{
            cy.contains('mat-row', 'application-attachment').contains('mat-icon', 'edit').click();
            const testInput = faker.word.noun();
            cy.contains('div.mat-form-field-flex', 'Description').find('textarea').type(testInput);
            cy.contains('button', 'Save').click();
            cy.contains(testInput).should('be.visible');

            cy.contains('mat-row', 'partner-upload').contains('mat-icon', 'delete').click();
          })
          cy.contains('button', 'Confirm').click();
          cy.root().contains('div', 'File was deleted successfully.').should('be.visible');
          cy.contains('partner-upload').should('not.exist');

          cy.loginByRequest(testData.applicationCollaborator.email);
          cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
          cy.contains('Application annexes').click();
          cy.get('mat-table').within(() => {
            cy.contains('mat-ivon', 'delete').should('not.exist');
            cy.contains('mat-icon', 'download').should('be.visible');
            cy.contains('mat-icon', 'download').clickToDownload(`api/project/${applicationId}/file/download/?*`, 'txt').then(returnValue => {
              cy.wrap(returnValue.fileName === 'application-attachment.txt').as('assertion');
              cy.get('@assertion').should('eq', true);
            });
          })
        });
      })
    });
  })
});
