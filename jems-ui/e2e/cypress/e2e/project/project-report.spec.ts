import user from '../../fixtures/users.json';
import application from '../../fixtures/api/application/application.json';
import approvalInfo from '../../fixtures/api/application/modification/approval.info.json';
import rejectionInfo from '../../fixtures/api/application/modification/rejection.info.json';
import call from '../../fixtures/api/call/1.step.call.json';
import {faker} from '@faker-js/faker';
import date from 'date-and-time';

context('Project report tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-1035 Data is taken from correct AF form version', function () {

    cy.loginByRequest(user.applicantUser.email);
    cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {

      const originalPartnerDetails = application.partners[0].details;
      const partnerId = this[originalPartnerDetails.abbreviation];

      // Set-up
      cy.loginByRequest(user.programmeUser.email)
      cy.getContractMonitoring(applicationId).then((contractMonitoring) => {
        contractMonitoring.startDate = date.format(new Date(), 'YYYY-MM-DD');
        cy.updateContractMonitoring(applicationId, contractMonitoring);
      })

      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`/app/project/detail/${applicationId}/contractReporting`, {failOnStatusCode: false});

      addReportingPeriod(0);
      addReportingPeriod(1);
      addReportingPeriod(2);

     cy.contains('Save changes').should('be.enabled').click().wait(1000);

      // 1
      cy.loginByRequest(user.applicantUser.email);
      cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
      cy.contains('li span', 'Project reports').click();

      // 2
      createProjectReport(1);
      assertProjectReportData(originalPartnerDetails, '1.0');

      // 3
      const updatedPartnerDetails = {
        ...originalPartnerDetails,
        nameInOriginalLanguage: `${faker.word.adverb()}, ${faker.word.noun()}`,
        nameInEnglish: `${faker.word.adverb()}, ${faker.word.noun()}`,
      };

      cy.startModification(applicationId, user.programmeUser.email);
      cy.updatePartnerIdentity(partnerId, updatedPartnerDetails);
      cy.runPreSubmissionCheck(applicationId);
      cy.submitProjectApplication(applicationId);
      cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

      // 4
      cy.visit(`/app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
      cy.contains('mat-row', 'PR.1').click();
      assertProjectReportData(originalPartnerDetails, '1.0');

      // 5
      cy.visit(`/app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
      createProjectReport(2);
      assertProjectReportData(updatedPartnerDetails, '2.0');

      // 6.
      const rejectedPartnerDetails = {
        ...originalPartnerDetails,
        nameInOriginalLanguage: `${faker.word.adverb()}, ${faker.word.noun()}`,
        nameInEnglish: `${faker.word.adverb()}, ${faker.word.noun()}`,
      };

      cy.startModification(applicationId, user.programmeUser.email);
      cy.updatePartnerIdentity(partnerId, rejectedPartnerDetails);
      cy.runPreSubmissionCheck(applicationId);
      cy.submitProjectApplication(applicationId);
      cy.rejectModification(applicationId, rejectionInfo, user.programmeUser.email);

      // 7
      cy.visit(`/app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
      createProjectReport(3);
      assertProjectReportData(updatedPartnerDetails, '2.0');
    });
  });

  it.only('TB-1034 Make sure Pre-check works and that you can submit', function () {
    cy.fixture('project/reporting/TB-1034.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.loginByRequest(user.programmeUser.email);
        const yesterday = new Date((new Date()).valueOf() - (1000 * 60 * 60 * 24));

        cy.getContractMonitoring(applicationId).then((contractMonitoring) => {
          contractMonitoring.startDate = date.format(yesterday, 'YYYY-MM-DD');
          cy.updateContractMonitoring(applicationId, contractMonitoring);
        });

        testData.deadlines[0].date = yesterday;
        testData.deadlines[1].date = new Date();
        cy.createReportingDeadlines(applicationId, testData.deadlines);

        cy.loginByRequest(user.applicantUser.email);

        cy.visit(`app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
        cy.contains('Add Project Report').click();

        cy.contains('Link to reporting schedule').click();
        cy.contains('mat-option span', `1, Period 1`).click();
        cy.contains('button', 'Create').should('be.enabled').click();
        cy.wait(2000);

        cy.url().then(url => {
          const reportId = url.replace('/identification','').split('/').pop();
          cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/submitReport`, {failOnStatusCode: false});
          cy.contains('button', 'Run pre-submission check').should('be.enabled').click();
          cy.contains('button', 'Submit project report').should('be.disabled');
          cy.contains('1 Issue(s)').should('be.visible');
          cy.contains('mat-expansion-panel-header', 'Project report identification').should('be.visible').click();
          cy.contains('The Date of the reporting schedule expired').should('be.visible');

          cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/identification`, {failOnStatusCode: false});
          cy.contains('Link to reporting schedule').parent().prev().click();
          cy.contains('mat-option span', `2, Period 1`).click();
          cy.contains('button', 'Save changes').should('be.enabled').click();

          cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/submitReport`, {failOnStatusCode: false});
          cy.contains('button', 'Run pre-submission check').should('be.enabled').click();
          cy.contains('0 Issue(s)').should('be.visible');
          cy.contains('button', 'Submit project report').should('be.enabled').click();

          cy.intercept(`/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/submit`).as('submitProjectReport')
          cy.contains('button', 'Confirm').click();
          cy.wait('@submitProjectReport')

          cy.visit(`app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
          cy.contains('mat-row', 'PR.1').should('be.visible');
          cy.contains('mat-row', 'Submitted').should('be.visible');
        });
      });
    });
  });
});

function addReportingPeriod(number: number) {
  cy.contains('span', 'Add Reporting deadline').click();
  cy.get('mat-table').scrollIntoView();

  const type = number == 0 ? 'Only Finance'
    : number == 1 ? 'Only Content' : 'Both';

  cy.get('mat-table mat-row').eq(number).within(() => {
    cy.contains('span', type).click();

    cy.get('.mat-select-placeholder').eq(0).click();
    cy.root().closest('body').find('mat-option').contains(`Period ${number + 1}`).click();

    cy.contains('div', 'Date').scrollIntoView().next().click();
    cy.root().closest('body').find('table.mat-calendar-table').find('tr').last().find('td').last().click();
  });
}

function createProjectReport(forPeriod: number) {
  cy.contains('Add Project Report').click();

  cy.contains('div', 'Reporting period start date (').next().click();
  cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();

  cy.contains('div', 'Reporting period end date (').next().click();
  cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();

  cy.contains('Link to reporting schedule').click();
  cy.contains('mat-option span', `${forPeriod}, Period ${forPeriod}`).click();

  cy.contains('button', 'Create').should('be.enabled').click();
}

function assertProjectReportData(partnerDetails: any, afVersion: string) {
  cy.contains('span', 'Name of the organisation in original language').next().should('contain', partnerDetails.nameInOriginalLanguage);
  cy.contains('span', 'Name of the organisation in english').next().should('contain', partnerDetails.nameInEnglish);
  cy.contains('span', 'AF Version linked').next().should('contain', afVersion);
}
