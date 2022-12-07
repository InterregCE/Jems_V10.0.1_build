import {faker} from '@faker-js/faker';
import user from '../../fixtures/users.json';

context('Call management tests', () => {

  beforeEach(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.visit('app/call', {failOnStatusCode: false});
  });

  context('Standard 1-step call', () => {

    before(() => {
      cy.wrap(`Standard 1-step call ${faker.datatype.uuid()}`).as('callName');
    });

    it('TB-388 Create a new 1-step call', function () {
      cy.contains('Add new call').click();

      // Call identification
      cy.contains('div', 'Call name').find('input').type(this.callName);

      cy.contains('div', 'Start date').next().click();
      cy.contains('mat-icon', 'done').click();

      cy.contains('div', 'End date (').next().click();
      cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();
      cy.get('mat-icon').contains('done').click();

      cy.contains('div', 'Period length').find('input').type('3');
      cy.contains('div', 'Description').find('textarea').type('Automated call description');

      // Programme Priorities
      const priority = 'Developing and enhancing research and innovation capacities and the uptake of advanced technologies';
      cy.contains(priority).click();

      // Strategies
      const strategy = 'EU Strategy for the Adriatic and Ionian Region';
      cy.contains(strategy).click();

      // Funds
      // Fund options
      cy.contains('mat-checkbox', 'Allow multiple funds per partner in application form').click();

      // Available funds
      cy.contains('div', 'ERDF').within(() => {
        cy.get('mat-checkbox input').check({force: true});
      });
      cy.contains('div', 'Neighbourhood CBC').within(() => {
        cy.get('mat-checkbox input').check({force: true});
        cy.get('input[name="fundRateValue"]').type('50,00');
        cy.contains('Up To').click();
      });

      // State aid
      cy.get('jems-call-state-aids').contains('General de minimis').click();

      cy.contains('button', 'Create').click();
      cy.contains('span', 'General call settings').should('be.visible');
      cy.contains('button', 'Publish call').should('be.visible').and('be.disabled');
    });


    it('TB-389 Edit and publish 1-step call', function () {
      cy.contains(this.callName).click();
      cy.get('input[name="name"]').should('have.value', this.callName);

      // Budget settings
      cy.get('jems-side-nav span.title').contains('Budget Settings').click();

      // Flat Rates
      const flatRate1 = 'Staff cost flat rate based on direct cost';
      cy.contains('div', flatRate1).within(() => {
        cy.get('mat-checkbox input').check({force: true});
        cy.get('input[name="staffCostFlatRate"]').type('19');
      });

      const flatRate2 = 'Office and administrative costs flat rate based on direct staff cost';
      cy.contains('div', flatRate2).within(() => {
        cy.get('mat-checkbox input').check({force: true});
        cy.get('input[name="officeOnStaffFlatRate"]').type('14');
        cy.contains('button', 'Up To').click();
      });

      cy.contains('button', 'Save changes').click();
      cy.contains('Flat rates updated successfully').should('be.visible');
      cy.contains('Flat rates updated successfully').should('not.exist');

      // Lump Sums
      const lumpSum = 'Preparation Lump sum DE';
      cy.contains('div', lumpSum).find('mat-checkbox input').check({force: true});
      cy.contains('button', 'Save changes').click();
      cy.contains('Lump sums updated successfully').should('be.visible');
      cy.contains('Lump sums updated successfully').should('not.exist');

      // Unit Costs
      const unitCost = 'Unit cost single - Staff DE';
      cy.contains(unitCost).parent().find('mat-checkbox input').check({force: true});
      cy.contains('button', 'Save changes').click();
      cy.contains('Unit costs updated successfully').should('be.visible');
      cy.contains('Unit costs updated successfully').should('not.exist');

      // Draft budget
      cy.contains('mat-checkbox', 'Allow Project proposed unit costs').find('input').check({force: true});
      cy.contains('button', 'Save changes').click();
      cy.contains('Allow Draft Budget options were saved successfully').should('be.visible');
      cy.contains('Allow Draft Budget options were saved successfully').should('not.exist');

      // Pre-submission check
      cy.contains('Pre-submission check settings').click();
      cy.contains('mat-select', 'Select a pre-submission check plugin').click();
      cy.contains('mat-option', 'Standard pre condition check').click();
      cy.contains('div', 'Partner report pre-submission check plugin').find('mat-select').click();
      cy.contains('mat-option', 'Report (Example) Check').click();
      cy.contains('Save changes').click();
      cy.contains('Application form configuration was saved successfully').should('be.visible');
      cy.contains('Application form configuration was saved successfully').should('not.exist');

      // Publish call
      cy.contains('General call settings').click();
      cy.contains('Publish call').click();
      cy.contains('button', 'Confirm').click();

      cy.contains(`Successfully published Call ${this.callName}`).should('be.visible');
    });
  });

  context('Standard 2-step call', () => {

    before(() => {
      cy.wrap(`Standard 2-step call ${faker.datatype.uuid()}`).as('callName');
    });

    it("TB-754 Create a new 2-step call", function () {
      cy.contains('button', 'Add new call').click();
      
      cy.contains('div', 'Call name').find('input').type(this.callName);
      cy.contains('div', 'Period length').find('input').type('3');
      
      // Date validations
      cy.contains('mat-checkbox', 'Use 2-step application form procedure for this call').find('input').check({force: true});
      cy.contains('mat-form-field', 'Start date (MM/DD/YYYY h:mm A)').find('button').click();
      cy.contains('td', '2').click();
      cy.contains('button', 'done').click();
      
      cy.contains('mat-form-field', 'End date (MM/DD/YYYY h:mm A)').find('button').click();
      cy.contains('td', '10').click({force: true});
      cy.contains('button', 'done').click();
      
      cy.contains('mat-form-field', 'End date Step 1 (MM/DD/YYYY h:mm A)').find('button').click();
      cy.contains('td', '11').should('have.attr', 'aria-disabled', 'true');
      cy.contains('td', '1').should('have.attr', 'aria-disabled', 'true');
      cy.contains('td', '5').click({force: true});
      cy.contains('button', 'done').click();
      
      cy.contains('mat-form-field', 'Start date (MM/DD/YYYY h:mm A)').find('button').click();
      cy.get('button[aria-label="Previous Month"]').click();
      cy.contains('td', '1').click({force: true});
      cy.contains('button', 'done').click();
      
      cy.contains('mat-form-field', 'End date (MM/DD/YYYY h:mm A)').find('button').click();
      cy.get('button[aria-label="Next Month"]').click();
      cy.get('button[aria-label="Next Month"]').click();
      cy.contains('td', '20').click({force: true});
      cy.contains('button', 'done').click();
      
      cy.contains('mat-form-field', 'End date Step 1 (MM/DD/YYYY h:mm A)').find('button').click();
      cy.get('button[aria-label="Next Month"]').click();
      cy.contains('td', '20').click({force: true});
      cy.contains('button', 'done').click();

      // Priorities
      cy.contains('Promoting energy efficiency and reducing greenhouse gas emissions').click();
      cy.contains('Promoting access to water and sustainable water management').click();

      // Strategies
      cy.contains('EU Strategy for the Alpine Region').click();
      cy.contains('EU Strategy for the Baltic Sea Region').click();
      
      // Fund
      cy.contains('div', 'ERDF').within(() => {
        cy.get('mat-checkbox input').check({force: true});
      });
      
      cy.contains('button', 'Create').click();
      cy.contains('span', 'General call settings').should('be.visible');
      cy.contains('button', 'Publish call').should('be.visible').and('be.disabled');
      cy.contains('You cannot publish call because you did not set up pre-submission check plugin').should('be.visible');
    });

    it("TB-876 Edit and publish a 2-step call", function () {
      cy.contains(this.callName).click();
      cy.get('input[name="name"]').should('have.value', this.callName);
      
      cy.contains('Application form configuration').click();
      cy.contains('button', '1 & 2').should('be.visible');
      cy.contains('button', '2 only').should('be.visible');
      
      cy.get('input.mat-slide-toggle-input').should('have.attr', 'disabled');
      cy.contains('tr', 'Project title').contains('button', '1 & 2').click();
      cy.contains('button', 'Save changes').click();
      cy.contains('Application form configuration was saved successfully.').should('be.visible');
      
      cy.contains('Pre-submission check settings').click();
      cy.get('jems-form mat-select').should('have.length', 3);
      
      
      cy.contains('div', 'Select a pre-submission check plugin for step one').click();
      cy.contains('mat-option', 'No-Check').should('be.visible');
      cy.contains('mat-option', 'Blocked').should('be.visible');
      cy.contains('mat-option', 'Standard pre condition check').should('be.visible').click();
      
      cy.contains('div', 'Select a pre-submission check plugin').click();
      cy.contains('mat-option', 'No-Check').should('be.visible');
      cy.contains('mat-option', 'Blocked').should('be.visible');
      cy.contains('mat-option', 'Standard pre condition check').should('be.visible').click();
      
      cy.contains('div', 'Partner report pre-submission check plugin').click();
      cy.contains('mat-option', 'No-Check').should('be.visible');
      cy.contains('mat-option', 'Blocked').should('be.visible');
      cy.contains('mat-option', 'Report (Example) Check').should('be.visible').click();
      
      cy.contains('button', 'Save changes').click();
      cy.contains('Application form configuration was saved successfully.').should('be.visible');
      cy.contains('Application form configuration was saved successfully.').should('not.exist');
      
      cy.contains('General call settings').click();
      cy.contains('button', 'Publish call').should('be.visible').click();
      cy.contains('button', 'Confirm').should('be.visible').click();
      cy.contains(`Successfully published Call ${this.callName}`).should('be.visible');
      cy.contains(`Successfully published Call ${this.callName}`).should('not.exist');

      cy.loginByRequest(user.applicantUser.email);
      cy.visit('/');

      cy.contains('mat-row', this.callName).scrollIntoView().contains('button', 'Apply').click();
      cy.contains('div.mat-form-field-flex', 'Project acronym').type(faker.word.noun());
      cy.contains('button', 'Create project application').click();
      cy.contains('div.mat-form-field-flex', 'Project title').should('be.visible');
      cy.contains('div.mat-form-field-flex', 'Project summary').should('not.exist');
    });
  });
});
