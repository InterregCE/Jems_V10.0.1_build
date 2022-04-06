import {loginByRequest} from './login.commands';

declare global {

  interface Role {
    name: string,
    permissions: string[]
  }

  namespace Cypress {
    interface Chainable {
      createRole(role: Role, userEmail?: string);
    }
  }
}

Cypress.Commands.add('createRole', (role: Role, userEmail?: string) => {
  if (userEmail)
    loginByRequest(userEmail);
  cy.request({
    method: 'POST',
    url: 'api/role',
    body: role
  }).then(response => {
    if (userEmail) {
      cy.get('@currentUser').then((currentUser: any) => {
        loginByRequest(currentUser.name);
      });
    }
    cy.wrap(response.body.id);
  });
});

export {}
