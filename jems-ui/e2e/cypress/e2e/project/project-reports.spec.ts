import user from '@fixtures/users.json';
import application from '@fixtures/api/application/application.json';
import approvalInfo from '@fixtures/api/application/modification/approval.info.json';
import rejectionInfo from '@fixtures/api/application/modification/rejection.info.json';
import call from '@fixtures/api/call/1.step.call.json';
import {faker} from '@faker-js/faker';
import reporting from '@fixtures/api/reporting/reporting.json';
import partnerParkedExpenditures from '@fixtures/api/partnerReport/partnerParkedExpenditures.json';
import partnerReportIdentification from '@fixtures/api/partnerReport/partnerReportIdentification.json';
import controlReportIdentification from '@fixtures/api/partnerControlReport/controlReportIdentification.json';
import paymentsUser from '@fixtures/api/users/paymentsUser.json';
import paymentsRole from '@fixtures/api/roles/paymentsRole.json';
import partner from '@fixtures/api/application/partner/partner.json';
import {projectReportPage} from './reports-page.pom';
import {ProjectReportType} from './ProjectReportType';
import controllerAssignment from "@fixtures/api/control/assignment.json";

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
      cy.contains('Add Project Report').click();

      cy.contains('div', 'Reporting period start date (').next().click();
      cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();

      cy.contains('div', 'Reporting period end date (').next().click();
      cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();

      cy.contains('Link to reporting schedule').click();
      cy.contains('mat-option span', '1, Period 1').click();

      cy.contains('button', 'Create').should('be.enabled').click();
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
      cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[1].id}).then(projectReportId => {
        cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/`, {failOnStatusCode: false});
        assertProjectReportData(updatedPartnerDetails, '2.0');
      });

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
      cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[2].id}).then(projectReportId => {
        cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}`, {failOnStatusCode: false});
        assertProjectReportData(updatedPartnerDetails, '2.0');
      });
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

  it('TB-1023 PR - Work plan progress & Project results', function () {
    cy.fixture('project/reporting/TB-1023.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {
        application.details.projectCallId = this.callId;
        application.reportingDeadlines = [];
        cy.loginByRequest(user.applicantUser.email);
        cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
          createProjectReportWithoutReportingSchedule(applicationId, ProjectReportType.Content);

          cy.url().then(url => {
            const reportId = Number(url.replace('/identification', '').split('/').pop());

            cy.updateProjectReportWorkPlan(applicationId, reportId, testData.workPlanUpdate);

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
            cy.contains('Completed in prior report. No changes.').should('not.exist');
            cy.contains('New changes after completion.').scrollIntoView().should('be.visible');

            cy.get('input[type=file]').eq(0).selectFile('cypress/fixtures/project/reporting/fileToUpload.txt', {force: true});
            cy.contains('fileToUpload.txt').should('be.visible');

            cy.visit(`app/project/detail/${applicationId}/projectReports/${reportId}/resultsAndPrinciples`, {failOnStatusCode: false});

            cy.contains('mat-expansion-panel', 'Result 1').within(() => {
              cy.contains('Result 1').click();
              cy.contains('mat-form-field', 'Delivery period').find('input').should('have.value', 'Period 8, month 22 - 24');
              cy.contains('mat-form-field', 'Measurement Unit').find('input').should('have.value', 'annual FTEs');
              cy.contains('mat-form-field', 'Baseline').find('input').should('have.value', '0,00');
              cy.contains('mat-form-field', 'Target Value').find('input').should('have.value', '13,00');
              cy.contains('mat-form-field', 'Cumulative value').find('input').should('have.value', '0,00');
            });
            cy.contains('mat-expansion-panel', 'Result 2').within(() => {
              cy.contains('Result 2').click();
              cy.contains('mat-form-field', 'Delivery period').find('input').should('have.value', 'After project implementation');
              cy.contains('mat-form-field', 'Measurement Unit').find('input').should('have.value', 'annual FTEs');
              cy.contains('mat-form-field', 'Baseline').find('input').should('have.value', '1,00');
              cy.contains('mat-form-field', 'Target Value').find('input').should('have.value', '10,00');
              cy.contains('mat-form-field', 'Cumulative value').find('input').should('have.value', '0,00');
            });
          });
        });
      });
    });
  });

  it('TB-1093 Regular payments are properly reflected in following project reports', function () {
    cy.fixture('project/reporting/TB-1093.json').then(testData => {
      cy.fixture('api/partnerReport/partnerReportExpenditures.json').then(partnerReportExpenditures => {
        cy.fixture('api/application/application.json').then(application => {
          application.details.projectCallId = this.callId;
          application.reportingDeadlines = [];
          cy.loginByRequest(user.applicantUser.email);
          application.contractMonitoring.fastTrackLumpSums[0].readyForPayment = false;
          cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
            const partnerId1 = this[application.partners[0].details.abbreviation];

            // add partner report
            cy.addPartnerReport(partnerId1)
              .then(reportId => {
                cy.updatePartnerReportIdentification(partnerId1, reportId, partnerReportIdentification);
                cy.updatePartnerReportExpenditures(partnerId1, reportId, partnerReportExpenditures)
                  .then(savedExpenditures => {
                    setPartnerReportExpenditureVerificationIds(savedExpenditures, partnerParkedExpenditures)
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

              const paymentFundERDF = ftlsPayments.find(payment => payment.fund.abbreviation.find(abbreviation =>
                abbreviation.language === 'EN').translation === "ERDF");
              expect(paymentFundERDF.fundAmount).to.be.equal(1199.99);
              expect(paymentFundERDF.amountAuthorizedPerFund).to.be.equal(0);
              expect(paymentFundERDF.amountPaidPerFund).to.be.equal(0);
              expect(paymentFundERDF.totalEligibleAmount).to.be.equal(1607.29);

              const paymentFundOTHER = ftlsPayments.find(payment => payment.fund.abbreviation.find(abbreviation =>
                abbreviation.language === 'EN').translation === "Other fund EN");
              expect(paymentFundOTHER.fundAmount).to.be.equal(293.19);
              expect(paymentFundOTHER.amountAuthorizedPerFund).to.be.equal(0);
              expect(paymentFundOTHER.amountPaidPerFund).to.be.equal(0);
              expect(paymentFundOTHER.totalEligibleAmount).to.be.equal(392.70);
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
    });
  });

  it('TB-1091 PR - Content report should not include certificates', function () {
    cy.fixture('project/reporting/TB-1091.json').then(testData => {
      cy.fixture('api/partnerReport/partnerReportExpenditures.json').then(partnerReportExpenditures => {
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
          const deadlines = {content: new Date()};
          createReportingDeadlines(applicationId, testData, deadlines).then(deadlines => {
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
                cy.get('mat-cell.mat-column-amountRequested').should('have.text', '  ');
                cy.get('mat-cell.mat-column-totalEligible').should('have.text', '  ');
              });
            });
          });
        });
      });
    });
  });

  it('TB-1025 PR - Finance report should include certificates', function () {
    cy.fixture('project/reporting/TB-1025.json').then(testData => {
      cy.fixture('api/partnerReport/partnerReportExpenditures.json').then(partnerReportExpenditures => {
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
          const deadlines = {finance: new Date()};
          createReportingDeadlines(applicationId, testData, deadlines).then(deadlines => {
            const contentDeadlineId = deadlines.find(deadline => deadline.type == 'Finance')?.id;

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
                cy.get('mat-cell.mat-column-amountRequested').should('have.text', ' 9.929,78 ');
                cy.get('mat-cell.mat-column-totalEligible').should('have.text', ' 9.929,78 ');
              });
            });
          });
        });
      });
    });
  });

  it('TB-1027 PR - Financial overview - Summary shows correct figures across multiple project reports', function () {
    cy.fixture('project/reporting/TB-1027.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {

        const partnerId = this[application.partners[0].details.abbreviation];
        // First - partner and project report
        cy.completeReporting(applicationId, reporting).then(projectReportId => {
          cy.visit(`/app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          projectReportPage.verifyAmountsInTables(testData.projectReport1ExpectedResults);
        });

        // create and certify second partner report
        const partnerReportDetails = {
          partnerReport: reporting.projectReports[0].partnerReports[0].partnerReport,
          controlWork: reporting.projectReports[0].partnerReports[0].controlWork
        }
        const partnerReport2Details = JSON.parse(JSON.stringify(partnerReportDetails));
        partnerReport2Details.partnerReport.contributions.toBeUpdated = [
          ...partnerReportDetails.partnerReport.contributions.toBeUpdated,
          ...partnerReportDetails.partnerReport.contributions.toBeCreated
        ];
        partnerReport2Details.partnerReport.contributions.toBeCreated = [];
        cy.createCertifiedPartnerReport(partnerId, partnerReport2Details, user.controllerUser.email);


        // pay a part of the newly created Regular payment for Project Report 1
        cy.loginByRequest(paymentsUser.email);
        cy.findProjectPayments(applicationId).then(projectPayments => {
          const regularPayments = projectPayments.filter(payment => payment.paymentType === "REGULAR");
          expect(regularPayments.length).to.be.equal(3);
          cy.addAuthorizedPayments(applicationId, testData.authorizedPayments);
        });

        // Second project report
        cy.loginByRequest(user.applicantUser.email);
        cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[1].id}).then(projectReportId => {
          cy.visit(`/app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          projectReportPage.verifyAmountsInTables(testData.projectReport2ExpectedResults);
          cy.contains('Current report after verification').should('not.exist');
        });
      });
    });
  });

  it('TB-1126 PR - Financial overview - Breakdown per investment shows correct figures across multiple project reports', function () {
    cy.fixture('project/reporting/TB-1126.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {
        cy.loginByRequest(user.applicantUser.email);

        // remove other costs flat rate so that any expenditure items can be created
        application.partners[1].budget.options.otherCostsOnStaffCostsFlatRate = null;
        application.partners[1].cofinancing.partnerContributions[2].amount = 4969.12;
        cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {

          // don't park a relevant expenditure item
          reporting.projectReports[0].partnerReports[0].controlWork.expenditureVerification[3].parked = false;
          reporting.projectReports[0].partnerReports[0].controlWork.expenditureVerification[3].parkedOn = null;

          // change expenditure category to be able to link it to investments
          reporting.projectReports[0].partnerReports[1].partnerReport.expenditures = testData.expenditures;
          reporting.projectReports[0].partnerReports[1].controlWork.expenditureVerification = testData.controlExpenditureVerification;
          reporting.projectReports[0].verificationWork.expenditures = testData.verificationWork;
          cy.completeReporting(applicationId, reporting);

          const leadPartnerId = this[application.partners[0].details.abbreviation];

          cy.addPartnerReport(leadPartnerId).then(reportId => {
            cy.updatePartnerReportExpenditures(leadPartnerId, reportId, testData.draftPartnerReportExpenditures);
          });

          cy.loginByRequest(user.applicantUser.email);
          cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[2].id}).then(projectReportId => {
            cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
            cy.contains('Project expenditure - breakdown per investment').scrollIntoView().should('be.visible');
            projectReportPage.verifyAmountsInTables(testData.expectedResults);
          });
        });
      });
    });
  });

  it('TB-1127 PR - Financial overview - Breakdown per Partner shows correct figures across multiple project reports', function () {
    cy.fixture('project/reporting/TB-1127.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
        const firstPartnerId = this[application.partners[0].details.abbreviation];
        cy.createCertifiedPartnerReport(firstPartnerId, reporting.projectReports[0].partnerReports[0], user.controllerUser.email);

        const secondPartnerId = this[application.partners[1].details.abbreviation];
        cy.createCertifiedPartnerReport(secondPartnerId, reporting.projectReports[0].partnerReports[1], user.controllerUser.email);

        cy.loginByRequest(user.applicantUser.email);
        cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[2].id}).then(projectReportId => {
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          cy.contains('Project expenditure - overview per partner/per cost category - Current report').scrollIntoView().should('be.visible');
          projectReportPage.verifyAmountsInTables(testData.expectedResults.firstResults);

          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/certificate`, {failOnStatusCode: false});
          cy.contains('mat-row', 'PP2').find('mat-checkbox').click();
          cy.contains('Confirm').click();
          cy.contains('mat-row', 'PP2').find('mat-checkbox').should('not.be.checked')

          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          cy.contains('Project expenditure - overview per partner/per cost category - Current report').scrollIntoView().should('be.visible');
          projectReportPage.verifyAmountsInTables(testData.expectedResults.secondResults);
        });

        cy.startModification(applicationId, user.programmeUser.email);
        cy.loginByRequest(user.applicantUser.email);
        const thirdPartner = JSON.parse(JSON.stringify(partner));
        thirdPartner.details.abbreviation = 'Partner3';
        cy.createFullPartner(applicationId, thirdPartner).then(thirdPartnerId => {
          cy.runPreSubmissionCheck(applicationId);
          cy.submitProjectApplication(applicationId);

          cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

          cy.loginByRequest(user.admin.email);
          controllerAssignment.assignmentsToAdd[0].partnerId = thirdPartnerId;
          cy.assignInstitution(controllerAssignment);

          cy.loginByRequest(user.applicantUser.email);
          cy.visit(`app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});
          cy.get('jems-partner-team-privileges-expansion-panel').eq(2).scrollIntoView().find('input').eq(0).type(user.applicantUser.email);
          cy.get('jems-partner-team-privileges-expansion-panel').eq(2).find('mat-button-toggle-group').contains('edit').click();
          cy.contains('Save changes').click();

          cy.createCertifiedPartnerReport(thirdPartnerId, reporting.projectReports[0].partnerReports[0], user.controllerUser.email);
        });

        cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[2].id}).then(projectReportId => {
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/certificate`, {failOnStatusCode: false});
          cy.get('mat-table').find('mat-checkbox').eq(0).get('input').should('be.enabled');
          cy.get('mat-table').find('mat-checkbox').eq(1).get('input').should('be.enabled');
          cy.get('mat-table').find('mat-checkbox').eq(2).get('input').should('be.disabled');
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          cy.contains('Project expenditure - overview per partner/per cost category - Current report').scrollIntoView().should('be.visible');
          projectReportPage.verifyAmountsInTables(testData.expectedResults.thirdResults);
        });

        cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[2].id}).then(projectReportId => {
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          cy.contains('Project expenditure - breakdown per Unit cost (in Euro)').scrollIntoView();
          cy.contains('Project expenditure - breakdown per Unit cost (in Euro)').should('be.visible');
          cy.contains('Project expenditure - overview per partner/per cost category - Current report').should('not.exist');
        });
      });
    });
  });

  it('TB-1128 PR - Financial overview - Breakdown per Lump Sums and Unit Costs shows correct figures across multiple project reports', function () {
    cy.fixture('project/reporting/TB-1128.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
        const leadPartnerId = this[application.partners[0].details.abbreviation];

        cy.createCertifiedPartnerReport(leadPartnerId, reporting.projectReports[0].partnerReports[0], user.controllerUser.email);

        cy.loginByRequest(paymentsUser.email);
        editFTLSPayment(applicationId, 0, true);
        editFTLSPayment(applicationId, 1, false);

        cy.loginByRequest(user.applicantUser.email);
        cy.visit(`app/project/detail/${applicationId}/projectReports`, {failOnStatusCode: false});
        cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[2].id}).then(projectReportId => {
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          projectReportPage.verifyAmountsInTables(testData.expectedResults);
        });
      });
    });
  });

  it('TB-1129 PR - Financial overview - Summary of deducted items by control shows correct figures across multiple project reports', function () {
    cy.fixture('project/reporting/TB-1129.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);

      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
        const partnerId = this[application.partners[0].details.abbreviation];
        cy.createCertifiedPartnerReport(partnerId, reporting.projectReports[0].partnerReports[0], user.controllerUser.email);

        cy.addPartnerReport(partnerId).then(reportId => {
          cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
          cy.updatePartnerReportExpenditures(partnerId, reportId, testData.expenditures);

          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/expenditures`, {failOnStatusCode: false});

          reincludeParkedExpenditureByDescription('expenditure 6');
          deleteParkedExpenditureByDescription('expenditure 4');

          cy.submitPartnerReport(partnerId, reportId);

          cy.loginByRequest(user.controllerUser.email);
          cy.startControlWork(partnerId, reportId);
          cy.updateControlReportIdentification(partnerId, reportId, controlReportIdentification);
          cy.updateControlReportExpenditureVerification(partnerId, reportId, testData.expenditureVerification);
          cy.finalizeControl(partnerId, reportId);
        });

        cy.loginByRequest(user.applicantUser.email);
        cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[2].id}).then(projectReportId => {
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          cy.contains('Project expenditure - Summary of deducted items by control - Current report').scrollIntoView().should('be.visible');
          projectReportPage.verifyAmountsInTables(testData.expectedResults.firstResults, '#breakdown-per-partner-deduction-table');
        });

        cy.createProjectReport(applicationId, {deadlineId: application.reportingDeadlines[2].id}).then(projectReportId => {
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReportId}/financialOverview`, {failOnStatusCode: false});
          cy.contains('Project expenditure - breakdown per Unit cost (in Euro)').scrollIntoView();
          cy.contains('Project expenditure - breakdown per Unit cost (in Euro)').should('be.visible');
          cy.contains('Project expenditure - overview per partner/per cost category - Current report').should('not.exist');
          cy.contains('Project expenditure - Summary of deducted items by control - Current report').should('not.exist');
        });
      });
    });
  });

  it('TB-1073 Project Report should properly reflect the Application Form and Partner Report data it is based on', function () {
    cy.fixture('project/reporting/TB-1073.json').then(testData => {
      const adjustedApplication = JSON.parse(JSON.stringify(application));
      cy.loginByRequest(user.programmeUser.email);
      call.preSubmissionCheckSettings.pluginKey = 'jems-pre-condition-check-off';
      cy.createCall(call).then(callId => {
        adjustedApplication.details.projectCallId = callId;
        cy.publishCall(callId);
      });

      adjustedApplication.description.relevanceAndContext.projectBenefits = [];
      adjustedApplication.description.workPlan = [];
      adjustedApplication.partners[0].stateAid.activities = [];
      adjustedApplication.partners[1].stateAid.activities = [];
      adjustedApplication.description.results = [];
      adjustedApplication.description.management.projectHorizontalPrinciples = null;
      cy.loginByRequest(user.applicantUser.email);

      cy.createContractedApplication(adjustedApplication, user.programmeUser.email).then(applicationId => {
        cy.createProjectReport(applicationId, {deadlineId: adjustedApplication.reportingDeadlines[2].id}).then(projectReport1Id => {
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReport1Id}/identification`, {failOnStatusCode: false});
          assertSectionsVisibility(false);

          // group order 2
          call.preSubmissionCheckSettings.pluginKey = 'standard-pre-condition-check-plugin';
          cy.loginByRequest(user.programmeUser.email);
          cy.updateCallPreSubmissionCheckSettings(this.callId, call.preSubmissionCheckSettings);

          adjustedApplication.description = application.description;
          const partner1Id = this[adjustedApplication.partners[0].details.abbreviation]
          const partner2Id = this[adjustedApplication.partners[1].details.abbreviation]
          adjustedApplication.partners = application.partners;
          cy.startModification(applicationId);

          cy.loginByRequest(user.applicantUser.email);
          cy.createProjectWorkPlan(applicationId, adjustedApplication.description.workPlan);
          cy.updateProjectRelevanceAndContext(applicationId, adjustedApplication.description.relevanceAndContext);
          cy.updatePartnerStateAid(partner1Id, adjustedApplication.partners[0].stateAid);
          cy.updatePartnerStateAid(partner2Id, adjustedApplication.partners[1].stateAid);
          cy.createProjectResults(applicationId, adjustedApplication.description.results);
          cy.updateProjectManagement(applicationId, adjustedApplication.description.management);
          cy.submitProjectApplication(applicationId);
          cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

          // old report should be the same
          cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReport1Id}/identification`, {failOnStatusCode: false});
          assertSectionsVisibility(false);

          // group order 3
          cy.createProjectReport(applicationId, {deadlineId: adjustedApplication.reportingDeadlines[2].id}).then(projectReport2Id => {
            cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReport2Id}/identification`, {failOnStatusCode: false});
            assertSectionsVisibility(true);

            // group oder 4
            cy.createCertifiedPartnerReport(partner1Id, reporting.projectReports[0].partnerReports[0], user.controllerUser.email);
            cy.createProjectReport(applicationId, {deadlineId: adjustedApplication.reportingDeadlines[2].id}).then(projectReport3Id => {
              cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReport3Id}/identification`, {failOnStatusCode: false});
              projectReportPage.verifySpendingProfile(testData.expectedResults);
              assertSectionsVisibility(true);

              // old reports should stay the same
              cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReport1Id}/identification`, {failOnStatusCode: false});
              assertSectionsVisibility(false);

              cy.visit(`app/project/detail/${applicationId}/projectReports/${projectReport2Id}/identification`, {failOnStatusCode: false});
              assertSectionsVisibility(true);
            });
          });
        });
      });
    });
  });
});

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

