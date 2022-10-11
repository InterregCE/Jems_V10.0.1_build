import {faker} from '@faker-js/faker';
import user from '../../fixtures/users.json';

context('Call management tests', () => {

  before(() => {
    cy.wrap(`Automated call ${faker.datatype.uuid()}`).as('callName');
  });

  beforeEach(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.visit('app/call', {failOnStatusCode: false});
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
    cy.get('mat-checkbox[name="additionalFundAllowed"]').click();
    cy.get('div[formarrayname="funds"]').eq(0).then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[name="fundRateValue"]').type('60,00');
    });
    cy.contains('div', 'ERDF').within(() => {
      cy.get('mat-checkbox').click();
      cy.get('input[name="fundRateValue"]').type('50,00');
      cy.contains('Up To').click();
    });

    // Strategies
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
    cy.get('jems-call-flat-rates span').contains(flatRate1).parent().then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[name="staffCostFlatRate"]').type('19');
    });

    const flatRate2 = 'Office and administrative costs flat rate based on direct staff cost';
    cy.get('jems-call-flat-rates span').contains(flatRate2).parent().then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[name="officeOnStaffFlatRate"]').type('14');
      cy.wrap(el).find('button').contains('Up To').click();
    });

    cy.get('jems-call-flat-rates button').contains('Save changes').click();
    cy.contains('mat-card-footer', 'Flat rates updated successfully').should('be.visible');

    // Lump Sums
    const lumpSum = 'Preparation Lump sum DE';
    cy.get('jems-call-lump-sums span').contains(lumpSum).parent().find('input[type="checkbox"]').check({force: true});
    cy.get('jems-call-lump-sums button').contains('Save changes').click();
    cy.contains('mat-card-footer', 'Lump sums updated successfully').should('be.visible');

    // Unit Costs
    const unitCost = 'Unit cost MCC1 DE';
    cy.get('jems-call-unit-costs span').contains(unitCost).parent().find('input[type="checkbox"]').check({force: true});
    cy.get('jems-call-unit-costs button').contains('Save changes').click();
    cy.contains('mat-card-footer', 'Unit costs updated successfully').should('be.visible');

    // Pre-submission check
    cy.contains('Pre-submission check settings').click();
    cy.get('mat-select[formcontrolname="pluginKey"]').click();
    cy.contains('No-Check').click();
    cy.get('jems-pre-submission-check-settings-page button').contains('Save changes').click();
    cy.get('jems-pre-submission-check-settings-page span').should('contain', 'Application form configuration was saved successfully');

    // Publish call
    cy.get('jems-side-nav span.title').contains('General call settings').click();
    cy.contains('Publish call').click();
    cy.get('jems-confirm-dialog button').contains('Confirm').click();

    cy.get('div.success-wrapper span').should('contain', `Successfully published Call ${this.callName}`);
  });

  it("TB-754 Create a new 2-step call", () => {
    const callName = `${faker.word.adjective()} ${faker.word.noun()}`;
    cy.intercept('/api/call/byId/*/preSubmissionCheck').as('preSubmissionCheck');
    cy.intercept('/api/project/*/workPackage').as('workPackage');

    cy.loginByRequest(user.programmeUser.email);
    cy.visit('/app/call', {failOnStatusCode: false});
    cy.contains('button', 'Add new call').click();
    cy.contains('div.mat-form-field-infix', 'Call name').find('input').type(callName);
    cy.contains('div.mat-form-field-infix', 'Period length').find('input').type('3');
    cy.contains('mat-checkbox', 'Use 2-step application form procedure for this call').scrollIntoView().find('input').check({force: true});
    cy.contains('div.mat-form-field-flex', 'End date Step 1 (MM/DD/YYYY h:mm A)').find('svg').should('be.visible');
    cy.contains('div.mat-form-field-flex', 'Start date (MM/DD/YYYY h:mm A)').find('svg').click();
    cy.contains('div.mat-calendar-body-cell-content', '2').click({force: true});
    cy.contains('button', 'done').click();
    cy.contains('div.mat-form-field-flex', 'End date (MM/DD/YYYY h:mm A)').find('svg').click();
    cy.contains('div.mat-calendar-body-cell-content', '10').click({force: true});
    cy.contains('button', 'done').click();
    cy.contains('div.mat-form-field-flex', 'End date Step 1 (MM/DD/YYYY h:mm A)').find('svg').click();
    cy.contains('td', '11').invoke('attr', 'aria-disabled').should('eq', 'true');
    cy.contains('td', '1').invoke('attr', 'aria-disabled').should('eq', 'true');
    cy.contains('div.mat-calendar-body-cell-content', '5').click({force: true});
    cy.contains('button', 'done').click();
    cy.contains('div.mat-form-field-flex', 'Start date (MM/DD/YYYY h:mm A)').find('svg').click();
    cy.root().find('button[aria-label="Previous Month"]').click();
    cy.contains('div.mat-calendar-body-cell-content', '1').click({force: true});
    cy.contains('button', 'done').click();
    cy.contains('div.mat-form-field-flex', 'End date (MM/DD/YYYY h:mm A)').find('svg').click();
    cy.root().find('button[aria-label="Next Month"]').click();
    cy.root().find('button[aria-label="Next Month"]').click();
    cy.contains('div.mat-calendar-body-cell-content', '20').click({force: true});
    cy.contains('button', 'done').click();
    cy.contains('div.mat-form-field-flex', 'End date Step 1 (MM/DD/YYYY h:mm A)').find('svg').click();
    cy.root().find('button[aria-label="Next Month"]').click();
    cy.contains('div.mat-calendar-body-cell-content', '20').click({force: true});
    cy.contains('button', 'done').click();
    cy.get('jems-call-priority-tree').scrollIntoView().find('mat-checkbox:first').find('input').check({force: true});
    cy.get('jems-call-funds').scrollIntoView().find('mat-checkbox:first').find('input').check({force: true});
    cy.intercept('/api/programmeFund').as('programmeFund');
    cy.contains('button', 'Create').click();
    cy.wait('@programmeFund');
    cy.contains('Application form configuration').click();
    cy.contains('span.mat-button-toggle-label-content', '1 & 2').should('be.visible');
    cy.contains('span.mat-button-toggle-label-content', '2 only').should('be.visible');
    cy.root().find('input.mat-slide-toggle-input').should('have.attr', 'disabled');
    cy.contains('tr', 'Project title').scrollIntoView().contains('span', '1 & 2').click();
    cy.contains('button', 'Save changes').click();
    cy.contains('Pre-submission check settings').click();
    cy.get('div.mat-form-field-flex').should('have.length', 2);
    cy.get('div.mat-form-field-flex').first().click();
    cy.contains('No-Check').should('be.visible');
    cy.contains('Blocked').should('be.visible');
    cy.contains('Standard pre condition check').should('be.visible');
    cy.contains('No-Check').click();
    cy.contains('button', 'Discard changes').click();
    cy.get('div.mat-form-field-flex').last().click();
    cy.contains('No-Check').should('be.visible');
    cy.contains('Blocked').should('be.visible');
    cy.contains('Standard pre condition check').should('be.visible');
    cy.contains('No-Check').click();
    cy.get('div.mat-form-field-flex').first().click();
    cy.contains('Blocked').click();
    cy.wait(100);
    cy.contains('button', 'Save changes').click();
    cy.wait('@preSubmissionCheck');
    cy.contains('General call settings').click();
    cy.contains('button', 'Publish call').click();
    cy.contains('button', 'Confirm').click();

    cy.wait(1000);

    cy.loginByRequest(user.applicantUser.email);
    cy.visit('/');

    cy.contains('mat-row', callName).contains('button', 'Apply').click();
    cy.contains('div.mat-form-field-flex', 'Project acronym').type(faker.word.noun());
    cy.contains('button', 'Create project application').click();
    cy.wait('@workPackage');
    cy.contains('div.mat-form-field-flex', 'Project title').should('be.visible');
    cy.contains('div.mat-form-field-flex', 'Project summary').should('not.exist');
  })
})
