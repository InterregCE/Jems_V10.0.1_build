import user from '../../fixtures/users.json';
import faker from "@faker-js/faker";

context('Project management tests', () => {

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.applicantUser);
  });

  it('TB-390 Applicant can apply for a call', function () {
    cy.visit('/');

    cy.get('jems-call-list').should('exist');


    cy.intercept(/api\/call\/byId\/\d/).as('apply');
    cy.contains('Apply').click();
    cy.wait('@apply')
    cy.contains('Apply').click();

    const random = faker.random.alpha(5);
    cy.get('input[name="acronym"]').type(`Automation Project ${random}`);

    cy.contains('Create project application').click();

    cy.get('jems-project-page-template').find('h1').contains(`Automation Project ${random}`)
  });
})
