import user from '../../../../../fixtures/users.json';
import call from '../../../../../fixtures/api/call/1.step.call.json';
import testData from '../../../../../fixtures/project/application-form/project-budget/rounding/TB-383.json';

context('Rounding', () => {

  beforeEach(() => {
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-383 Rounding values in partner co-financing', () => {
    call.generalCallSettings.additionalFundAllowed = false;
    cy.createCall(call, user.programmeUser.email).then(callId => {
      testData.application.details.projectCallId = callId;
      cy.publishCall(callId, user.programmeUser.email);

      cy.createApplication(testData.application).then(applicationId => {
        cy.updateProjectIdentification(applicationId, testData.application.identification);
        const projectPartner = testData.application.partners[0];
        cy.createPartner(applicationId, projectPartner.details).then(partnerId => {
          cy.addPartnerTravelCosts(partnerId, projectPartner.budget.travel);

          cy.visit(`app/project/detail/${applicationId}/applicationFormPartner/${partnerId}/coFinancing`, {failOnStatusCode: false});
          cy.get('jems-project-partner-co-financing-tab').should('exist');

          cy.contains('div.jems-table-config', 'Source').then(cofinancingSection => {
            cy.wrap(cofinancingSection).find('mat-select').click();
            cy.contains('mat-option', testData.fundSource).click();
            cy.wrap(cofinancingSection).find('input').type(testData.fundPercentage.toString());

            cy.wrap(cofinancingSection).children().eq(1).children('div').eq(0).should('contain', testData.roundedDownAmount);
            cy.wrap(cofinancingSection).children().eq(2).children('div').eq(1).should('contain', testData.roundedUpAmount);
            cy.wrap(cofinancingSection).children().eq(2).children('div').eq(2).should('contain', testData.percentageDifference);
            cy.wrap(cofinancingSection).children().eq(3).children('div').eq(1).should('contain', testData.partnerTotalEligibleBudget);
          });

          cy.contains('div.jems-table-config', 'Source of contribution').then(partnerContributionSection => {
            cy.wrap(partnerContributionSection).find('mat-select').click();
            cy.contains('mat-option', testData.contributionStatus).click();
            cy.wrap(partnerContributionSection).find('input').type(testData.contributionAmount);
          });

          cy.contains('button', 'Save changes').click();

          // verify A.3 section
          cy.visit(`app/project/detail/${applicationId}/applicationFormOverviewTables`, {failOnStatusCode: false});
          cy.get('jems-alert').should('be.visible');

          cy.contains('tr', 'Neighbourhood CBC').then(budgetBreakdown => {
            cy.wrap(budgetBreakdown).children().eq(1).should('contain', testData.roundedDownAmount);
            cy.wrap(budgetBreakdown).children().eq(2).should('contain', testData.fundPercentage);
            cy.wrap(budgetBreakdown).children().eq(6).should('contain', testData.roundedUpAmount);
            cy.wrap(budgetBreakdown).children().eq(8).should('contain', testData.partnerTotalEligibleBudget);
          });

          cy.contains('tr', 'Total project budget').then(totalBudgetBreakdown => {
            cy.wrap(totalBudgetBreakdown).children().eq(1).should('contain', testData.roundedDownAmount);
            cy.wrap(totalBudgetBreakdown).children().eq(2).should('contain', testData.fundPercentage);
            cy.wrap(totalBudgetBreakdown).children().eq(6).should('contain', testData.roundedUpAmount);
            cy.wrap(totalBudgetBreakdown).children().eq(8).should('contain', testData.partnerTotalEligibleBudget);
          });

          // verify D.1 section
          cy.visit(`app/project/detail/${applicationId}/applicationFormBudgetPerPartner`, {failOnStatusCode: false});
          cy.contains('100 % of total').should('be.visible');
          cy.get('div.jems-table-config').children().eq(1).then(partnerBreakdown => {
            cy.wrap(partnerBreakdown).children().eq(4).should('contain', testData.roundedDownAmount);
            cy.wrap(partnerBreakdown).children().eq(5).should('contain', testData.fundPercentage);
            cy.wrap(partnerBreakdown).children().eq(8).should('contain', testData.roundedUpAmount);
            cy.wrap(partnerBreakdown).children().eq(10).should('contain', testData.partnerTotalEligibleBudget);
          });

          // verify PDF export
          cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});
          cy.contains('button', 'Export').clickToDownload('**/export/application?*', 'pdf').then(exportFile => {
            cy.fixture('project/application-form/project-budget/rounding/TB-383-export-pdf.txt').then(testDataFile => {
              const assertionMessage = 'Verify downloaded pdf file';
              expect(exportFile.text.includes(testDataFile), assertionMessage).to.be.true;
            });
          });

          // verify CSV export
          cy.contains('button', 'Partners budget').click();
          cy.contains('button', 'Export').clickToDownload('**/export/budget?*', 'csv').then(exportFile => {
            cy.fixture('project/application-form/project-budget/rounding/TB-383-export.csv').parseCSV().then(testDataFile => {
                const assertionMessage = 'Verify downloaded csv file';
                expect(exportFile.content.slice(1), assertionMessage).to.deep.equal(testDataFile);
            });
          });
        });
      });
    });
  });
});
