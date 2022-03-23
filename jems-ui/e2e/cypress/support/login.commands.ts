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
      loginByRequest(user: User): void;

      logoutByRequest(): void;

      createUser(user: User): boolean;
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
