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

  it('TB-1034 Make sure Pre-check works and that you can submit', function () {
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

  it('TB-1023 PR - Work plan progress', function () {
    cy.fixture('project/reporting/TB-1023.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email)
        .then(applicationId => {
          cy.loginByRequest(user.programmeUser.email);
          cy.getContractMonitoring(applicationId).then((contractMonitoring) => {
            contractMonitoring.startDate = date.format(new Date(), 'YYYY-MM-DD');
            cy.updateContractMonitoring(applicationId, contractMonitoring);
          });

          cy.loginByRequest(user.applicantUser.email);
          createProjectReportWithoutReportingSchedule(applicationId, 'Content');

          cy.url().then(url => {
            const reportId = Number(url.replace('/identification', '').split('/').pop());

            cy.getProjectReportWorkPlanProgress(applicationId, reportId).then((workPlanProgress) => {
              updateProjectReportWorkPlanProgress(testData.workPlansUpdate, workPlanProgress[0], applicationId, reportId);
            });

            cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/submitReport`, {failOnStatusCode: false});
            cy.contains('button', 'Run pre-submission check').should('be.enabled').click();
            cy.contains('0 Issue(s)').should('be.visible');
            cy.contains('button', 'Submit project report').should('be.enabled').click();

            cy.intercept(`/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/submit`).as('submitProjectReport')
            cy.contains('button', 'Confirm').click();
            cy.wait('@submitProjectReport');
          });

          createProjectReportWithoutReportingSchedule(applicationId, 'Both');

          cy.url().then(url => {
            const reportId = Number(url.replace('/identification', '').split('/').pop());
            cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/workPlan`, {failOnStatusCode: false});
            cy.contains('Completed in prior report. No changes.').should('be.visible');

            cy.contains('Work package 1').click();
            cy.contains('mat-checkbox', 'This work package is completed.').click();

            cy.contains('mat-form-field', 'Status').click();
            cy.contains('mat-option', 'Not achieved').click();
            cy.contains('mat-form-field', 'Explanations').click().type('Updated objective');

            cy.contains('mat-form-field', 'Partly achieved').scrollIntoView().click();
            cy.contains('mat-option', 'Not achieved').click();

            cy.contains('mat-form-field', 'Enter text here').click().type('Updated progress');

            cy.contains('mat-expansion-panel', 'I 1.1').click();
            cy.contains('mat-form-field', 'Please describe the progress of investment in this reporting period').click().type('Updated investment');

            cy.contains('mat-expansion-panel', 'A 1.1').click();
            cy.contains('Completed in prior report. No changes.').should('be.visible');
            cy.contains('mat-form-field', 'Status').click();
            cy.contains('mat-option', 'Not achieved').click();
            cy.contains('mat-form-field', 'Describe how you contributed to the progress made in this activity').click().type('Updated activity');

            cy.contains('div', 'D 1.1.1').parent().contains('mat-form-field', 'Achieved in this reporting period').click().type('80');
            cy.contains('mat-form-field', 'Cumulative value').find('input').should('have.value', formatAmount('130'));
            cy.contains('mat-form-field', 'Progress in this report').click().type('Updated deliverable');
            cy.contains('mat-panel-title', 'A 1.1').click();

            cy.contains('mat-expansion-panel', 'O 1.1').scrollIntoView().click();
            cy.contains('div', 'O 1.1').parent().contains('mat-form-field', 'Achieved in this reporting period').click().type('25');
            cy.contains('div', 'O 1.1').parent().contains('mat-form-field', 'Cumulative value').find('input').should('have.value', formatAmount('105'));
            cy.contains('mat-form-field', 'Progress in this period').click().type('Updated output');

            cy.contains('Save changes').click();
            cy.wait(2000);
            cy.contains('Completed in prior report. No changes.').should('not.exist');

            cy.contains('Work package 1').click();
            cy.contains('mat-expansion-panel', 'A 1.1').click();
            cy.contains('New changes after completion.').should('be.visible');

            cy.contains('span', 'Attachment:').next().click();
            cy.get('input[type=file]').eq(0).selectFile('cypress/fixtures/project/reporting/fileToUpload.txt', {force: true});
            cy.contains('fileToUpload.txt').should('be.visible');
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

function createProjectReportWithoutReportingSchedule(applicationId: number, reportType: string) {
  cy.visit(`/app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
  cy.contains('Add Project Report').click();

  cy.contains('jems-project-periods-select', 'Reporting period').click();
  cy.contains('mat-option span', `Period 1`).click();
  cy.contains('button', reportType).should('be.enabled').click();

  cy.contains('div', 'Reporting date').next().click();
  cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();

  cy.contains('button', 'Create').should('be.enabled').click();
  cy.wait(2000);
}

function assertProjectReportData(partnerDetails: any, afVersion: string) {
  cy.contains('span', 'Name of the organisation in original language').next().should('contain', partnerDetails.nameInOriginalLanguage);
  cy.contains('span', 'Name of the organisation in english').next().should('contain', partnerDetails.nameInEnglish);
  cy.contains('span', 'AF Version linked').next().should('contain', afVersion);
}

function updateProjectReportWorkPlanProgress(workPlansUpdate: any, existingData: any, applicationId: number, reportId: number) {
  workPlansUpdate[0].id = reportId;
  workPlansUpdate[0].activities[0].id = existingData.activities[0].id;
  workPlansUpdate[0].activities[0].deliverables[0].id = existingData.activities[0].deliverables[0].id;
  workPlansUpdate[0].activities[0].deliverables[1].id = existingData.activities[0].deliverables[1].id;

  workPlansUpdate[0].activities[1].id = existingData.activities[1].id;
  workPlansUpdate[0].activities[1].deliverables[0].id = existingData.activities[1].deliverables[0].id;
  workPlansUpdate[0].activities[1].deliverables[1].id = existingData.activities[1].deliverables[1].id;

  workPlansUpdate[0].investments[0].id = existingData.investments[0].id;
  workPlansUpdate[0].investments[1].id = existingData.investments[1].id;
  workPlansUpdate[0].investments[2].id = existingData.investments[2].id;

  workPlansUpdate[0].outputs[0].id = existingData.outputs[0].id;
  workPlansUpdate[0].outputs[1].id = existingData.outputs[1].id;

  cy.updateProjectReportWorkPlanProgress(applicationId, reportId, workPlansUpdate);
}

function formatAmount(amount) {
  return new Intl.NumberFormat('de-DE', {minimumFractionDigits: 2}).format(amount);
}
