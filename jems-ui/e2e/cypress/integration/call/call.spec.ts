import faker from '@faker-js/faker';
import user from '../../fixtures/users.json';

context('Call management tests', () => {

  beforeEach(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.wrap(`Automated call ${faker.datatype.uuid()}`).as('callName');
  });

  it.only('TB-388 Create a new 1-step call', function () {

    cy.visit('app/call', {failOnStatusCode: false});

    cy.contains('Add new call').click();

    // Call identification
    cy.contains('div', 'Call name').find('input').type(this.callName);

    cy.contains('div', 'Start date').next().click();
    cy.contains('mat-icon', 'done').click();

    cy.wait(1000);
    cy.contains('div', 'End date (MM').next().click();
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
    cy.get('input[name="name"]').should('have.value', this.callName);
  });


  it('TB-389 Edit and publish 1-step call', {scrollBehavior: 'nearest'}, function () {

    cy.visit('/app/call', {failOnStatusCode: false});
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
})
