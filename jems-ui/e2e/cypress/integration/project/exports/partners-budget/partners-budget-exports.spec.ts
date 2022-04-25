import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';

context('Partners budget exports', () => {
  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-369 Export partners budget using two sets of input and export language', () => {
    cy.fixture('project/exports/partners-budget/TB-369.json').then(testData => {
      cy.createCall(call, user.programmeUser.email).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId, user.programmeUser.email);
        application.identification.acronym = testData.acronym;
        application.details.acronym = testData.acronym;
        cy.createFullApplication(application, user.programmeUser.email).then(applicationId => {
          cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

          cy.contains('Partners budget').click();
          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?exportLanguage=EN&inputLanguage=DE`, 'csv').then(exportFile => {
            expect(exportFile.fileName).to.contain('tb-369_Budget_22');
            cy.fixture('project/exports/partners-budget/TB-369-export-en-de.csv').parseCSV().then(testDataFile => {
              const assertionMessage = 'Verify downloaded csv file';
              expect(exportFile.content.slice(1), assertionMessage).to.deep.equal(testDataFile);
            });
          });

          cy.contains('div', 'Export language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();
          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'English').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?exportLanguage=DE&inputLanguage=EN`, 'csv').then(exportFile => {
            expect(exportFile.fileName).to.contain('tb-369_Budget_22');
            cy.fixture('project/exports/partners-budget/TB-369-export-de-en.csv').parseCSV().then(testDataFile => {
              const assertionMessage = 'Verify downloaded csv file';
              expect(exportFile.content.slice(1), assertionMessage).to.deep.equal(testDataFile);
            });
          });
        });
      });
    });
  });
});
