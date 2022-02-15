import user from '../fixtures/users.json';
import faker from "@faker-js/faker";

context('Project management tests', () => {

  before(() => {
    cy.loginByRequest(user.admin);
    cy.createUser(user.applicantUser);
  });

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.applicantUser);
  });

  it('Applicant can apply for a call', function () {
    cy.visit('/');

    cy.get('jems-call-list').should('exist');


    cy.contains('Apply').click();
    cy.contains('Apply').click();

    const random = faker.random.alpha(5);
    cy.get('input[name="acronym"]').type(`Automation Project ${random}`);

    cy.contains('Create project application').click();

    cy.get('jems-project-application-list').find('mat-cell').contains(`Automation Project ${random}`)
  });
})
