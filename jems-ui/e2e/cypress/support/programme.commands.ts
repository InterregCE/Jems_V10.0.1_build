import {faker} from '@faker-js/faker';

declare global {

  namespace Cypress {
    interface Chainable {
      createChecklist(checklist);

      addProgrammeFund(fund);

      createLumpSum(lumpSum);
    }
  }
}

Cypress.Commands.add('createChecklist', (checklist) => {
  checklist.name = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
  cy.request({
    method: 'POST',
    url: 'api/programme/checklist/create',
    body: checklist
  }).then(response => {
    cy.wrap(response.body.id).as('checklistId');
  });
});

Cypress.Commands.add('addProgrammeFund', (fund) => {
  getExistingFunds().then(existingFunds => {
    const programmeAbbreviation = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
    fund.abbreviation.forEach(item => {
      item.translation = `${programmeAbbreviation} ${item.language}`;
    });
    existingFunds.push(fund);
    cy.request({
      method: 'PUT',
      url: 'api/programmeFund',
      body: existingFunds
    }).then(response => {
      cy.wrap(response.body[response.body.length - 1].id).as('fundId');
    });
  });
});

Cypress.Commands.add('createLumpSum', (lumpSum) => {
  const lumpSumName = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
  lumpSum.name.forEach(item => {
    item.translation = `${lumpSumName} ${item.language}`;
  });
  cy.request({
    method: 'POST',
    url: 'api/costOption/lumpSum',
    body: lumpSum
  }).then(response => {
    cy.wrap(response.body.id).as('lumpSumId');
  });
});

function getExistingFunds() {
  return cy.request({
    method: 'GET',
    url: 'api/programmeFund'
  }).then(response => {
    return response.body;
  });
}

export {}
