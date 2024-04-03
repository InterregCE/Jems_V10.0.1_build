import user from '../fixtures/users.json';
import partner from '../fixtures/api/application/partner/partner.json';
import approvalInfo from '../fixtures/api/application/modification/approval.info.json';
import programmeFund from '../fixtures/api/programme/fund.json';
import fastTrackLumpSum from '../fixtures/api/programme/fastTrackLumpSum.json';
import {faker} from '@faker-js/faker';
import programmeEditorRole from "../fixtures/api/roles/programmeEditorRole.json";
import programmeEditorUser from "../fixtures/api/users/programmeEditorUser.json";
import paymentsRole from "../fixtures/api/roles/paymentsRole.json";
import paymentsUser from "../fixtures/api/users/paymentsUser.json";
import {partnerReportPage} from "./project/reports-page.pom";
import date from 'date-and-time';
import call from '../fixtures/api/call/1.step.call.json';

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

                testData.contractingFastTrackLumpSums[1].programmeLumpSumId = lumpSumId1;
                testData.contractingFastTrackLumpSums[2].programmeLumpSumId = lumpSumId1;
                testData.contractingFastTrackLumpSums[3].programmeLumpSumId = lumpSumId2;
                application.contractMonitoring.fastTrackLumpSums = testData.contractingFastTrackLumpSums;
                cy.loginByRequest(user.applicantUser.email);
                cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {
                  cy.visit('/');
                  cy.contains('Dashboard').should('be.visible');
                  cy.contains('Payments').should('not.exist');

                  cy.loginByRequest(paymentsUser.email);
                  cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});

                  cy.contains('Payments').click();
                  cy.get('table mat-row:nth-child(1)').then(row => {
                    assertPaymentType(row, 'FTLS');
                    assertPaymentProjectId(row, applicationId);
                    assertProjectAcronym(row, application.identification.acronym);
                    assertClaimNo(row, '0');
                    assertSubmissionDate(row, date.format(new Date(), 'MM/DD/YYYY'));
                    assertMAApprovalDate(row, date.format(new Date(), 'MM/DD/YYYY'));
                    assertTotalApproved(row, '392,51');
                    assertFund(row, 'Other fund EN');
                    assertApprovedPerFund(row, '293,05');
                    assertAuthorised(row, '0,00');
                    assertPaid(row, '0,00');
                    assertRemainingToBePaid(row, '293,05');
                  });

                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertPaymentProjectId(row, applicationId);
                    assertProjectAcronym(row, application.identification.acronym);
                    assertTotalApproved(row, '1.606,49');
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
                    assertFund(row, 'Other fund EN');
                    assertApprovedPerFund(row, '296,45');
                    assertRemainingToBePaid(row, '296,45');
                  });
                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertFund(row, 'Neighbourhood CBC');
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
                    assertFund(row, 'Other fund EN');
                    assertApprovedPerFund(row, '97,49');
                    assertRemainingToBePaid(row, '97,49');
                  });

                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertFund(row, 'Neighbourhood CBC');
                    assertApprovedPerFund(row, '162,49');
                    assertRemainingToBePaid(row, '162,49');
                  });

                  cy.startModification(applicationId, user.programmeUser.email);
                  partner.cofinancing = testData.partnerCofinancingAfterModification;
                  testData.partnerCofinancingAfterModification.finances[2].fundId = this['fundId'];
                  cy.loginByRequest(user.applicantUser.email);
                  cy.updatePartnerCofinancing(this[partner.details.abbreviation], partner.cofinancing);
                  cy.runPreSubmissionCheck(applicationId);
                  cy.submitProjectApplication(applicationId);
                  cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

                  cy.loginByRequest(paymentsUser.email);
                  cy.visit('app/payments', {failOnStatusCode: false});

                  cy.get('table mat-row:nth-child(1)').then(row => {
                    assertPaymentProjectId(row, applicationId);

                    assertFund(row, 'Other fund EN');
                    assertApprovedPerFund(row, '97,49');
                    assertRemainingToBePaid(row, '97,49');
                  });

                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertFund(row, 'Neighbourhood CBC');
                    assertApprovedPerFund(row, '162,49');
                    assertRemainingToBePaid(row, '162,49');
                  });

                  cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
                  setReadyForPayment(false, 3);
                  setReadyForPayment(true, 2);

                  cy.contains('Payments').click();
                  cy.get('table mat-row:nth-child(1)').then(row => {
                    assertPaymentProjectId(row, applicationId);
                    assertTotalApproved(row, '99,90');
                    assertFund(row, programmeFund.abbreviation[1].translation);
                    assertApprovedPerFund(row, '49,95');
                    assertRemainingToBePaid(row, '49,95');
                  });

                  cy.get('table mat-row:nth-child(2)').then(row => {
                    assertTotalApproved(row, '196,36');
                    assertFund(row, 'Other fund EN');
                    assertApprovedPerFund(row, '146,60');
                    assertRemainingToBePaid(row, '146,60');
                  });

                  cy.get('table mat-row:nth-child(3)').then(row => {
                    assertFund(row, 'Neighbourhood CBC');
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
              partnerReportPage.verifyAmountsInTables(testData.expectedResults.afterModification);
            });

            // group order 7
            cy.loginByRequest(paymentsUser.email);
            cy.findProjectPayments(applicationId).then(projectPayments => {
              const projectPayment = projectPayments.find(payment => payment.fund.type === testData.authorizedPayments[0].fundType);
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

  it('TB-1125 Advance Payments for contracted projects', function () {
    cy.fixture('api/application/application.json').then(application => {
      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      });

      cy.loginByRequest(user.applicantUser.email);
      const firstPartner = application.partners[0].details.abbreviation;
      const secondPartner = application.partners[1].details.abbreviation;
      cy.createApprovedApplication(application, user.programmeUser.email).then((applicationId: number) => {
        const projectIdentifier = `${applicationId}`.padStart(5, '0');

        // Group order 1
        cy.loginByRequest(paymentsUser.email);
        cy.visit('/app/payments/advancePayments/', {failOnStatusCode: false});

        // Group order 2
        cy.contains('button', 'Add advance payment').click();
        cy.get('input[name="projectCustomIdentifier"]').type(projectIdentifier);
        cy.contains('Please select a valid project');

        // Group order 3
        cy.setProjectToContracted(applicationId, user.programmeUser.email);

        // Group order 4
        cy.visit('/app/payments/advancePayments/', {failOnStatusCode: false});
        cy.contains('button', 'Add advance payment').click();
        cy.get('input[name="projectCustomIdentifier"]').type(projectIdentifier);
        cy.wait(100);
        cy.contains('mat-option', projectIdentifier).click();
        cy.wait(100);
        cy.get('mat-select[id="partner"]').click();
        cy.wait(100);
        cy.contains('mat-option', firstPartner).should('be.visible').click();

        cy.get('mat-select[id="source"]').click();
        cy.contains('mat-option', "ERDF").should('be.visible');
        cy.contains('mat-option', "Other fund EN").should('be.visible');
        cy.contains('mat-option', "Lead contribution 1").should('be.visible');
        cy.contains('mat-option', "Lead contribution 2").should('be.visible').click();

        cy.get('mat-select[id="partner"]').click();
        cy.contains('mat-option', secondPartner).should('be.visible').click();
        cy.get('mat-select[id="source"]').click();
        cy.contains('mat-option', "Other fund EN").should('be.visible');
        cy.contains('mat-option', "Neighbourhood CBC").should('be.visible');
        cy.contains('mat-option', "Partner contribution 1").should('be.visible');
        cy.contains('mat-option', "Partner contribution 2").should('be.visible').click();

        // Group order 5
        // Payment A
        createAdvancePayment(projectIdentifier, firstPartner, 'ERDF', '1.000,00');
        authorizeAdvancePayment();
        saveAdvancePayment();

        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}/`, {failOnStatusCode: false});
        cy.contains('Advance Payments').should('not.exist');

        // Group order 6
        cy.loginByRequest(paymentsUser.email);
        cy.visit('/app/payments/advancePayments/', {failOnStatusCode: false});
        cy.contains('mat-row', projectIdentifier).find('a').click();
        confirmAdvancePayment();
        saveAdvancePayment();
        settleAdvancePayment('500,00');

        // Payment B
        createAdvancePayment(projectIdentifier, secondPartner, 'Neighbourhood CBC', '10.000,00');
        authorizeAdvancePayment();
        confirmAdvancePayment();
        saveAdvancePayment();
        settleAdvancePayment('10000,00');

        // Payment C
        createAdvancePayment(projectIdentifier, secondPartner, 'Neighbourhood CBC', '100,00');
        saveAdvancePayment();

        // Assert advance payments
        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}/advancePayments`, {failOnStatusCode: false});
        cy.contains('Advance Payments').should('be.visible');
        assertAdvancePayment('LP', '1.000,00', '500,00', '500,00');
        assertAdvancePayment('PP2', '10.000,0', '10.000,00', '0,00');

        // Group order 7
        cy.loginByRequest(user.applicantUser.email);
        cy.startModification(applicationId, user.programmeUser.email);

        updateFinancing(this[application.partners[0].details.abbreviation], application.partners[0]);
        cy.submitProjectApplication(applicationId);
        cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

        // Group order 8
        cy.loginByRequest(paymentsUser.email);
        cy.visit(`/app/payments/advancePayments/`, {failOnStatusCode: false});
        cy.wait(100);
        cy.contains('button', 'Add advance payment').click();
        cy.get('input[name="projectCustomIdentifier"]').type(projectIdentifier);
        cy.wait(100);
        cy.contains('mat-option', projectIdentifier).click();
        cy.wait(100);
        cy.get('mat-select[id="partner"]').click();
        cy.contains('mat-option', firstPartner).should('be.visible').click();

        cy.get('mat-select[id="source"]').click();
        cy.contains('mat-option', "ERDF").should('not.exist');
        cy.contains('mat-option', "Lead contribution 1").should('not.exist');
        cy.contains('mat-option', "Lead contribution 2").should('not.exist');
        cy.contains('mat-option', "Other fund EN").should('be.visible');

        // Group order 9
        cy.loginByRequest(paymentsUser.email);
        cy.visit(`/app/payments/advancePayments/`, {failOnStatusCode: false});
        getAdvancePayment(projectIdentifier, secondPartner).find('button.delete-button').should('not.exist');

        // Group order 10
        getAdvancePayment(projectIdentifier, secondPartner).find('a').click();
        cy.contains('Confirm payment').scrollIntoView().should('be.visible');
        cy.get('mat-checkbox[id="confirm"] input').should('be.disabled');

        // Group order 11
        cy.contains('button', 'delete').should('be.enabled').click();
        saveAdvancePayment()

        cy.get('mat-checkbox[id="confirm"]').click();
        saveAdvancePayment();
        cy.wait(1000);

        cy.visit(`/app/payments/advancePayments/`, {failOnStatusCode: false});
        cy.contains('mat-row', '10.000,00').find('button.delete-button').should('not.exist');

        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}/advancePayments`, {failOnStatusCode: false});
        cy.contains('mat-row', secondPartner).should('not.exist');

        // Group order 12
        cy.loginByRequest(paymentsUser.email);
        cy.visit(`/app/payments/advancePayments/`, {failOnStatusCode: false});
        getAdvancePayment(projectIdentifier, secondPartner).find('button.delete-button').should('not.exist');
        getAdvancePayment(projectIdentifier, secondPartner).find('a').click();

        cy.contains('Authorise payment').scrollIntoView().should('be.visible');
        cy.get('mat-checkbox[id="authorize"]').click();
        saveAdvancePayment()

        cy.visit(`/app/payments/advancePayments/`, {failOnStatusCode: false});
        cy.contains('Delete').scrollIntoView().should('be.visible');
        getAdvancePayment(projectIdentifier, secondPartner).find('button.delete-button').should('be.visible').click({force: true});
        cy.contains('button', 'Confirm').click();
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
  expect(row.children('.mat-column-payments-payment-to-project-table-column-payment-type').get(0)).to.contain(type);
}

function assertPaymentProjectId(row, id) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-project-id').get(0)).to.contain(id);
}

function assertProjectAcronym(row, acronym) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-project-acronym').get(0)).to.contain(acronym);
}

function assertClaimNo(row, claimNo) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-payment-claim-no').get(0)).to.contain(claimNo);
}

function assertSubmissionDate(row, submissionDate) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-payment-claim-submission-date').get(0)).to.contain(submissionDate);
}

function assertMAApprovalDate(row, maApprovalDate) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-payment-claim-approval-date').get(0)).to.contain(maApprovalDate);
}

function assertTotalApproved(row, totalApproved) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-total-eligible-amount').get(0)).to.contain(totalApproved);
}

function assertFund(row, fund) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-fund').get(0)).to.contain(fund);
}

function assertApprovedPerFund(row, approvedPerFund) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-amount-approved-per-fund').get(0)).to.contain(approvedPerFund);
}

function assertAuthorised(row, authorised) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-authorised').get(0)).to.contain(authorised);
}

function assertPaid(row, paid) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-amount-paid-per-fund').get(0)).to.contain(paid);
}

function assertRemainingToBePaid(row, remainingToBePaid) {
  expect(row.children('.mat-column-payments-payment-to-project-table-column-remaining-to-be-paid').get(0)).to.contain(remainingToBePaid);
}

function createAdvancePayment(projectIdentifier: string, partner: string, fund: string, amount: string) {
  cy.loginByRequest(paymentsUser.email);
  cy.visit('/app/payments/advancePayments/', {failOnStatusCode: false});
  cy.contains('button', 'Add advance payment').click();
  cy.get('input[name="projectCustomIdentifier"]').type(projectIdentifier);
  cy.wait(100);
  cy.contains('mat-option', projectIdentifier).click();
  cy.wait(100);
  cy.get('mat-select[id="partner"]').click();
  cy.wait(100);
  cy.contains('mat-option', partner).click();
  cy.get('mat-select[id="source"]').click();
  cy.contains('mat-option', fund).click();
  cy.get('input[name="paid"]').type(amount);
}

function authorizeAdvancePayment() {
  cy.contains('Authorise payment').scrollIntoView().should('be.visible');
  cy.get('mat-checkbox[id="authorize"] input').check({force: true});
}

function confirmAdvancePayment() {
  cy.contains('Date of advance payment').scrollIntoView().should('be.visible');
  cy.get('mat-datepicker-toggle button').click();
  cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();
  cy.contains('Confirm payment').scrollIntoView().should('be.visible');
  cy.get('mat-checkbox[id="confirm"] input').check({force: true});
}

function settleAdvancePayment(amount: string) {
  cy.contains('Add Settlement').click();
  cy.get('input[name="amountSettled"]').type(amount);
  cy.get('mat-datepicker-toggle button').last().click();
  cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();
  saveAdvancePayment();
}

function assertAdvancePayment(partner: string, amount: string, settled: string, remaining: string) {
  cy.contains('mat-row', partner).within(() => {
    cy.get('mat-cell.mat-column-payments-advance-payment-table-header-advance-amount').should('contain', amount);
    cy.get('mat-cell.mat-column-payments-advance-payment-table-header-amount-settled').should('contain', settled);
    cy.get('mat-cell.mat-column-payments-advance-payment-table-header-remaining-amount-to-be-settled').should('contain', remaining);
  });
}

function updateFinancing(partnerId: number, partner: any) {
  console.dir(partner.cofinancing);
  partner.cofinancing.finances.shift();
  partner.cofinancing.finances[0].percentage = 74.66;
  partner.cofinancing.partnerContributions.pop();
  partner.cofinancing.partnerContributions.pop();
  partner.cofinancing.partnerContributions[0].amount = 15573.57;
  cy.updatePartnerData(partnerId, partner);
}

function getAdvancePayment(projectIdentifier: string, partner: string) {
  return cy.get(`mat-row:contains(${projectIdentifier}):contains(${partner})`).last();
}

function saveAdvancePayment() {
  cy.intercept('PUT', `/api/advancePayment`).as('submitAdvancePayment');
  cy.contains('Save changes').should('be.enabled').click();
  cy.wait('@submitAdvancePayment');
}
