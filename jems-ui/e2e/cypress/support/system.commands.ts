import {loginByRequest} from './login.commands';
import {faker} from "@faker-js/faker";

declare global {

  namespace Cypress {
    interface Chainable {
      createRole(role, userEmail?: string);
      
      updateRole(role);
    }
  }
}

Cypress.Commands.add('createRole', (role, userEmail?: string) => {
  if (userEmail)
    loginByRequest(userEmail);
  role.name = `${role.name}_${faker.random.alphaNumeric(5)}`;
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
    cy.wrap(response.body.id).as('roleId');
  });
});

Cypress.Commands.add('updateRole', (role) => {
  cy.request({
    method: 'PUT',
    url: 'api/role',
    body: role
  });
});

export {}