function createReportingDeadlines(applicationId, testData, deadlines) {
  cy.loginByRequest(user.programmeUser.email);
  const yesterday = new Date((new Date()).valueOf() - (1000 * 60 * 60 * 24));

  testData.deadlines[0].date = deadlines.content ?? yesterday;
  testData.deadlines[1].date = deadlines.finance ?? yesterday;

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

function setPartnerReportExpenditureVerificationIds(savedExpenditures, expendituresVerification) {
  for (let i = 0; i < savedExpenditures.length; i++) {
    expendituresVerification[i].id = savedExpenditures[i].id;
  }
}

function editFTLSPayment(applicationId: number, ftlsNumber: number, isPaid: boolean) {
  cy.visit(`app/payments/paymentsToProjects`, {failOnStatusCode: false});
  cy.contains('mat-expansion-panel-header', 'Filters').click();
  cy.get('mat-expansion-panel').contains('div', 'ProjectID').find('input').type(applicationId + '{enter}');
  cy.get('jems-table').find('mat-row').eq(ftlsNumber).click();
  cy.contains('div.full-payment-row', 'Lead Partner').find('button').click();
  cy.get('.installments-table').find('mat-checkbox').eq(0).click();
  if (isPaid) {
    cy.get('div.installments-table mat-datepicker-toggle').find('button').click();
    cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();
    cy.get('.installments-table').find('mat-checkbox').eq(1).click();
  }
  cy.contains('Save changes').click();
}


function confirmReinclusionOfParkedExpenditure(description: string) {
  cy.contains('button', 'Confirm')
    .click();
  cy.contains('button', 'Confirm')
    .should('not.exist');

  cy.contains('jems-partner-report-expenditures-parked mat-row', description)
    .should('not.exist');
}

function confirmDeletionOfParkedExpenditure(description) {
  cy.contains('button', 'Confirm')
    .click();
  cy.contains('button', 'Confirm')
    .should('not.exist');

  cy.contains('jems-partner-report-expenditures-parked mat-row', description)
    .should('not.exist');
}

function reincludeParkedExpenditureByDescription(description: string) {
  cy.contains('jems-partner-report-expenditures-parked mat-row', description)
    .contains('mat-icon', 'sync')
    .click();

  confirmReinclusionOfParkedExpenditure(description);
}

function deleteParkedExpenditureByDescription(description: string) {
  cy.contains('jems-partner-report-expenditures-parked mat-row', description)
    .contains('mat-icon', 'delete')
    .click();

  confirmDeletionOfParkedExpenditure(description);
}

function assertSectionsVisibility(isVisible) {
  if (isVisible) {
    // TODO after MP2-4287 is fixed, replace the two assertions below
    // cy.contains('Overview of Project outputs and result overview').should('be.visible')
    cy.contains('Programme Result Indicator').scrollIntoView().should('be.visible');
    // TODO after MP2-4601 is fixed uncomment the below assertion
    //cy.contains('Partner spending profile (in Euro)').should('be.visible');
    cy.contains('Target groups').scrollIntoView().should('be.visible');

    cy.contains('Work plan progress').click();
    cy.contains('Work package 1').should('be.visible');

    cy.contains('Project results & Horizontal').click();
    // TODO after MP2-4600 is fixed, replace the two assertions below
    // cy.contains('Project results').should(visibilityFlag);
    cy.contains('Result 1').should('be.visible');
    cy.contains('div.jems-table-config', 'Cooperation criteria').find('.mat-button-toggle-checked')
      .should('be.visible');
  } else {
    cy.contains('Project progress report identification').should('be.visible');
    // TODO after MP2-4287 is fixed, replace the two assertions below
    // cy.contains('Overview of Project outputs and result overview').should('not.exist')
    cy.contains('Programme Result Indicator').should('not.exist');
    // TODO after MP2-4601 is fixed uncomment the below assertion
    //cy.contains('Partner spending profile (in Euro)').should('not.exist');
    cy.contains('Target groups').should('not.exist');

    cy.contains('Work plan progress').click();
    cy.contains('Work plan progress').should('be.visible');
    cy.contains('Work package 1').should('not.exist');

    cy.contains('Project results & Horizontal').click();
    cy.contains('Horizontal principles').should('be.visible');
    // TODO after MP2-4600 is fixed, replace the two assertions below
    // cy.contains('Project results').should('not.exist');
    cy.contains('Result 1').should('not.exist');
    cy.contains('div.jems-table-config', 'Cooperation criteria').find('.mat-button-toggle-checked')
      .should('not.exist');
  }
}
