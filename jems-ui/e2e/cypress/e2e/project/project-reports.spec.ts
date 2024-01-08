import user from '@fixtures/users.json';
import application from '@fixtures/api/application/application.json';
import approvalInfo from '@fixtures/api/application/modification/approval.info.json';
import rejectionInfo from '@fixtures/api/application/modification/rejection.info.json';
import call from '@fixtures/api/call/1.step.call.json';
import {faker} from '@faker-js/faker';
import partnerReportExpenditures from "@fixtures/api/partnerReport/partnerReportExpenditures.json";
import partnerParkedExpenditures from "@fixtures/api/partnerReport/partnerParkedExpenditures.json";
import partnerReportIdentification from "@fixtures/api/partnerReport/partnerReportIdentification.json";
import controlReportIdentification from "@fixtures/api/partnerControlReport/controlReportIdentification.json";
import paymentsUser from "@fixtures/api/users/paymentsUser.json";
import paymentsRole from "@fixtures/api/roles/paymentsRole.json";
import {projectReportPage} from "./reports-page.pom";
import {ProjectReportType} from "./ProjectReportType";


context('Project report tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
    cy.loginByRequest(user.admin.email);
    paymentsUser.email = faker.internet.email();
    cy.createRole(paymentsRole).then(roleId => {
      paymentsUser.userRoleId = roleId;
      cy.createUser(paymentsUser);
    });
  });

  it('TB-1035 Data is taken from correct AF form version', function () {

    cy.loginByRequest(user.applicantUser.email);
    cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {

      const originalPartnerDetails = application.partners[0].details;
      const partnerId = this[originalPartnerDetails.abbreviation];

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

  it('TB-1034 PR - Pre-submission check should show errors if the deadline has expired', function () {
    cy.fixture('project/reporting/TB-1034.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.loginByRequest(user.programmeUser.email);
        cy.createReportingDeadlines(applicationId, testData.deadlines).then(deadlines => {
          testData.projectReport.deadlineId = deadlines[0].id;
          cy.loginByRequest(user.applicantUser.email);
          cy.createProjectReport(applicationId, testData.projectReport).then(reportId => {
            cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/submitReport`, {failOnStatusCode: false});
            cy.contains('button', 'Run pre-submission check').should('be.enabled').click();
            cy.contains('1 Issue(s)').should('be.visible');
            cy.contains('button', 'Submit project report').should('be.disabled');
            cy.contains('mat-expansion-panel-header', 'Project report identification').should('be.visible').click();
            cy.contains('The Date of the reporting schedule expired').should('be.visible');

            cy.loginByRequest(user.programmeUser.email);
            testData.deadlines[0].id = deadlines[0].id;
            testData.deadlines[0].date = new Date();
            cy.updateReportingDeadlines(applicationId, testData.deadlines);

            cy.loginByRequest(user.applicantUser.email);
            cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/submitReport`, {failOnStatusCode: false});
            cy.contains('button', 'Run pre-submission check').should('be.enabled').click();
            cy.contains('0 Issue(s)').should('be.visible');
            cy.contains('button', 'Submit project report').should('be.enabled').click();

            cy.intercept(`/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/submit`).as('submitProjectReport');
            cy.contains('button', 'Confirm').click();
            cy.wait('@submitProjectReport');

            cy.visit(`app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
            cy.contains('mat-row', 'PR.1').should('be.visible');
            cy.contains('mat-row', 'Submitted').should('be.visible');
          });
        });
      });
    });
  });

  it('TB-1023 PR - Work plan progress', function () {
    cy.fixture('project/reporting/TB-1023.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      application.reportingDeadlines = [];
      cy.createContractedApplication(application, user.programmeUser.email)
        .then(applicationId => {
          createProjectReportWithoutReportingSchedule(applicationId, ProjectReportType.Content);

          cy.url().then(url => {
            const reportId = Number(url.replace('/identification', '').split('/').pop());

            cy.getProjectReportWorkPlanProgress(applicationId, reportId).then((workPlanProgress) => {
              updateProjectReportWorkPlanProgress(testData.workPlansUpdate, workPlanProgress[0], applicationId, reportId);
            });

            cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/submitReport`, {failOnStatusCode: false});
            cy.contains('button', 'Run pre-submission check').should('be.enabled').click();
            cy.contains('0 Issue(s)').should('be.visible');
            cy.contains('button', 'Submit project report').should('be.enabled').click();

            cy.intercept(`/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/submit`).as('submitProjectReport');
            cy.contains('button', 'Confirm').click();
            cy.wait('@submitProjectReport');
          });

          createProjectReportWithoutReportingSchedule(applicationId, ProjectReportType.Both);

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

  it('TB-1093 Regular payments are properly reflected in following project reports', function () {
    cy.fixture('project/reporting/TB-1093.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      application.reportingDeadlines = [];
      application.contractMonitoring.fastTrackLumpSums[0].readyForPayment = false;
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
        const partnerId1 = this[application.partners[0].details.abbreviation];

        // add partner report
        cy.addPartnerReport(partnerId1)
          .then(reportId => {
            cy.updatePartnerReportIdentification(partnerId1, reportId, partnerReportIdentification);
            cy.updatePartnerReportExpenditures(partnerId1, reportId, partnerReportExpenditures)
              .then(response => {
                for (let i = 0; i < response.length; i++) {
                  partnerParkedExpenditures[i].id = response[i].id;
                }
              });
            cy.runPreSubmissionPartnerReportCheck(partnerId1, reportId);
            cy.runPreSubmissionPartnerReportCheck(partnerId1, reportId);
            cy.submitPartnerReport(partnerId1, reportId);

            performControlWorkAndFinalize(reportId, partnerId1);
          });

        // first project report
        cy.loginByRequest(user.applicantUser.email);
        createProjectReportWithoutReportingSchedule(applicationId, ProjectReportType.Finance);
        cy.url().then(url => {
          const reportId = Number(url.replace('/identification', '').split('/').pop());
          cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/financialOverview`, {failOnStatusCode: false});
          projectReportPage.verifyAmountsInTables(testData.expectedResultsR1);
          cy.submitProjectReport(applicationId, reportId);
          cy.loginByRequest(user.verificationUser.email);
          cy.startProjectReportVerification(applicationId, reportId);
          cy.finalizeProjectReportVerification(applicationId, reportId);
        });

        // second project report
        cy.loginByRequest(user.applicantUser.email);
        createProjectReportWithoutReportingSchedule(applicationId, ProjectReportType.Finance);
        cy.url().then(url => {
          const reportId = Number(url.replace('/identification', '').split('/').pop());
          cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/financialOverview`, {failOnStatusCode: false});
          projectReportPage.verifyAmountsInTables(testData.expectedResultsR2);
          cy.submitProjectReport(applicationId, reportId);
          cy.loginByRequest(user.verificationUser.email);
          cy.startProjectReportVerification(applicationId, reportId);
          cy.finalizeProjectReportVerification(applicationId, reportId);
        });

        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
        setReadyForPayment(true, 1);

        // check payments are created for FTLS
        cy.contains('Payments').click();
        cy.findProjectPayments(applicationId).then(projectPayments => {
          const ftlsPayments = projectPayments.filter(payment => payment.paymentType === "FTLS");
          expect(ftlsPayments.length).to.be.equal(2);

          const paymentFundERDF = ftlsPayments.find(payment => payment.fundName === "ERDF");
          expect(paymentFundERDF.amountApprovedPerFund).to.be.equal(1199.99);
          expect(paymentFundERDF.amountAuthorizedPerFund).to.be.equal(0);
          expect(paymentFundERDF.amountPaidPerFund).to.be.equal(0);
          expect(paymentFundERDF.totalEligibleAmount).to.be.equal(1999.99);

          const paymentFundOTHER = ftlsPayments.find(payment => payment.fundName = "OTHER");
          expect(paymentFundOTHER.amountApprovedPerFund).to.be.equal(293.19);
          expect(paymentFundOTHER.amountAuthorizedPerFund).to.be.equal(0);
          expect(paymentFundOTHER.amountPaidPerFund).to.be.equal(0);
          expect(paymentFundOTHER.totalEligibleAmount).to.be.equal(1999.99);
        });

        // update payment
        cy.loginByRequest(paymentsUser.email);
        cy.addAuthorizedPayments(applicationId, testData.authorizedPayments);

        // third project report
        cy.loginByRequest(user.applicantUser.email);
        createProjectReportWithoutReportingSchedule(applicationId, ProjectReportType.Finance);
        cy.url().then(url => {
          const reportId = Number(url.replace('/identification', '').split('/').pop());
          cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/financialOverview`, {failOnStatusCode: false});
          projectReportPage.verifyAmountsInTables(testData.expectedResultsR3);
        });
      });
    });
  });

  it('TB-1091 PR - Content report should not include certificates', function () {
    cy.fixture('project/reporting/TB-1091.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {

        // Set-up:
        // Partner Report & Control
        const partnerId = this[application.partners[0].details.abbreviation];
        cy.addPartnerReport(partnerId).then(reportId => {
          cy.wrap(reportId).as('reportId');
          cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
          cy.updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures);
          cy.runPreSubmissionPartnerReportCheck(partnerId, reportId);
          cy.submitPartnerReport(partnerId, reportId);

          cy.loginByRequest(user.controllerUser.email);
          cy.startControlWork(partnerId, reportId);

          cy.updateControlReportIdentification(partnerId, reportId, controlReportIdentification);
          cy.finalizeControl(partnerId, reportId);
        });

        // Contract monitoring
        createReportingDeadlines(applicationId, testData).then(deadlines => {
          const contentDeadlineId = deadlines.find(deadline => deadline.type == 'Content')?.id;

          cy.loginByRequest(user.applicantUser.email);
          cy.createProjectReport(applicationId, {deadlineId: contentDeadlineId}).then(projectReportId => {

            cy.submitProjectReport(applicationId, projectReportId);

            // 1
            createJsMaUser(testData);
            cy.get<number>("@jsmaUserId").then((jsmaUserId) => {

              cy.loginByRequest(user.admin.email);
              cy.assignUserToProject(applicationId, jsmaUserId);

              cy.loginByRequest(testData.jsmaUser.email);
              cy.startProjectReportVerification(applicationId, projectReportId);

              // 2
              cy.finalizeProjectReportVerification(applicationId, projectReportId);
              cy.visit(`/app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
              cy.get('mat-cell.mat-column-amountRequested').should('contain.text', ' ');
              cy.get('mat-cell.mat-column-totalEligible').should('contain.text', ' ');
            });
          });
        });
      });
    });
  });
});

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

