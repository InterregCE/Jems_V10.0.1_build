import {faker} from '@faker-js/faker';
import user from '../../fixtures/users.json';

context('SPF Call Management tests', () => {

  beforeEach(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.visit('app/call', {failOnStatusCode: false});
  });

  it('TB-382 Create SPF call', function() {
    cy.contains('Button', 'Add SPF call').should('be.visible');
    cy.contains('Button', 'Add SPF call').click();
    cy.contains('h1.ng-star-inserted', 'Small project funds call overview').should('be.visible');
    cy.get('input.mat-input-element:first').should('not.have.attr', 'readonly');

    // creating the call
    cy.contains('div', 'Call name').find('input').type(faker.internet.domainWord());
    cy.contains('mat-form-field', 'Start date').find('button').click();
    cy.contains('div.mat-calendar-body-cell-content', '5').click();
    cy.contains('mat-icon', 'done').click();
    cy.contains('mat-form-field', 'End date (MM').find('button').click();
    cy.contains('div.mat-calendar-body-cell-content', '7').click();
    cy.contains('mat-icon', 'done').click();
    cy.contains('div', 'Period length (in months)').find('input').type(String(Math.round((Math.random()+1)*8)));
    cy.contains('mat-checkbox', 'ERDF').click();
    cy.contains('button', 'Create').click();

    cy.contains('button', 'Publish call').should('be.disabled');
    cy.contains('You cannot publish call because you did not set up pre-submission check plugin').should('be.visible');
    cy.contains('div', 'Call name').find('input').should('not.have.attr', 'readonly');
    for (const navItem of ['General call settings', 'Application form configuration', 'Budget Settings', 'Pre-submission check settings']) {
      cy.contains('div', navItem).should('be.visible');
    }
    cy.contains('div', 'Budget Settings').click();
    cy.contains('mat-card-content', 'Allow Real Costs for cost categories').find('span.mat-checkbox-inner-container').should('be.visible');
    cy.contains('mat-card-content', 'Allow Real Costs for cost categories').find('input[type="checkbox"]').invoke('attr', 'aria-checked').should('eq', 'true');
    for (const matCardContents of ["Flat Rates", "Lump Sums", "Unit Costs"]) {
      cy.contains('h3', matCardContents).scrollIntoView().should('be.visible');
      cy.contains('mat-card-content', matCardContents).find('input[type="checkbox"]').invoke('attr', 'aria-checked').should('eq', 'false');
    }

    cy.contains('div', 'Pre-submission check settings').click();
    cy.contains('span', 'Select a pre-submission check plugin').should('be.visible');

    cy.contains('div', 'Application form configuration').click();
    cy.get('tr:contains("Project partnership")').last().find('input[type="checkbox"]').invoke('attr', 'aria-checked').should('eq', 'false');
    cy.contains('tr', 'Cooperation criteria').find('input[type="checkbox"]').invoke('attr', 'aria-checked').should('eq', 'false');

    for (const option of ['Project communication approach', 'Ownership', 'Durability', 'Transferability']) {
      cy.contains('tr', option).find('input[type="checkbox"]').invoke('attr', 'aria-checked').should('eq', 'true');
      cy.contains('tr', option).find('input[type="checkbox"]').should('not.have.attr', 'disabled');
    }
  });
});
