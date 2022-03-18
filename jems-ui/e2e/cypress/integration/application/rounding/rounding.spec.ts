import user from '../../../fixtures/users.json';
import call from '../../../fixtures/api/call/1.step.call.json';
import testData from '../../../fixtures/application/rounding/TB-383.json';

context('Rounding', () => {

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.applicantUser);
  });

  it('TB-383 Rounding values in partner co-financing', function () {
    call.generalCallSettings.additionalFundAllowed = false;
    cy.createCall(call).then(callId => {
      call.generalCallSettings.id = callId;
      testData.application.details.projectCallId = callId;
      cy.publishCall(callId);

      cy.createApplication(testData.application, user.applicantUser.email).then(applicationId => {
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

          cy.get('mat-select[ng-reflect-placeholder="Legal status"]').click();
          cy.contains('mat-option', testData.contributionStatus).click();
          cy.get('input[ng-reflect-name="amount"]').type(testData.contributionAmount);

          cy.contains('button', 'Save changes').click();

          // verify A.3 section
          cy.visit(`app/project/detail/${applicationId}/applicationFormOverviewTables`, {failOnStatusCode: false});
          cy.get('jems-alert').should('be.visible');

          cy.get('table tbody tr').eq(1).then(budgetBreakdown => {
            cy.wrap(budgetBreakdown).children().eq(1).should('contain', testData.roundedDownAmount);
            cy.wrap(budgetBreakdown).children().eq(2).should('contain', testData.fundPercentage);
            cy.wrap(budgetBreakdown).children().eq(6).should('contain', testData.roundedUpAmount);
            cy.wrap(budgetBreakdown).children().eq(8).should('contain', testData.partnerTotalEligibleBudget);
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
        });
      });
    });
  });
});
