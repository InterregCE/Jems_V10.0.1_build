import user from '../fixtures/users.json';
import faker from "@faker-js/faker";

context('Call management tests', () => {

  before(() => {
    cy.loginByRequest(user.admin);
    cy.createUser(user.programmeUser);
  });

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.programmeUser);
  });

  it('Call can be created', function () {

    cy.visit('/');

    cy.get('div#navbarSupportedContent a').contains('Calls').click();
    cy.contains('Add new call').click();

    // Call identification
    this.callName = `Automated call ${faker.random.uuid()}`;
    cy.get('input[name="name"]').type(this.callName);

    cy.get('button[aria-label="Open Calendar"]').eq(0).click();
    cy.get('mat-icon').contains('done').click();

    cy.get('button[aria-label="Open Calendar"]').eq(2).click();
    cy.get('table.mat-calendar-table').find('tr').last().find('td').last().click();
    cy.get('mat-icon').contains('done').click();

    cy.get('input[name="lengthOfPeriod"]').type('3');
    cy.get('textarea').type('Automated call description');

    // Programme Priorities
    const priority = 'Reaping the benefits of digitisation for citizens, companies, research organisations and public authorities';
    cy.get('jems-call-priority-tree').find('span').contains(priority).click();

    // Strategies
    const strategy = 'EU Strategy for the Adriatic and Ionian Region';
    cy.get('jems-call-strategies').find('mat-checkbox').contains(strategy).click();

    // Funds
    cy.get('mat-checkbox[name="additionalFundAllowed"]').click();
    cy.get('div[formarrayname="funds"]').eq(0).then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[name="fundRateValue"]').type('60,00');
    });
    cy.get('div[formarrayname="funds"]').eq(1).then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[name="fundRateValue"]').type('50,00');
      cy.wrap(el).find('button').contains('Up To').click();
    });

    // Strategies
    cy.get('jems-call-state-aids').contains('General de minimis').click();

    cy.intercept('api/call').as('callCreation');
    cy.get('jems-pending-button').contains('Create').click();
    cy.wait('@callCreation').then(res => {
      cy.wrap(res.response.body.id).as('callId');
    });

    cy.get('input[name="name"]').should('have.value', this.callName);
  });


  it('Call can be opened, edited and published', function () {

    cy.visit('/');
    cy.get('div#navbarSupportedContent a').contains('Calls').click();

    cy.get('div[title]').contains(this.callName).click();

    // Budget settings
    cy.get('jems-side-nav span.title').contains('Budget Settings').click();

    // Flat Rates
    cy.intercept(/api\/call\/byId\/\d\/flatRate/).as('flatRate');
    const flatRate1 = 'Staff cost flat rate based on direct cost';
    cy.get('jems-call-flat-rates span').contains(flatRate1).parent().then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[name="staffCostFlatRate"]').type('19');
    });

    const flatRate2 = 'Office and administration flat rate based on direct staff cost';
    cy.get('jems-call-flat-rates span').contains(flatRate2).parent().then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[name="officeOnStaffFlatRate"]').type('14');
      cy.wrap(el).find('button').contains('Up To').click();
    });

    cy.get('jems-call-flat-rates button').contains('Save changes').click();
    cy.wait('@flatRate');

    // Lump Sums
    cy.intercept(/api\/call\/byId\/\d\/lumpSum/).as('lumpSum');
    const lumpSum = 'DE Preparation Lump sum';
    cy.get('jems-call-lump-sums span').contains(lumpSum).parent().find('input[type="checkbox"]').check({force: true});
    cy.get('jems-call-lump-sums button').contains('Save changes').click();
    cy.wait('@lumpSum');

    // Unit Costs
    cy.intercept(/api\/call\/byId\/\d\/unitCost/).as('unitCost');
    const unitCost = 'DE Unit cost MCC1';
    cy.get('jems-call-unit-costs span').contains(unitCost).parent().find('input[type="checkbox"]').check({force: true});
    cy.get('jems-call-unit-costs button').contains('Save changes').click();
    cy.wait('@unitCost');

    // Pre-submission check
    cy.get('jems-side-nav span.title').contains('Pre-submission check settings').click();
    cy.get('mat-select[formcontrolname="plugin"]').click();
    cy.get('mat-option').contains('No-Check').click();
    cy.get('jems-pre-submission-check-settings-page button').contains('Save changes').click();

    // Publish call
    cy.intercept(/api\/call\/byId\/\d\/publish/).as('publish');
    cy.get('jems-side-nav span.title').contains('General call settings').click();
    cy.contains('Publish call').click();
    cy.get('jems-confirm-dialog button').contains('Confirm').click();
    cy.wait('@publish');

    cy.get('div.success-wrapper span').should('contain', `Successfully published Call ${this.callName}`);
  });
})
