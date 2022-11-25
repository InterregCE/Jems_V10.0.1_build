declare global {

  namespace Cypress {
    interface Chainable {

      createPartner(applicationId: number, partner);

      createPartners(applicationId: number, partners: any[]);

      updatePartner(partnerId: number, partner);

      updatePartnerAddress(partnerId: number, address: any[]);

      updatePartnerContact(partnerId: number, address: any[]);

      updatePartnerMotivation(partnerId: number, address);

      updatePartnerBudget(partnerId: number, address, investmentId?: number);

      addPartnerTravelCosts(partnerId: number, travelCosts: any[]);

      updatePartnerCofinancing(partnerId: number, cofinancing);

      updatePartnerStateAid(partnerId: number, stateAid, options?: any);

      deactivatePartner(partnerId: number);

      createAssociatedOrganization(applicationId: number, partnerId: number, stateAid);
    }
  }
}

Cypress.Commands.add('createPartner', (applicationId: number, partner) => {
  createPartner(applicationId, partner);
});

Cypress.Commands.add('createPartners', (applicationId: number, partners: []) => {
  createPartners(applicationId, partners);
});

Cypress.Commands.add('updatePartner', (partnerId: number, partner) => {
  partner.id = partnerId;
  cy.request({
    method: 'PUT',
    url: `api/project/partner`,
    body: partner
  });
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

Cypress.Commands.add('updatePartnerBudget', (partnerId: number, partnerBudget, investmentId: number) => {
  updateBudget(partnerId, partnerBudget, investmentId);
});

Cypress.Commands.add('addPartnerTravelCosts', (partnerId: number, travelCosts: []) => {
  addTravelCosts(partnerId, travelCosts);
});

Cypress.Commands.add('updatePartnerCofinancing', (partnerId: number, cofinancing) => {
  updateCofinancing(partnerId, cofinancing);
});

Cypress.Commands.add('updatePartnerStateAid', (partnerId: number, stateAid, options?) => {
  updateStateAid(partnerId, stateAid, options);
});

Cypress.Commands.add('deactivatePartner', (partnerId: number) => {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/deactivate`
  });
});

Cypress.Commands.add('createAssociatedOrganization', (applicationId: number, partnerId: number, associatedOrganization) => {
  createAssociatedOrganization(applicationId, partnerId, associatedOrganization);
});

export function createPartner(applicationId: number, partner) {
  return cy.request({
    method: 'POST',
    url: `api/project/partner/toProjectId/${applicationId}`,
    body: partner
  }).then(response => {
    cy.wrap(response.body.id);
  });
}

export function createPartners(applicationId: number, partners: [], options?) {
  partners.forEach((partner: any) => {
    createPartner(applicationId, partner.details).then(partnerId => {
      cy.wrap(partnerId).as(partner.details.abbreviation);
      updateAddress(partnerId, partner.address);
      updateContact(partnerId, partner.contact);
      updateMotivation(partnerId, partner.motivation);
      updateBudget(partnerId, partner.budget);
      updateCofinancing(partnerId, partner.cofinancing);
      if (partner.spfCofinancing)
        updateSpfCofinancing(partnerId, partner.spfCofinancing);
      updateStateAid(partnerId, partner.stateAid, options);
    });
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

function updateBudget(partnerId, budget, investmentId?: number) {

  if (investmentId) {
    budget.external[0].investmentId = investmentId;
    budget.equipment[0].investmentId = investmentId;
    budget.infrastructure[0].investmentId = investmentId;
  }
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/options`,
    body: budget.options
  });

  if (budget.staff) {
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/staffcosts`,
      body: budget.staff
    });
  }
  if (budget.external) {
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/external`,
      body: budget.external
    });
  }
  if (budget.equipment) {
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/equipment`,
      body: budget.equipment
    });
  }
  if (budget.infrastructure) {
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/infrastructure`,
      body: budget.infrastructure
    });
  }
  if (budget.unit) {
    cy.request({
      method: 'PUT',
      url: `api/project/partner/${partnerId}/budget/unitcosts`,
      body: budget.unit
    });
  }
  if (budget.travel) {
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

function updateStateAid(partnerId, stateAid, options?) {
  const stateAidCopy = JSON.parse(JSON.stringify(stateAid));
  if (options?.workPlanId) {
    const activity = {
      "activityId": options.activityId,
      "workPackageNumber": options.workPlanId,
      "activityNumber": null
    }
    stateAidCopy.activities.push(activity);
  }
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/stateAid`,
    body: stateAidCopy
  });
}

function createAssociatedOrganization(applicationId: number, partnerId: number, associatedOrganization) {
  associatedOrganization.partnerId = partnerId
  cy.request({
    method: 'POST',
    url: `api/project/${applicationId}/organization`,
    body: associatedOrganization
  });
}

export {}
