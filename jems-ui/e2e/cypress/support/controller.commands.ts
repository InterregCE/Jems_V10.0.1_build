import institutionAssignment from '@fixtures/api/control/assignment.json';

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
    cy.loginByRequest(assigningUser, false);
  }
  institutionAssignment.assignmentsToAdd[0].partnerId = partnerId;
  cy.request({
    method: 'POST',
    url: 'api/controller/institution/assign',
    body: institutionAssignment
  });
  if (assigningUser) {
    cy.get('@currentUser').then((currentUser: any) => {
      cy.loginByRequest(currentUser.name, false);
    });
  }
});

export {}
