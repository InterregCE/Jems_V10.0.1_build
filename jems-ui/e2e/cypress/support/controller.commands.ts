import institutionAssignment from '@fixtures/api/control/assignment.json';
import {loginByRequest} from "./login.commands";

declare global {

  namespace Cypress {
    interface Chainable {
      createInstitution(institution);

      assignInstitution(assignment);
      
      assignDefaultInstitution(partnerId, assigningUser?);
    }
  }
}

Cypress.Commands.add('createInstitution', (institution) => {
  cy.request({
    method: 'POST',
    url: 'api/controller/institution/create',
    body: institution
  }).then(response => {
    return response.body.id
  });
});

Cypress.Commands.add('assignInstitution', (assignment) => {
  cy.request({
    method: 'POST',
    url: 'api/controller/institution/assign',
    body: assignment
  });
});

Cypress.Commands.add('assignDefaultInstitution', (partnerId, assigningUser?) => {
  if (assigningUser) {
    loginByRequest(assigningUser);
  }
  institutionAssignment.assignmentsToAdd[0].partnerId = partnerId;
  cy.request({
    method: 'POST',
    url: 'api/controller/institution/assign',
    body: institutionAssignment
  });
  if (assigningUser) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
});

export {}
