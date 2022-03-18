import user from '../../../fixtures/users.json';
import call from '../../../fixtures/api/call/1.step.call.json';
import application from '../../../fixtures/api/application/application.json';
import testData from '../../../fixtures/application/application-form-export/TB-366.json';

context('Application form exports', () => {

  before(() => {
    cy.createCall(call).then(callId => {
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
    application.identification.intro = testData.intro;
    application.identification.acronym = testData.acronym;
    application.details.acronym = testData.acronym;
    cy.createFullApplication(application, user.applicantUser.email).then(applicationId => {
      cy.runPreSubmissionCheck(applicationId);
      cy.submitProjectApplication(applicationId);

      cy.loginByRequest(user.programmeUser);
      cy.enterEligibilityAssessment(applicationId, testData.eligibilityAssessment);
      cy.enterQualityAssessment(applicationId, testData.qualityAssessment);
      cy.enterEligibilityDecision(applicationId, testData.eligibilityDecision);
      cy.enterFundingDecision(applicationId, testData.fundingDecision);

      cy.loginByRequest(user.applicantUser);
      cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

      cy.intercept(`api/project/${applicationId}/workPackage`).as('pageLoaded');
      cy.contains('Export').should('be.visible').click();
      cy.wait('@pageLoaded');

      cy.contains('div', 'Input language').find('mat-select').click();
      cy.contains('mat-option', 'Deutsch').click();

      cy.intercept(`api/project/${applicationId}/export/application?exportLanguage=EN&inputLanguage=DE`).as('downloadRequest');
      cy.contains('button', 'Export').click();
      cy.wait('@downloadRequest').then(result => {
        const regex = /filename="(.*\.pdf)"/;
        const fileName = regex.exec(result.response.headers['content-disposition'].toString())[1];
        expect(fileName).to.contain('tb-366_en_de_2022');
        cy.wait(2000); // TODO needed because of cypress issue, github-20683
        cy.readFile('./cypress/downloads/' + fileName, null).parsePDF().then(file => {
          const assertionMessage = 'Verify downloaded pdf file';
          expect(file.text.includes(testData.exportedApplicationDataDE), assertionMessage).to.be.true;
        });
      });

      cy.contains('div', 'Export language').find('mat-select').click();
      cy.contains('mat-option', 'Deutsch').click();
      cy.contains('div', 'Input language').find('mat-select').click();
      cy.contains('mat-option', 'English').click();

      cy.intercept(`api/project/${applicationId}/export/application?exportLanguage=DE&inputLanguage=EN`).as('downloadRequest');
      cy.contains('button', 'Export').click();
      cy.wait('@downloadRequest').then(result => {
        const regex = /filename="(.*\.pdf)"/;
        const fileName = regex.exec(result.response.headers['content-disposition'].toString())[1];
        expect(fileName).to.contain('tb-366_de_en_2022');
        cy.wait(2000); // TODO needed because of cypress issue, github-20683
        cy.readFile('./cypress/downloads/' + fileName, null).parsePDF().then(file => {
          const assertionMessage = 'Verify downloaded pdf file';
          expect(file.text.includes(testData.exportedApplicationDataEN), assertionMessage).to.be.true;
        });
      });
    });
  });
});