function createProjectReportWithoutReportingSchedule(applicationId: number, reportType: ProjectReportType) {
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

function performControlWorkAndFinalize(reportId, partnerId) {
  cy.loginByRequest(user.controllerUser.email);
  cy.startControlWork(partnerId, reportId);

  cy.updateControlReportIdentification(partnerId, reportId, controlReportIdentification);
  cy.finalizeControl(partnerId, reportId);
}

function setReadyForPayment(flag, rowIndex) {
  const ready = flag ? 'Yes' : 'No';
  cy.get('div.jems-table-config').eq(1).children().eq(rowIndex).contains(ready).click();
  cy.contains('Save changes').should('be.visible').click();
  cy.contains('Contract monitoring form saved successfully.').should('be.visible');
  cy.contains('Contract monitoring form saved successfully.').should('not.exist');
}

function createReportingDeadlines(applicationId, testData) {
  cy.loginByRequest(user.programmeUser.email);
  const yesterday = new Date((new Date()).valueOf() - (1000 * 60 * 60 * 24));

  testData.deadlines[0].date = new Date();
  testData.deadlines[1].date = yesterday;

  return cy.createReportingDeadlines(applicationId, testData.deadlines);
}

function createJsMaUser(testData) {
  cy.loginByRequest(user.admin.email);
  testData.jsmaUser.email = faker.internet.email();

  cy.createRole(testData.jsmaRole).then(roleId => {
    testData.jsmaUser.userRoleId = roleId;

    cy.createUser(testData.jsmaUser).as("jsmaUserId");
  });
}
