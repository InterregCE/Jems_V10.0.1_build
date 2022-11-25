
declare global {

  namespace Cypress {
    interface Chainable {
      createInstitution(institution);

      assignInstitution(assignment);
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

export {}
