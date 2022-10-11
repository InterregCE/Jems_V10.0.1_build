import user from "../../../../fixtures/users.json";
import call from "../../../../fixtures/api/call/1.step.call.json";
import application from "../../../../fixtures/api/application/application.json";

context('Draft budget tests', () => {
  it('TB-672 Create project proposed lump sums', () => {
    cy.fixture('project/application-form/d-project-budget/TB-672.json').then(testData => {

      cy.loginByRequest(user.programmeUser.email);
      call.generalCallSettings.additionalFundAllowed = true;
      call.budgetSettings.allowedCostOption.projectDefinedUnitCostAllowed = true;
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      });

      cy.loginByRequest(user.applicantUser.email);
      cy.createSubmittedApplication(application).then(applicationId => {
        cy.returnToApplicant(applicationId, user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});

        testData.draftUnitCosts.forEach(cost => {

          cy.contains('E.2.1 - Project proposed unit costs').click();
          cy.contains('Add Project Proposed Unit Cost').click();

          cy.contains('jems-multi-language-container', 'Name of the Unit Cost').within(() => {
            cost.name.forEach(item => {
              cy.contains('button', item.language).click();
              cy.get('input').type(item.translation);
            });
          });

          cy.contains('jems-multi-language-container', 'Description of the Unit Cost').within(() => {
            cost.description.forEach(item => {
              cy.contains('button', item.language).click();
              cy.get('input').type(item.translation);
            });
          });

          cy.contains('jems-multi-language-container', 'Unit type').within(() => {
            cost.type.forEach(item => {
              cy.contains('button', item.language).click();
              cy.get('input').type(item.translation);
            });
          });

          cy.contains('jems-multi-language-container', 'Justification and calculation').within(() => {
            cost.justification.forEach(item => {
              cy.contains('button', item.language).click();
              cy.get('textarea').type(item.translation);
            });
          });

          cy.contains('div', 'Cost per unit (in Euro)').find('input').type(cost.costPerUnit);
          cy.contains('mat-form-field', 'Currency selector').click();
          cy.contains('mat-option', cost.foreignCurrencyCode).click();
          cy.contains('div', 'Cost per unit (other currency)').find('input').type(cost.costPerUnitForeignCurrency);

          cy.contains(cost.costCategory).click();
          cost.categories.forEach(category => {
            cy.contains('label', category).click();
          });

          cy.contains('Save changes').click();
          cy.contains('mat-icon', 'arrow_circle_left').should('be.visible');
        });
        
        cy.get(`@${application.partners[0].details.abbreviation}`).then(function (partnerId: any) {
          cy.visit(`/app/project/detail/${applicationId}/applicationFormPartner/${partnerId}/budget`, {failOnStatusCode: false});

          cy.contains('h4', 'Infrastructure and works').should('exist').next().within(() => {
            cy.contains('add').click();
            cy.get('mat-row').last().within(() => {
              cy.contains('mat-select', 'N/A').click();
              cy.root().closest('body').contains('mat-option', testData.infrastructureCostItem.name).click();

              testData.infrastructureCostItem.comment.forEach(value => {
                cy.root().closest('body').contains('button', value.language).click();
                cy.get('input').eq(0).type(value.translation, {force: true});
              });

              testData.infrastructureCostItem.awardProcedures.forEach(value => {
                cy.root().closest('body').contains('button', value.language).click();
                cy.get('input').eq(1).type(value.translation, {force: true});
              });

              cy.get('input').eq(2).type(testData.infrastructureCostItem.numberOfUnits, {force: true});
              cy.get('input').eq(4).type(testData.infrastructureCostItem.period1Amount, {force: true});
            });
          });

          cy.contains('Unit costs covering more than one cost category').next().within(() => {

            cy.contains('Add').click();
            cy.get('mat-select').click();
            cy.root().closest('body').contains('mat-option', testData.unitCostItem.name).click();
            cy.get('input').eq(0).type(testData.unitCostItem.numberOfUnits, {force: true});
            cy.get('input').eq(1).type(testData.unitCostItem.period1Amount, {force: true});
          });

          cy.contains('Save changes').click();
          cy.contains('Partner budgets were saved successfully').should('be.visible');

          cy.updatePartnerCofinancing(partnerId, testData.updatedCofinancing);
          cy.runPreSubmissionCheck(applicationId).then(response => {
            expect(response.body.submissionAllowed).to.be.true;
          });
          cy.submitProjectApplication(applicationId).then(response => {
            expect(response.body).to.eq('SUBMITTED');
          });
        });
      });
    });
  });
})