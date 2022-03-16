import user from '../../../fixtures/users.json';
import call from '../../../fixtures/api/call/1.step.call.json';
import application from '../../../fixtures/api/application/application.json';

context('Application form exports', () => {

  before(() => {
    cy.createCall(call as Call).then(callId => {
      call.generalCallSettings.id = callId;
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.applicantUser);
  });

  it('TB-366 Export application form using two sets of input and export language', function () {
    cy.fixture('application/application-form-export/TB-366').then(function (testData) {
      application.identification.intro = testData.intro;
      cy.createFullApplication(application as Application).then(applicationId => {
        application.id = applicationId;
        cy.runPreSubmissionCheck(applicationId);
        cy.submitProjectApplication(applicationId);

        cy.loginByRequest(user.programmeUser);
        cy.enterEligibilityAssessment(applicationId, testData.eligibilityAssessment);
        cy.enterQualityAssessment(applicationId, testData.qualityAssessment);
        cy.enterEligibilityDecision(applicationId, testData.eligibilityDecision);
        cy.enterFundingDecision(applicationId, testData.fundingDecision);

        cy.loginByRequest(user.applicantUser);
        cy.intercept(`api/project/${applicationId}/workPackage`).as('pageLoaded');
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.wait('@pageLoaded')

        cy.contains('Export').should('be.visible').click();
        cy.contains('div', 'Input language').find('mat-select').click();
        cy.contains('mat-option', 'Deutsch').click();

        cy.intercept(`api/project/${applicationId}/export/application?*`).as('downloadRequest');
        cy.contains('button', 'Export').click();
        cy.wait('@downloadRequest').then(result => {
          const regex = /filename="(.*\.pdf)"/;
          const fileNameMatch = regex.exec(result.response.headers['content-disposition'].toString());
          cy.readFile('./cypress/downloads/' + fileNameMatch[1]).then(file => {
            const assertionMessage = 'Downloaded file should contain application title';
            expect(file.includes(application.identification.acronym), assertionMessage).to.be.true;
          });
        });
      });
    });
  });
});
