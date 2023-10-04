import user from '../fixtures/users.json';
import partner from '../fixtures/api/application/partner/partner.json';
import approvalInfo from '../fixtures/api/application/modification/approval.info.json';
import programmeFund from '../fixtures/api/programme/fund.json';
import fastTrackLumpSum from '../fixtures/api/programme/fastTrackLumpSum.json';
import {faker} from '@faker-js/faker';
import date from 'date-and-time';
import programmeEditorRole from "../fixtures/api/roles/programmeEditorRole.json";
import programmeEditorUser from "../fixtures/api/users/programmeEditorUser.json";
import paymentsRole from "../fixtures/api/roles/paymentsRole.json";
import paymentsUser from "../fixtures/api/users/paymentsUser.json";
import {partnerReportPage} from "./project/partner-reports.pom";
import {findProjectPayments} from "../support/payments.commands";

context('Payments tests', () => {

  before(() => {
    cy.loginByRequest(user.admin.email);
    cy.createRole(programmeEditorRole).then(roleId => {
      programmeEditorUser.userRoleId = roleId;
      programmeEditorUser.email = faker.internet.email();
      cy.createUser(programmeEditorUser);
      paymentsUser.email = faker.internet.email();
      cy.createRole(paymentsRole).then(roleId => {
        paymentsUser.userRoleId = roleId;
        cy.createUser(paymentsUser);
      });

      // create additional fund for the second partner
      cy.loginByRequest(programmeEditorUser.email);
      cy.addProgrammeFund(programmeFund);
    });
  });

  it('TB-775 Payment section population with FTLS info', function () {
    cy.fixture('payments/TB-775.json').then(testData => {
      cy.fixture('api/call/1.step.call.json').then(call => {
        cy.fixture('api/application/application.json').then(application => {

          cy.loginByRequest(programmeEditorUser.email);
          // create two FTLS, first one to be used twice
          cy.createLumpSum(fastTrackLumpSum).then(lumpSumId1 => {
            const fastTrackLumpSum2 = {...fastTrackLumpSum};
            fastTrackLumpSum2.cost = 649.99;
            cy.createLumpSum(fastTrackLumpSum2).then(lumpSumId2 => {

              // customize the call
              call.generalCallSettings.additionalFundAllowed = true;
              call.generalCallSettings.funds[0].adjustable = true;
              testData.callFund.programmeFund.id = this.fundId;
              call.generalCallSettings.funds.push(testData.callFund);
              cy.loginByRequest(user.programmeUser.email);
              call.budgetSettings.lumpSums.push(2, lumpSumId1, lumpSumId2);
              cy.createCall(call).then(callId => {
                cy.publishCall(callId);

                application.details.projectCallId = callId;
                application.partners[0].cofinancing.partnerContributions[0].amount = 6853.10;
                application.partners[1].cofinancing.partnerContributions[2].amount = 4964.02;

                testData.projectLumpSums[1].programmeLumpSumId = lumpSumId1;
                testData.projectLumpSums[2].programmeLumpSumId = lumpSumId1;
                testData.projectLumpSums[3].programmeLumpSumId = lumpSumId2;

                application.lumpSums = testData.projectLumpSums;
                cy.loginByRequest(user.applicantUser.email);
                cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {
                  cy.visit('/');
                  cy.contains('Dashboard').should('be.visible');
                  cy.contains('Payments').should('not.exist');

                  cy.loginByRequest(paymentsUser.email);
                  cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});

                  setReadyForPayment(true, 1);

                  cy.contains('Payments').click();
                  cy.get('table mat-row:nth-child(1)').then(row => {
                    assertPaymentType(row, 'FTLS');
                    assertPaymentProjectId(row, applicationId);
                    assertProjectAcronym(row, application.identification.acronym);
                    assertClaimNo(row, '0');
                    assertSubmissionDate(row, date.format(new Date, 'MM/DD/YYYY'));
                    assertMAApprovalDate(row, date.format(new Date, 'MM/DD/YYYY'));
                    assertTotalApproved(row, '1.999,00');
                    assertFund(row, 'OTHER');
                    assertApprovedPerFund(row, '293,05');
                    assertAuthorised(row, '0,00');
                    assertPaid(row, '0,00');
                    assertRemainingToBePaid(row, '293,05');
                  });

                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertPaymentProjectId(row, applicationId);
                    assertProjectAcronym(row, application.identification.acronym);
                    assertTotalApproved(row, '1.999,00');
                    assertFund(row, 'ERDF');
                    assertApprovedPerFund(row, '1.199,40');
                    assertRemainingToBePaid(row, '1.199,40');
                  });

                  cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
                  setReadyForPayment(false, 1);
                  setReadyForPayment(true, 2);

                  cy.contains('Payments').click();
                  cy.get('table mat-row:nth-child(1)').then(row => {
                    assertPaymentProjectId(row, applicationId);
                    assertFund(row, 'OTHER');
                    assertApprovedPerFund(row, '296,45');
                    assertRemainingToBePaid(row, '296,45');
                  });
                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertFund(row, 'NEIGHBOURHOOD_CBC');
                    assertApprovedPerFund(row, '249,75');
                    assertRemainingToBePaid(row, '249,75');

                  });
                  cy.get('table mat-row:nth-child(3)').then(row => {
                    assertFund(row, 'ERDF');
                    assertApprovedPerFund(row, '600,00');
                    assertRemainingToBePaid(row, '600,00');
                  });

                  cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
                  setReadyForPayment(false, 2);
                  setReadyForPayment(true, 3);

                  cy.contains('Payments').click();
                  cy.get('table mat-row:nth-child(1)').then(row => {
                    assertPaymentProjectId(row, applicationId);
                    assertFund(row, 'OTHER');
                    assertApprovedPerFund(row, '97,49');
                    assertRemainingToBePaid(row, '97,49');
                  });

                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertFund(row, 'NEIGHBOURHOOD_CBC');
                    assertApprovedPerFund(row, '162,49');
                    assertRemainingToBePaid(row, '162,49');
                  });

                  cy.startModification(applicationId, user.programmeUser.email);
                  partner.cofinancing = testData.partnerCofinancingAfterModification;
                  testData.partnerCofinancingAfterModification.finances[2].fundId = this.fundId;
                  cy.loginByRequest(user.applicantUser.email);
                  cy.updatePartnerCofinancing(this[partner.details.abbreviation], partner.cofinancing);
                  cy.runPreSubmissionCheck(applicationId);
                  cy.submitProjectApplication(applicationId);
                  cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

                  cy.loginByRequest(paymentsUser.email);
                  cy.visit('app/payments', {failOnStatusCode: false});

                  cy.get('table mat-row:nth-child(1)').then(row => {
                    assertPaymentProjectId(row, applicationId);

                    assertFund(row, 'OTHER');
                    assertApprovedPerFund(row, '97,49');
                    assertRemainingToBePaid(row, '97,49');
                  });

                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertFund(row, 'NEIGHBOURHOOD_CBC');
                    assertApprovedPerFund(row, '162,49');
                    assertRemainingToBePaid(row, '162,49');
                  });

                  cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
                  setReadyForPayment(false, 3);
                  setReadyForPayment(true, 2);

                  cy.contains('Payments').click();
                  cy.get('table mat-row:nth-child(1)').then(row => {
                    assertPaymentProjectId(row, applicationId);
                    assertTotalApproved(row, '1.999,00');
                    assertFund(row, 'OTHER');
                    assertApprovedPerFund(row, '49,95');
                    assertRemainingToBePaid(row, '49,95');
                  });

                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertTotalApproved(row, '1.999,00');
                    assertFund(row, 'OTHER');
                    assertApprovedPerFund(row, '146,60');
                    assertRemainingToBePaid(row, '146,60');
                  });

                  cy.get('table mat-row:nth-child(3)').then(row => {
                    assertFund(row, 'NEIGHBOURHOOD_CBC');
                    assertApprovedPerFund(row, '299,70');
                    assertRemainingToBePaid(row, '299,70');
                  });

                  cy.get('table mat-row:nth-child(4)').then(row => {
                    assertFund(row, 'ERDF');
                    assertApprovedPerFund(row, '749,85');
                    assertRemainingToBePaid(row, '749,85');
                  });
                });
              });
            });
          });
        });
      });
    });
  });

  it('TB-1018 FTLS in partner report overviews', function () {
    cy.fixture('payments/TB-1018.json').then(testData => {
      cy.fixture('api/call/1.step.call.json').then(call => {
        cy.fixture('api/application/application.json').then(application => {

          cy.loginByRequest(user.programmeUser.email);
          cy.createCall(call).then(callId => {
            cy.publishCall(callId);
            application.details.projectCallId = callId;
          });

          cy.loginByRequest(user.applicantUser.email);
          cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {

            const partnerId = this[application.partners[0].details.abbreviation];

            cy.loginByRequest(user.programmeUser.email);
            cy.setContractingFastTrackLumpSums(applicationId, testData.contractingFastTrackLumpSums);

            cy.loginByRequest(user.applicantUser.email);
            cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborators);
            
            // group order 2
            cy.addPartnerReport(partnerId).then(partnerReportId => {
              cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${partnerReportId}/financialOverview`, {failOnStatusCode: false});
              partnerReportPage.verifyAmountsInTables(testData.expectedResults.beforePayment);
            });

            // group order 3
            cy.loginByRequest(paymentsUser.email);
            cy.addAuthorizedPayments(applicationId, testData.authorizedPayments);

            // group order 4
            cy.loginByRequest(user.applicantUser.email);
            cy.addPartnerReport(partnerId).then(partnerReportId => {
              cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${partnerReportId}/financialOverview`, {failOnStatusCode: false});
              partnerReportPage.verifyAmountsInTables(testData.expectedResults.afterPayment);
            });
            
            // group order 5
            cy.startModification(applicationId, user.programmeUser.email);
            cy.updatePartnerCofinancing(partnerId, testData.modifiedCofinancing);
            cy.submitProjectApplication(applicationId);
            cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);
            
            // group order 6
            cy.addPartnerReport(partnerId).then(partnerReportId => {
              cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${partnerReportId}/financialOverview`, {failOnStatusCode: false});
              partnerReportPage.verifyAmountsInTables(testData.expectedResults.afterModification); // TODO affected by bug MP2-4073
            });

            // group order 7
            cy.loginByRequest(paymentsUser.email);
            findProjectPayments(applicationId).then(projectPayments => {
              const projectPayment = projectPayments.find(payment => payment.fundName === testData.authorizedPayments[0].fundName);
              cy.visit(`app/payments/paymentsToProjects/${projectPayment.id}`, {failOnStatusCode: false});
              
              cy.contains('expand_more').click();
              cy.get('mat-checkbox').eq(1).click();
              cy.contains('Save changes').should('be.visible').click();
              cy.contains('Payment details have been saved successfully!').should('be.visible');
              cy.contains('Payment details have been saved successfully!').should('not.exist');
              
              cy.get('mat-checkbox').eq(0).click();
              cy.contains('Save changes').should('be.visible').click();
              cy.contains('Payment details have been saved successfully!').should('be.visible');
              cy.contains('Payment details have been saved successfully!').should('not.exist');

              cy.contains('mat-icon', 'delete').click();
              cy.contains('Save changes').should('be.visible').click();
              cy.contains('Payment details have been saved successfully!').should('be.visible');
            });
            cy.loginByRequest(user.programmeUser.email);
            cy.setContractingFastTrackLumpSums(applicationId, testData.contractingFastTrackLumpSumsDisabled);
            
            // group order 8
            cy.loginByRequest(user.applicantUser.email);
            cy.addPartnerReport(partnerId).then(partnerReportId => {
              cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${partnerReportId}/financialOverview`, {failOnStatusCode: false});
              partnerReportPage.verifyAmountsInTables(testData.expectedResults.afterDeletePayment);
            });
            
            // group order 9
            cy.loginByRequest(user.programmeUser.email);
            cy.setContractingFastTrackLumpSums(applicationId, testData.contractingFastTrackLumpSums);
            
            // group order 10
            cy.loginByRequest(user.applicantUser.email);
            cy.addPartnerReport(partnerId).then(partnerReportId => {
              cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${partnerReportId}/financialOverview`, {failOnStatusCode: false});
              partnerReportPage.verifyAmountsInTables(testData.expectedResults.afterReadyForPayment);
            });
          });
        });
      });
    });
  });
});

function setReadyForPayment(flag, rowIndex) {
  const ready = flag ? 'Yes' : 'No';
  cy.get('div.jems-table-config').eq(1).children().eq(rowIndex).contains(ready).click();
  cy.contains('Save changes').should('be.visible').click();
  cy.contains('Contract monitoring form saved successfully.').should('be.visible');
  cy.contains('Contract monitoring form saved successfully.').should('not.exist');
}

function assertPaymentType(row, type) {
  expect(row.children().get(1)).to.contain(type);
}

function assertPaymentProjectId(row, id) {
  expect(row.children().get(2)).to.contain(id);
}

function assertProjectAcronym(row, acronym) {
  expect(row.children().get(3)).to.contain(acronym);
}

function assertClaimNo(row, claimNo) {
  expect(row.children().get(4)).to.contain(claimNo);
}

function assertSubmissionDate(row, submissionDate) {
  expect(row.children().get(5)).to.contain(submissionDate);
}

function assertMAApprovalDate(row, maApprovalDate) {
  expect(row.children().get(6)).to.contain(maApprovalDate);
}

function assertTotalApproved(row, totalApproved) {
  expect(row.children().get(7)).to.contain(totalApproved);
}

function assertFund(row, fund) {
  expect(row.children().get(8)).to.contain(fund);
}

function assertApprovedPerFund(row, approvedPerFund) {
  expect(row.children().get(9)).to.contain(approvedPerFund);
}

function assertAuthorised(row, authorised) {
  expect(row.children().get(10)).to.contain(authorised);
}

function assertPaid(row, paid) {
  expect(row.children().get(11)).to.contain(paid);
}

function assertDateOfLastPayment(row, dateOfLastPayment) {
  expect(row.children().get(12)).to.contain(dateOfLastPayment);
}

function assertRemainingToBePaid(row, remainingToBePaid) {
  expect(row.children().get(13)).to.contain(remainingToBePaid);
}