import {CallUpdateRequestDTO} from '../../../build/swagger-code-jems-api/model/callUpdateRequestDTO'
import {FlatRateSetupDTO} from '../../../build/swagger-code-jems-api/model/flatRateSetupDTO'
import {
  UpdateApplicationFormFieldConfigurationRequestDTO
} from '../../../build/swagger-code-jems-api/model/updateApplicationFormFieldConfigurationRequestDTO'
import user from '../fixtures/users.json';
import faker from "@faker-js/faker";

declare global {

  interface Call {
    generalCallSettings: CallUpdateRequestDTO,
    budgetSettings: {
      flatRates: FlatRateSetupDTO,
      lumpSums: number[],
      unitCosts: number[]
    },
    applicationFormConfiguration: UpdateApplicationFormFieldConfigurationRequestDTO[],
    preSubmissionCheckSettings: string
  }

  namespace Cypress {
    interface Chainable {
      createCall(call: Call);

      publishCall(callId: number);
    }
  }
}

Cypress.Commands.add('createCall', (call: Call) => {
  // randomize name
  call.generalCallSettings.name = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
  // set relative dates
  call.generalCallSettings.startDateTime = faker.date.recent();
  call.generalCallSettings.endDateTime = faker.date.soon(2);
  cy.request({
    method: 'POST',
    url: 'api/call',
    auth: {'user': user.programmeUser.email, 'pass': Cypress.env('defaultPassword')},
    body: call.generalCallSettings
  }).then(function (response) {
    const callId = response.body.id;
    setCallFlatRates(callId, call.budgetSettings.flatRates);
    setCallLumpSums(callId, call.budgetSettings.lumpSums);
    setCallUnitCosts(callId, call.budgetSettings.unitCosts);
    setCallApplicationFormConfiguration(callId, call.applicationFormConfiguration);
    setCallPreSubmissionCheckSettings(callId, call.preSubmissionCheckSettings);
    cy.wrap(callId);
  });
});

Cypress.Commands.add('publishCall', (callId: number) => {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/publish`
  });
});

function setCallFlatRates(callId: number, flatRates: FlatRateSetupDTO) {
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

function setCallApplicationFormConfiguration(callId: number, applicationFormConfiguration: UpdateApplicationFormFieldConfigurationRequestDTO[]) {
  cy.request({
    method: 'POST',
    url: `api/call/${callId}/applicationFormFieldConfigurations`,
    body: applicationFormConfiguration
  });
}

function setCallPreSubmissionCheckSettings(callId: number, preSubmissionCheckSettings: string) {
  cy.request({
    method: 'PUT',
    url: `api/call/byId/${callId}/preSubmissionCheck`,
    headers: {'Content-Type': 'application/json'},
    body: preSubmissionCheckSettings
  });
}

export {}
