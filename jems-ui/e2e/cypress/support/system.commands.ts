import {faker} from '@faker-js/faker';

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
    cy.loginByRequest(userEmail, false);
  role.name = `${role.name}_${faker.string.alphanumeric(5)}`;
  cy.request({
    method: 'POST',
    url: 'api/role',
    body: role
  }).then(response => {
    if (userEmail) {
      // noinspection CYUnresolvedAlias
      cy.get('@currentUser').then((currentUser: any) => {
        cy.loginByRequest(currentUser.name, false);
      });
    }
    return response.body.id;
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
