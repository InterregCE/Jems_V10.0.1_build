// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

declare global {

  interface User {
    email: string,
    name?: string,
    surname?: string,
    userRoleId?: number,
    userStatus?: string
  }

  namespace Cypress {
    interface Chainable {
      loginByRequest(user: User): void

      logoutByRequest(): void

      createUser(user: User): boolean
    }
  }
}

Cypress.Commands.add('loginByRequest', (user: User) => {
  cy.request({
    method: 'POST',
    url: Cypress.env('authenticationUrl'),
    body: {
      email: user.email,
      password: Cypress.env('defaultPassword')
    }
  });
});

Cypress.Commands.add('logoutByRequest', () => {
  cy.request({
    method: 'POST',
    url: 'api/auth/logout'
  });
});

Cypress.Commands.add('createUser', (user: User) => {
  cy.request({
    method: 'POST',
    url: 'api/user',
    body: user
  }).then(response => {
    cy.request({
      method: 'PUT',
      url: `api/user/byId/${response.body.id}/password`,
      headers: {'Content-Type': 'application/json'},
      body: Cypress.env('defaultPassword')
    });
  });
});

export {}
