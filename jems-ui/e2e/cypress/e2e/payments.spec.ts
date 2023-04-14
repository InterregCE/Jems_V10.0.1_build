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

  it('TB-775 Payment section population with FTLS info', function() {
    cy.fixture('payments/TB-775.json').then(testData => {
      cy.fixture('api/call/1.step.call.json').then(call => {
        cy.fixture('api/application/application.json').then(application => {
          
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
                  application.details.projectCallId = callId;
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
                    cy.log(testData.projectLumpSums);

                    cy.loginByRequest(paymentsUser.email);
                    cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});


                    setReadyForPayment(true, 1);

                    cy.contains('Payments').click();
                    cy.get('table mat-row').then(row => {
                      expect(row).has.length.of.at.least(2);
                      expect(row.get(0).childNodes[1]).to.contain('FTLS');
                      expect(row.get(0).childNodes[2]).to.contain(applicationId);
                      expect(row.get(0).childNodes[3]).to.contain(application.identification.acronym);
                      expect(row.get(0).childNodes[5]).to.contain(date.format(new Date, 'MM/DD/YYYY'));
                      expect(row.get(0).childNodes[6]).to.contain(date.format(new Date, 'MM/DD/YYYY'));
                      expect(row.get(0).childNodes[7]).to.contain('1.999,00');
                      expect(row.get(0).childNodes[8]).to.contain('OTHER');
                      expect(row.get(0).childNodes[9]).to.contain('293,05');
                      expect(row.get(0).childNodes[10]).to.contain('0,00');
                      expect(row.get(0).childNodes[12]).to.contain('293,05');

                      expect(row.get(1).childNodes[2]).to.contain(applicationId);
                      expect(row.get(1).childNodes[3]).to.contain(application.identification.acronym);
                      expect(row.get(1).childNodes[7]).to.contain('1.999,00');
                      expect(row.get(1).childNodes[8]).to.contain('ERDF');
                      expect(row.get(1).childNodes[9]).to.contain('1.199,40');
                      expect(row.get(1).childNodes[10]).to.contain('0,00');
                      expect(row.get(1).childNodes[12]).to.contain('1.199,40');
                    });

                    cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
                    setReadyForPayment(false, 1);
                    setReadyForPayment(true, 2);

                    cy.contains('Payments').click();
                    cy.get('table mat-row').then(row => {
                      expect(row).has.length.of.at.least(3);
                      expect(row.get(0).childNodes[2]).to.contain(applicationId);

                      expect(row.get(0).childNodes[8]).to.contain('OTHER');
                      expect(row.get(0).childNodes[9]).to.contain('296,45');
                      expect(row.get(0).childNodes[12]).to.contain('296,45');

                      expect(row.get(1).childNodes[8]).to.contain('NEIGHBOURHOOD_CBC');
                      expect(row.get(1).childNodes[9]).to.contain('249,75');
                      expect(row.get(1).childNodes[12]).to.contain('249,75');

                      expect(row.get(2).childNodes[8]).to.contain('ERDF');
                      expect(row.get(2).childNodes[9]).to.contain('600,00');
                      expect(row.get(2).childNodes[12]).to.contain('600,00');
                    });

                    cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
                    setReadyForPayment(false, 2);
                    setReadyForPayment(true, 3);

                    cy.contains('Payments').click();
                    cy.get('table mat-row').then(row => {
                      expect(row).has.length.of.at.least(2);
                      expect(row.get(0).childNodes[2]).to.contain(applicationId);

                      expect(row.get(0).childNodes[8]).to.contain('OTHER');
                      expect(row.get(0).childNodes[9]).to.contain('97,49');
                      expect(row.get(0).childNodes[12]).to.contain('97,49');

                      expect(row.get(1).childNodes[8]).to.contain('NEIGHBOURHOOD_CBC');
                      expect(row.get(1).childNodes[9]).to.contain('162,49');
                      expect(row.get(1).childNodes[12]).to.contain('162,49');
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

                    cy.get('table mat-row').then(row => {
                      expect(row).has.length.of.at.least(2);
                      expect(row.get(0).childNodes[2]).to.contain(applicationId);

                      expect(row.get(0).childNodes[8]).to.contain('OTHER');
                      expect(row.get(0).childNodes[9]).to.contain('97,49');
                      expect(row.get(0).childNodes[12]).to.contain('97,49');

                      expect(row.get(1).childNodes[8]).to.contain('NEIGHBOURHOOD_CBC');
                      expect(row.get(1).childNodes[9]).to.contain('162,49');
                      expect(row.get(1).childNodes[12]).to.contain('162,49');
                    });

                    cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
                    setReadyForPayment(false, 3);
                    setReadyForPayment(true, 2);

                    cy.contains('Payments').click();
                    cy.get('table mat-row').then(row => {
                      expect(row).has.length.of.at.least(3);
                      expect(row.get(0).childNodes[2]).to.contain(applicationId);
                      expect(row.get(0).childNodes[7]).to.contain('1.999,00');
                      expect(row.get(0).childNodes[8]).to.contain('OTHER');
                      expect(row.get(0).childNodes[9]).to.contain('49,95');
                      expect(row.get(0).childNodes[12]).to.contain('49,95');

                      expect(row.get(1).childNodes[7]).to.contain('1.999,00');
                      expect(row.get(1).childNodes[8]).to.contain('OTHER');
                      expect(row.get(1).childNodes[9]).to.contain('146,60');
                      expect(row.get(1).childNodes[12]).to.contain('146,60');

                      expect(row.get(2).childNodes[8]).to.contain('NEIGHBOURHOOD_CBC');
                      expect(row.get(2).childNodes[9]).to.contain('299,70');
                      expect(row.get(2).childNodes[12]).to.contain('299,70');

                      expect(row.get(3).childNodes[8]).to.contain('ERDF');
                      expect(row.get(3).childNodes[9]).to.contain('749,85');
                      expect(row.get(3).childNodes[12]).to.contain('749,85');
                    });
                  });
                });
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