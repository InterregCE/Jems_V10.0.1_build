declare global {

  namespace Cypress {
    interface Chainable {

      createPartner(applicationId: number, partner);

      createPartners(applicationId: number, partners: any[]);

      createFullPartner(applicationId: number, partner);

      createFullPartners(applicationId: number, partners: any[]);

      updatePartnerData(partnerId: number, partnerData: any);

      updatePartnerIdentity(partnerId: number, identity);

      updatePartnerAddress(partnerId: number, address: any[]);

      updatePartnerContact(partnerId: number, address: any[]);

      updatePartnerMotivation(partnerId: number, address);

      updatePartnerBudget(partnerId: number, address);

      addPartnerTravelCosts(partnerId: number, travelCosts: any[]);

      updatePartnerCofinancing(partnerId: number, cofinancing);

      updatePartnerStateAid(partnerId: number, stateAid);

      deactivatePartner(partnerId: number);
    }
  }
}

Cypress.Commands.add('createPartner', (applicationId: number, partner) => {
  createPartner(applicationId, partner);
});

Cypress.Commands.add('createPartners', (applicationId: number, partners: []) => {
  createPartners(applicationId, partners);
});

Cypress.Commands.add('createFullPartner', (applicationId: number, partner) => {
  createFullPartner(applicationId, partner);
});

Cypress.Commands.add('createFullPartners', (applicationId: number, partners: []) => {
  createFullPartners(applicationId, partners);
});

Cypress.Commands.add('updatePartnerData', (partnerId: number, partnerData) => {
  updatePartnerData(partnerId, partnerData);
});

Cypress.Commands.add('updatePartnerIdentity', (partnerId: number, identity) => {
  updateIdentity(partnerId, identity);
});

Cypress.Commands.add('updatePartnerAddress', (partnerId: number, partnerAddress: []) => {
  updateAddress(partnerId, partnerAddress);
});

Cypress.Commands.add('updatePartnerContact', (partnerId: number, partnerContact: []) => {
  updateContact(partnerId, partnerContact);
});

Cypress.Commands.add('updatePartnerMotivation', (partnerId: number, partnerMotivation) => {
  updateMotivation(partnerId, partnerMotivation);
});

Cypress.Commands.add('updatePartnerBudget', (partnerId: number, partnerBudget) => {
  updateBudget(partnerId, partnerBudget);
});

Cypress.Commands.add('addPartnerTravelCosts', (partnerId: number, travelCosts: []) => {
  addTravelCosts(partnerId, travelCosts);
});

Cypress.Commands.add('updatePartnerCofinancing', (partnerId: number, cofinancing) => {
  updateCofinancing(partnerId, cofinancing);
});

Cypress.Commands.add('updatePartnerStateAid', (partnerId: number, stateAid) => {
  updateStateAid(partnerId, stateAid);
});

Cypress.Commands.add('deactivatePartner', (partnerId: number) => {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/deactivate`
  });
});

export function createPartner(applicationId: number, partnerDetails) {
  return cy.request({
    method: 'POST',
    url: `api/project/partner/toProjectId/${applicationId}`,
    body: partnerDetails
  }).then(response => {
    cy.wrap(response.body.id).as(partnerDetails.abbreviation);
  });
}

export function createFullPartner(applicationId: number, partner) {
  createPartner(applicationId, partner.details).then(partnerId => {
    updatePartnerData(partnerId, partner);
    cy.wrap(partnerId).as('partnerId');
  });
}

export function createPartners(applicationId: number, partners: []) {
  partners.forEach((partner: any) => {
    createPartner(applicationId, partner.details);
  });
}

export function createFullPartners(applicationId: number, partners: []) {
  partners.forEach(partner => {
    createFullPartner(applicationId, partner);
  });
}

export function updatePartnerData(partnerId, partnerData) {
  updateIdentity(partnerId, partnerData.details)
  updateAddress(partnerId, partnerData.address);
  updateContact(partnerId, partnerData.contact);
  updateMotivation(partnerId, partnerData.motivation);
  updateBudget(partnerId, partnerData.budget);
  updateCofinancing(partnerId, partnerData.cofinancing);
  if (partnerData.spfCofinancing)
    updateSpfCofinancing(partnerId, partnerData.spfCofinancing);
  updateStateAid(partnerId, partnerData.stateAid);
}

function updateIdentity(partnerId, identity) {
  const tempPartner = JSON.parse(JSON.stringify(identity));
  tempPartner.id = partnerId;
  cy.request({
    method: 'PUT',
    url: `api/project/partner`,
    body: tempPartner
  });
}

function updateAddress(partnerId, address: []) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/address`,
    body: address
  });
}

function updateContact(partnerId, contact: []) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/contact`,
    body: contact
  });
}

function updateMotivation(partnerId, motivation) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/motivation`,
    body: motivation
  });
}

function updateBudget(partnerId, budget) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/options`,
    body: budget.options
  });

  if (budget.staff) {
    matchProjectProposedUnitCostReferences(budget.staff);
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/staffcosts`,
      body: budget.staff
    });
  }
  if (budget.external) {
    matchProjectProposedUnitCostReferences(budget.external);
    matchInvestmentReferences(budget.external);
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/external`,
      body: budget.external
    });
  }
  if (budget.equipment) {
    matchProjectProposedUnitCostReferences(budget.equipment);
    matchInvestmentReferences(budget.equipment);
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/equipment`,
      body: budget.equipment
    });
  }
  if (budget.infrastructure) {
    matchProjectProposedUnitCostReferences(budget.infrastructure);
    matchInvestmentReferences(budget.infrastructure);
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/infrastructure`,
      body: budget.infrastructure
    });
  }
  if (budget.unit) {
    matchProjectProposedUnitCostReferences(budget.unit);
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/unitcosts`,
      body: budget.unit
    });
  }
  if (budget.travel) {
    matchProjectProposedUnitCostReferences(budget.travel);
    addTravelCosts(partnerId, budget.travel);
  }
  if (budget.spf) {
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/spf`,
      body: budget.spf
    });
  }
}

function addTravelCosts(partnerId: number, travelCosts: []) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/travel`,
    body: travelCosts
  });
}

function updateCofinancing(partnerId, cofinancing) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/cofinancing`,
    body: cofinancing
  });
}

function updateSpfCofinancing(partnerId, spfCofinancing) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/spf/cofinancing`,
    body: spfCofinancing
  });
}

function updateStateAid(partnerId, stateAid) {
  // set activityId in the state aid
  cy.then(function () {
    stateAid.activities?.forEach((activity) => {
      if (activity.cypressReferenceStateAid) {
        activity.activityId = this[activity.cypressReferenceStateAid];
      }
    });
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/stateAid`,
      body: stateAid
    });
  });
}

function matchProjectProposedUnitCostReferences(costs) {
  // match any project proposed unit cost reference to its id
  cy.then(function () {
    costs.forEach(cost => {
      if (cost.cypressReferenceUnit) {
        cost.unitCostId = this[cost.cypressReferenceUnit];
      }
    });
  });
}

function matchInvestmentReferences(investments) {
  // match any project proposed unit cost reference to its id
  cy.then(function () {
    investments.forEach(investment => {
      if (investment.cypressReferenceInvestment) {
        investment.investmentId = this[investment.cypressReferenceInvestment];
      }
    });
  });
}

export {}
