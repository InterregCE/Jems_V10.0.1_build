declare global {

  namespace Cypress {
    interface Chainable {
      loginByRequest(userEmail: string, setAsCurrent?: boolean);

      logoutByRequest(): void;

      createUser(user, creatingUserEmail?: string);
    }
  }
}

Cypress.Commands.add('loginByRequest', (userEmail: string, setAsCurrent?: boolean) => {
  cy.request({
    method: 'POST',
    url: Cypress.env('authenticationUrl'),
    body: {
      email: userEmail,
      password: Cypress.env('defaultPassword')
    }
  }).then(response => {
    if (setAsCurrent === undefined || setAsCurrent === true)
      cy.wrap(response.body).as('currentUser');
  });
});

Cypress.Commands.add('logoutByRequest', () => {
  cy.request({
    method: 'POST',
    url: 'api/auth/logout'
  });
});

Cypress.Commands.add('createUser', (user, creatingUserEmail?: string) => {
  if (creatingUserEmail)
    cy.loginByRequest(creatingUserEmail, false);
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
    user.id = response.body.id;
    cy.wrap(response.body.id).as('userId');
  });
  if (creatingUserEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      cy.loginByRequest(currentUser.name, false);
    });
  }
});

export {}
