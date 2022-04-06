import faker from '@faker-js/faker';
import user from '../fixtures/users.json';

context('Login tests', () => {

  before(() => {
    cy.loginByRequest(user.admin.email);
    cy.createUser(user.applicantUser);
    cy.createUser(user.programmeUser);
    cy.logoutByRequest();
  });

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.visit('/');
  });

  it('TB-397 Admin can login and logout', () => {
    cy.fixture('users.json').then((user) => {

      cy.intercept('api/project/mine?*').as('applicationList');

      cy.get('#email').type(user.admin.email);
      cy.get('#password').type(Cypress.env('defaultPassword') + '{enter}');

      cy.wait('@applicationList', {timeout: 10000});

      cy.get('h2').should('contain', 'My applications');

      cy.intercept('api/auth/logout').as('logout');
      cy.get('button.logout-button').click();
      cy.wait('@logout');

      cy.get('span').should('contain', 'Login').and('be.visible');
    })
  });

  it('TB-398 Unknown users or users with incorrect password cannot login', () => {

    cy.get('#email').type('random_unknown_username');
    cy.get('#password').type('random_unknown_password');

    cy.get('button').contains('Login').click();

    cy.get('jems-alert').should('be.visible').and('contain', 'Email or password incorrect.');
    cy.get('jems-alert').contains('span', '×').click();

    cy.get('#email').type(user.admin.email);
    cy.get('#password').type('random_unknown_password');

    cy.get('button').contains('Login').click();

    cy.get('jems-alert').should('be.visible').and('contain', 'Email or password incorrect.');
  });

  it('TB-399 Applicant can register', () => {

    cy.get('a').contains('Create a new account').click();
    const id = faker.random.alphaNumeric(5);
    const email = `cypress1.${id}@Applicant.eu`;

    cy.get('input[name="name"]').type('Cypress');
    cy.get('input[name="surname"]').type(id);
    cy.get('input[name="email"]').type(email);
    cy.get('input[name="password"]').type(email);
    cy.get('mat-checkbox').click("left");

    cy.get('button[color="primary"]').click();

    cy.get('jems-alert').find('span').contains('Go to login').should('be.visible');
  });
})
