import {faker} from '@faker-js/faker';

declare global {

  namespace Cypress {
    interface Chainable {
      createCall(call, creatingUserEmail?: string);

      create2StepCall(call, creatingUserEmail?: string);

      publishCall(callId: number, publishingUserEmail?: string);

      updateCallPreSubmissionCheckSettings(callId: number, preSubmissionCheckSettings: any): any;

      updateCallChecklists(callId: number, checklistIds: number[]): void;
    }
  }
}

Cypress.Commands.add('createCall', (call, creatingUserEmail?: string) => {
  call.generalCallSettings.startDateTime = faker.date.recent();
  call.generalCallSettings.endDateTime = faker.date.soon({ days: 2 });
  createCall(call, creatingUserEmail);
});

Cypress.Commands.add('create2StepCall', (call, creatingUserEmail?: string) => {
  call.generalCallSettings.startDateTime = faker.date.recent();
  call.generalCallSettings.endDateTimeStep1 = faker.date.soon({ days: 1 });
  call.generalCallSettings.endDateTime = faker.date.soon({ days: 1, refDate: call.generalCallSettings.endDateTimeStep1 });
  createCall(call, creatingUserEmail);
});

Cypress.Commands.add('publishCall', (callId: number, publishingUserEmail?: string) => {
  if (publishingUserEmail)
    cy.loginByRequest(publishingUserEmail, false);
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/publish`
  });
  if (publishingUserEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      cy.loginByRequest(currentUser.name, false);
    });
  }
});

Cypress.Commands.add('updateCallPreSubmissionCheckSettings', (callId: number, preSubmissionCheckSettings: any) => {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/preSubmissionCheck`,
    headers: {'Content-Type': 'application/json'},
    body: preSubmissionCheckSettings
  });
});

Cypress.Commands.add('updateCallChecklists', (callId: number, checklistIds: number[]) => {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/checklists`,
    body: checklistIds
  });
});

function createCall(call, creatingUserEmail?: string) {
  call.generalCallSettings.name = `${faker.word.adverb()} ${faker.hacker.noun()} ${faker.string.uuid()}`;
  if (creatingUserEmail)
    cy.loginByRequest(creatingUserEmail, false);
  cy.request({
    method: 'POST',
    url: 'api/call',
    body: call.generalCallSettings
  }).then(function (response) {
    const callId = response.body.id;
    if (call.budgetSettings?.flatRates)
      setCallFlatRates(callId, call.budgetSettings.flatRates);
    if (call.budgetSettings?.lumpSums)
      setCallLumpSums(callId, call.budgetSettings.lumpSums);
    if (call.budgetSettings?.unitCosts)
      setCallUnitCosts(callId, call.budgetSettings.unitCosts);
    if (call.budgetSettings?.allowedCostOption)
      allowedCostOption(callId, call.budgetSettings.allowedCostOption);
    if (call.applicationFormConfiguration)
      setCallApplicationFormConfiguration(callId, call.applicationFormConfiguration);
    if (call.checklists)
      cy.updateCallChecklists(callId, call.checklists);
    if (call.preSubmissionCheckSettings)
      cy.updateCallPreSubmissionCheckSettings(callId, call.preSubmissionCheckSettings);
    if (creatingUserEmail && this.currentUser) {
      cy.loginByRequest(this.currentUser.name, false);
    }
    cy.wrap(callId).as('callId');
  });
}

function setCallFlatRates(callId: number, flatRates) {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/flatRate`,
    body: flatRates
  });
}

function setCallLumpSums(callId: number, lumpSums: number[]) {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/lumpSum`,
    body: lumpSums
  });
}

function setCallUnitCosts(callId: number, unitCosts: number[]) {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/unitCost`,
    body: unitCosts
  });
}

function allowedCostOption(callId: number, allowedCostOption: any) {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/allowedCostOption`,
    body: allowedCostOption
  });
}

function setCallApplicationFormConfiguration(callId: number, applicationFormConfiguration: []) {
  cy.request({
    method: 'POST',
    url: `api/call/${callId}/applicationFormFieldConfigurations`,
    body: applicationFormConfiguration
  });
}

export {}
