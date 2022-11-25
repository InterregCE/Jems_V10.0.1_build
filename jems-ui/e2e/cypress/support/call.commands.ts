import {faker} from '@faker-js/faker';
import {loginByRequest} from './login.commands';

declare global {

  namespace Cypress {
    interface Chainable {
      createCall(call, creatingUserEmail?: string);

      create2StepCall(call, creatingUserEmail?: string);

      publishCall(callId: number, publishingUserEmail?: string);
    }
  }
}

Cypress.Commands.add('createCall', (call, creatingUserEmail?: string) => {
  call.generalCallSettings.startDateTime = faker.date.recent();
  call.generalCallSettings.endDateTime = faker.date.soon(2);
  createCall(call, creatingUserEmail);
});

Cypress.Commands.add('create2StepCall', (call, creatingUserEmail?: string) => {
  call.generalCallSettings.startDateTime = faker.date.recent();
  call.generalCallSettings.endDateTimeStep1 = faker.date.soon(1);
  call.generalCallSettings.endDateTime = faker.date.soon(1, call.generalCallSettings.endDateTimeStep1);
  createCall(call, creatingUserEmail);
});

Cypress.Commands.add('publishCall', (callId: number, publishingUserEmail?: string) => {
  if (publishingUserEmail)
    loginByRequest(publishingUserEmail);
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/publish`
  });
  if (publishingUserEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
});

function createCall(call, creatingUserEmail?: string) {
  call.generalCallSettings.name = `${faker.word.adverb()} ${faker.hacker.noun()} ${faker.datatype.uuid()}`;
  if (creatingUserEmail)
    loginByRequest(creatingUserEmail);
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
    if (call.preSubmissionCheckSettings)
      setCallPreSubmissionCheckSettings(callId, call.preSubmissionCheckSettings);
    if (creatingUserEmail) {
      cy.get('@currentUser').then((currentUser: any) => {
        loginByRequest(currentUser.name);
      });
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

function allowedCostOption(callId: number, allowedCostOption) {
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

function setCallPreSubmissionCheckSettings(callId: number, preSubmissionCheckSettings) {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/preSubmissionCheck`,
    headers: {'Content-Type': 'application/json'},
    body: preSubmissionCheckSettings
  });
}

export {}
