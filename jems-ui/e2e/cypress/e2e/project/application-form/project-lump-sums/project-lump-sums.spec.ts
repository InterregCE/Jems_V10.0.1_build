import user from '../../../../fixtures/users.json';
import application from '../../../../fixtures/api/application/application.json';
import call from "../../../../fixtures/api/call/1.step.call.json";
import {faker} from "@faker-js/faker";

context('Project lump sums tests', () => {
  it('TB-734 Project lump sums can be defined and allocated to project partners', () => {
    cy.fixture("project/application-form/project-lump-sums/TB-734").then(testData => {
      cy.loginByRequest(user.programmeUser.email);
      call.budgetSettings.lumpSums.push(3);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      })
      cy.loginByRequest(user.applicantUser.email);
      cy.createApplication(application).then(applicationId => {
        cy.visit('/app/project/detail/'+applicationId+'/applicationFormPartner', {failOnStatusCode: false});
        cy.contains('Add new partner').click();
        cy.contains('button', 'Partner').click();
        cy.contains('div.mat-form-field-flex', 'Abbreviated name of the organisation').type(faker.word.noun());
        cy.contains('div.mat-form-field-flex', 'Legal status').click();
        cy.contains('Public').click();
        cy.contains('button', 'Create').click();
        cy.visit('/app/project/detail/'+applicationId+'/applicationFormPartner', {failOnStatusCode: false});
        cy.contains('Add new partner').click();
        cy.contains('button', 'Partner').click();
        cy.contains('div.mat-form-field-flex', 'Abbreviated name of the organisation').type(faker.word.noun());
        cy.contains('div.mat-form-field-flex', 'Legal status').click();
        cy.contains('Public').click();
        cy.intercept('/api/programmeLegalStatus').as('PartnerCreation');
        cy.contains('button', 'Create').click();
        cy.wait(1000);

        cy.contains('E.1 - Project lump sums').click();
        cy.contains('button', 'Add').click();
        cy.contains('div.mat-form-field-flex', 'Lump sum').click();
        cy.contains(testData.firstLumpSum.name).click();
        cy.get('input[type="decimal"]').first().clear().type(testData.firstLumpSum.firstPartnerSplitAmount);
        cy.get('input[type="decimal"]').last().clear().type(testData.firstLumpSum.secondPartnerAmount);
        cy.contains('Total lump sum costs must be allocated to one partner').should('be.visible');
        cy.get('input[type="decimal"]').last().clear();
        cy.get('input[type="decimal"]').first().clear().type(testData.firstLumpSum.firstPartnerTooFewAmount);
        cy.contains('Please update the lump sum table: The sum of the amounts per partner must match the total lump sum costs.').scrollIntoView().should('be.visible');
        cy.get('input[type="decimal"]').first().clear().type(testData.firstLumpSum.firstPartnerCorrectAmount);
        cy.contains('button', 'Save changes').click();
        cy.contains("Project's LumpSums saved successfully").should('be.visible');
        cy.contains('button', 'add').click();
        cy.get('mat-row').last().contains('span.mat-select-placeholder', 'Lump sum').click();
        cy.contains(testData.secondLumpSum.name).click();
        cy.get('mat-row').last().find('input[type="decimal"]').first().clear().type(testData.secondLumpSum.firstPartnerAmount);
        cy.get('mat-row').last().find('input[type="decimal"]').last().clear().type(testData.secondLumpSum.secondPartnerAmount);
        cy.contains('button', 'Save changes').click();
        cy.contains("Project's LumpSums saved successfully").should('be.visible');
      });
    })
  })
});
