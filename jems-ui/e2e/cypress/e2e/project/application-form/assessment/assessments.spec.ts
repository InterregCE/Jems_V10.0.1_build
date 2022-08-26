import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import assessmentChecklist from '../../../../fixtures/api/checklist/assessmentChecklist.json';

context('Assessments & decision tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-360 Approve project with conditions and return to applicant', () => {
    cy.fixture('project/application-form/assessments/TB-360.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createSubmittedApplication(application).then(applicationId => {
        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.contains('Assessment & Decision').click();

        cy.contains('Enter eligibility assessment').click();
        cy.contains(testData.eligibility.assessment).click();
        cy.contains('div', 'Explanatory notes').find('textarea').type(testData.eligibility.explanatoryNotes);
        cy.contains('Submit eligibility assessment').click();
        cy.contains('Confirm').click();
        cy.contains('Eligibility check:').next().should('contain.text', 'Passed');

        cy.contains('Enter quality assessment').click();
        cy.contains(testData.quality.assessment).click();
        cy.contains('div', 'Explanatory notes').find('textarea').type(testData.quality.explanatoryNotes);
        cy.contains('Submit quality assessment').click();
        cy.contains('Confirm').click();
        cy.contains('Quality check:').next().should('contain.text', 'Recommended for funding');

        cy.contains('Enter eligibility decision').click();
        cy.contains(testData.eligibility.decision).click();
        cy.contains('div', 'Explanatory notes').find('textarea').type(testData.eligibility.explanatoryNotes);
        cy.contains('mat-form-field', 'Decision date').find('button').click();
        cy.get('.mat-calendar-body-today').click();
        cy.contains('Submit eligibility decision').click();
        cy.contains('Confirm').click();
        cy.contains('Eligibility decision:').next().should('contain.text', 'Eligible');

        cy.contains('Enter funding decision').click();
        cy.contains(testData.funding.decision).click();
        cy.contains('div', 'Explanatory notes').find('textarea').type(testData.funding.explanatoryNotes);
        cy.contains('mat-form-field', 'Decision date').find('button').click();
        cy.get('.mat-calendar-body-today').click();
        cy.contains('Submit funding decision').click();
        cy.contains('Confirm').click();
        cy.contains('Funding decision:').next().should('contain.text', 'Approved with conditions');
        cy.wait(1000);

        cy.contains('Project overview').click();
        cy.contains('Approved with conditions').should('be.visible');

        cy.contains('Assessment & Decision').click();
        cy.contains('Return to applicant').click();
        cy.contains('Confirm').click();
        cy.contains('Project application has been returned to applicant successfully.').scrollIntoView().should('be.visible');
        cy.wait(1000);

        cy.contains('Project overview').click();
        cy.contains('Returned for conditions').should('be.visible');
        cy.contains('(current) V. 2.0').should('be.visible');
      });
    });
  });

  it('TB-361 Revert decision to submitted', () => {
    cy.fixture('project/application-form/assessments/TB-361.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createSubmittedApplication(application).then(applicationId => {
        cy.loginByRequest(user.programmeUser.email);
        cy.enterEligibilityAssessment(applicationId, application.assessments.eligibilityAssessment);
        cy.enterQualityAssessment(applicationId, application.assessments.qualityAssessment);
        cy.enterEligibilityDecision(applicationId, application.assessments.eligibilityDecision);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.contains('Assessment & Decision').click();
        cy.contains('Enter eligibility decision').should('not.exist');
        cy.contains('Revert decision back to Submitted').click();
        cy.contains('Confirm').click();
        cy.contains('Enter eligibility decision').should('be.visible');
        cy.wait(1000);

        cy.contains('Project overview').click();
        cy.contains('Submitted').should('be.visible');
        cy.wait(1000);

        cy.contains('Assessment & Decision').click();

        cy.contains('Enter eligibility decision').click();
        cy.contains(testData.eligibility.decision).click();
        cy.contains('div', 'Explanatory notes').find('textarea').type(testData.eligibility.explanatoryNotes);
        cy.contains('mat-form-field', 'Decision date').find('button').click();
        cy.get('.mat-calendar-body-today').click();
        cy.contains('Submit eligibility decision').click();
        cy.contains('Confirm').click();
        cy.contains('Eligibility decision:').next().should('contain.text', 'Ineligible');
        cy.wait(1000);

        cy.contains('Project overview').click();
        cy.contains('Ineligible').should('be.visible');
        cy.wait(1000);

        cy.contains('Assessment & Decision').click();
        cy.contains('Revert decision back to Submitted').click();
        cy.contains('Confirm').click();
        cy.contains('Enter eligibility decision').should('be.visible');
        cy.contains('Return to applicant').should('be.visible');
        cy.contains('Reverting the decision is not possible').should('be.visible');
        cy.wait(1000);

        cy.contains('Project overview').click();
        cy.contains('Submitted').should('be.visible');
      });
    });
  });

  it('TB-580 User can instantiate, fill out, delete and submit assessments', () => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createSubmittedApplication(application).then(applicationId => {
      cy.loginByRequest(user.programmeUser.email);
      cy.createChecklist(assessmentChecklist)
      cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
      cy.contains('Assessment & Decision').click();
      cy.contains('mat-select','Select checklist template').click();
      cy.get('mat-option:first-of-type').click();
      cy.contains('instantiate new assessment').click();
      cy.contains('Draft').should('be.visible');
      cy.contains('button','Finish checklist').should('exist');
      cy.contains('button','Yes?').click();
      cy.contains('button','Save changes').click();
      cy.contains('Checklist instance saved successfully').should('be.visible');
      cy.contains('Assessment & Decision').click();
      cy.get('jems-table').should('exist');
      cy.contains('mat-cell','Finished').should('not.exist');
      cy.contains('button','delete').click();
      cy.contains('Confirm').click();
      cy.get('jems-table').should('not.exist');
      cy.contains('mat-select','Select checklist template').click();
      cy.get('mat-option:first-of-type').click();
      cy.contains('instantiate new assessment').click();
      cy.contains('button','Yes?').click();
      cy.contains('button','Save changes').click();
      cy.contains('button','Finish checklist').click();
      cy.contains('Confirm').click();
      cy.contains('Assessment checklists').should('exist');
      cy.contains('mat-cell','Finished').should('exist');
      cy.contains('button','delete').should('not.exist');
    });
  });
});
