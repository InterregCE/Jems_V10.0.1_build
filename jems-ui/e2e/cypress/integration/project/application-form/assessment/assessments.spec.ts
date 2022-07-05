import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import date from 'date-and-time';

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
      cy.createSubmittedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.wait(10000)
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
        const today = new Date();
        const formattedToday = date.format(today, 'MM/DD/YYYY');
        cy.contains('div', 'Decision date').find('input').type(formattedToday)
        cy.contains('Submit eligibility decision').click();
        cy.contains('Confirm').click();
        cy.contains('Eligibility decision:').next().should('contain.text', 'Eligible');
        
        cy.contains('Enter funding decision').click();
        cy.contains(testData.funding.decision).click();
        cy.contains('div', 'Explanatory notes').find('textarea').type(testData.funding.explanatoryNotes);
        cy.contains('div', 'Decision date').find('input').type(formattedToday)
        cy.contains('Submit funding decision').click();
        cy.contains('Confirm').click();
        cy.contains('Funding decision:').next().should('contain.text', 'Approved with conditions');
        cy.wait(1000); // TODO remove after MP2-2391 is fixed
        
        cy.contains('Project overview').click();
        cy.contains('Approved with conditions').should('be.visible');

        cy.contains('Assessment & Decision').click();
        cy.contains('Return to applicant').click();
        cy.contains('Confirm').click();
        cy.contains('Project application has been returned to applicant successfully.').scrollIntoView().should('be.visible');
        cy.wait(1000); // TODO remove after MP2-2391 is fixed
        
        cy.contains('Project overview').click();
        cy.contains('Returned for conditions').should('be.visible');
        cy.contains('(current) V. 2.0').should('be.visible');
      });
    });
  });
});
