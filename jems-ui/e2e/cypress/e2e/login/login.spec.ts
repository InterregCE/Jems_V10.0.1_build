import { faker } from '@faker-js/faker';
import user from '../../fixtures/users.json';

context('Login tests', () => {

  before(() => {
    cy.loginByRequest(user.admin.email);
    cy.createUser(user.applicantUser);
    cy.createUser(user.programmeUser);
    cy.logoutByRequest();
  });

  beforeEach(() => {
    cy.visit('/');
  });

  it('TB-397 Admin can login and logout', () => {
    cy.fixture('users.json').then((user) => {

      cy.contains('div', 'Email').type(user.admin.email);
      cy.contains('div', 'Password').type(Cypress.env('defaultPassword') + '{enter}');

      cy.contains('h2', 'My applications').should('be.visible');

      cy.get('button.logout-button').click();

      cy.contains('button', 'Login').should('be.visible');
    })
  });

  it('TB-398 Unknown users or users with incorrect password cannot login', () => {

    cy.contains('div', 'Email').find('input').type('random_unknown_username');
    cy.contains('div', 'Password').find('input').type('random_unknown_password');

    cy.get('button').contains('Login').click();

    cy.contains('Email or password incorrect.').should('be.visible');
    cy.get('jems-alert').contains('span', '×').click();
    cy.contains('Email or password incorrect.').should('not.exist');

    cy.contains('div', 'Email').find('input').clear().type(user.admin.email);
    cy.contains('div', 'Password').find('input').clear().type('random_unknown_password');

    cy.get('button').contains('Login').click();
    cy.contains('Email or password incorrect.').should('be.visible');
  });

  it('TB-399 Applicant can register', () => {

    cy.contains('Create a new account').click();
    const id = faker.random.alphaNumeric(5);
    const email = `cypress1.${id}@Applicant.eu`;

    cy.contains('div', 'First name').find('input').type('Cypress');
    cy.contains('div', 'Last name').find('input').type(id);
    cy.contains('div', 'Email').find('input').type(email);
    cy.contains('div', 'Password').find('input').type(email);
    cy.get('mat-checkbox').click("left");

    cy.contains('button', 'Register').click();

    cy.contains('Please check your Inbox for a confirmation email. Click the link in the email to confirm your email address.').should('be.visible');
    cy.contains('button', 'Go to login').click();
    cy.contains('button', 'Login').should('be.visible');
  });
})
