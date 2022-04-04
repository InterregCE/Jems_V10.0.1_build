import {ProjectPartnerDTO} from '../../../build/swagger-code-jems-api/model/projectPartnerDTO'
import {ProjectPartnerAddressDTO} from '../../../build/swagger-code-jems-api/model/projectPartnerAddressDTO'
import {ProjectContactDTO} from '../../../build/swagger-code-jems-api/model/projectContactDTO'
import {ProjectPartnerMotivationDTO} from '../../../build/swagger-code-jems-api/model/projectPartnerMotivationDTO'
import {ProjectPartnerStateAidDTO} from '../../../build/swagger-code-jems-api/model/projectPartnerStateAidDTO'
import {
  ProjectPartnerCoFinancingAndContributionInputDTO
} from '../../../build/swagger-code-jems-api/model/projectPartnerCoFinancingAndContributionInputDTO'
import {ProjectPartnerBudgetOptionsDto} from '../../../build/swagger-code-jems-api/model/projectPartnerBudgetOptionsDto'
import {BudgetGeneralCostEntryDTO} from '../../../build/swagger-code-jems-api/model/budgetGeneralCostEntryDTO'
import {BudgetUnitCostEntryDTO} from '../../../build/swagger-code-jems-api/model/budgetUnitCostEntryDTO'
import {BudgetStaffCostEntryDTO} from '../../../build/swagger-code-jems-api/model/budgetStaffCostEntryDTO'
import {
  InputProjectAssociatedOrganization
} from '../../../build/swagger-code-jems-api/model/inputProjectAssociatedOrganization'
import {
  BudgetTravelAndAccommodationCostEntryDTO
} from '../../../build/swagger-code-jems-api/model/budgetTravelAndAccommodationCostEntryDTO'

declare global {

  interface ProjectPartner {
    details: ProjectPartnerDTO,
    address?: ProjectPartnerAddressDTO[],
    contact?: ProjectContactDTO[],
    motivation?: ProjectPartnerMotivationDTO,
    budget?: PartnerBudget,
    cofinancing?: ProjectPartnerCoFinancingAndContributionInputDTO,
    stateAid?: ProjectPartnerStateAidDTO
  }

  interface PartnerBudget {
    options?: ProjectPartnerBudgetOptionsDto,
    staff?: BudgetStaffCostEntryDTO[],
    external?: BudgetGeneralCostEntryDTO[],
    equipment?: BudgetGeneralCostEntryDTO[],
    infrastructure?: BudgetGeneralCostEntryDTO[],
    unit?: BudgetUnitCostEntryDTO[],
    travel?: BudgetTravelAndAccommodationCostEntryDTO[]
  }

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

      updatePartnerStateAid(partnerId: number, stateAid);

      deactivatePartner(partnerId: number);

      createAssociatedOrganization(applicationId: number, partnerId: number, stateAid);
    }
  }
}

Cypress.Commands.add('createPartner', (applicationId: number, partner: ProjectPartnerDTO) => {
  createPartner(applicationId, partner).then(response => {
    cy.wrap(response.body.id).as('partnerId');
  });
});

Cypress.Commands.add('createPartners', (applicationId: number, partners: ProjectPartner[]) => {
  createPartners(applicationId, partners);
});

Cypress.Commands.add('updatePartner', (partnerId: number, partner: ProjectPartnerDTO) => {
  partner.id = partnerId;
  cy.request({
    method: 'PUT',
    url: `api/project/partner`,
    body: partner
  });
});

Cypress.Commands.add('updatePartnerAddress', (partnerId: number, partnerAddress: ProjectPartnerAddressDTO[]) => {
  updateAddress(partnerId, partnerAddress);
});

Cypress.Commands.add('updatePartnerContact', (partnerId: number, partnerContact: ProjectContactDTO[]) => {
  updateContact(partnerId, partnerContact);
});

Cypress.Commands.add('updatePartnerMotivation', (partnerId: number, partnerMotivation: ProjectPartnerMotivationDTO) => {
  updateMotivation(partnerId, partnerMotivation);
});

Cypress.Commands.add('updatePartnerBudget', (partnerId: number, partnerBudget: PartnerBudget, investmentId: number) => {
  updateBudget(partnerId, partnerBudget, investmentId);
});

Cypress.Commands.add('addPartnerTravelCosts', (partnerId: number, travelCosts: BudgetTravelAndAccommodationCostEntryDTO[]) => {
  addTravelCosts(partnerId, travelCosts);
});

Cypress.Commands.add('updatePartnerCofinancing', (partnerId: number, cofinancing: ProjectPartnerCoFinancingAndContributionInputDTO) => {
  updateCofinancing(partnerId, cofinancing);
});

Cypress.Commands.add('updatePartnerStateAid', (partnerId: number, stateAid: ProjectPartnerStateAidDTO) => {
  updateStateAid(partnerId, stateAid);
});

Cypress.Commands.add('deactivatePartner', (partnerId: number) => {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/deactivate`
  });
});

Cypress.Commands.add('createAssociatedOrganization', (applicationId: number, partnerId: number, associatedOrganization: InputProjectAssociatedOrganization) => {
  createAssociatedOrganization(applicationId, partnerId, associatedOrganization);
});

export function createPartner(applicationId: number, partner: ProjectPartnerDTO) {
  return cy.request({
    method: 'POST',
    url: `api/project/partner/toProjectId/${applicationId}`,
    body: partner
  });
}

export function createPartners(applicationId: number, partners: ProjectPartner[]) {
  partners.forEach(partner => {
    createPartner(applicationId, partner.details).then(response => {
      cy.wrap(response.body.id).as('partnerId');
      updateAddress(response.body.id, partner.address);
      updateContact(response.body.id, partner.contact);
      updateMotivation(response.body.id, partner.motivation);
      updateBudget(response.body.id, partner.budget);
      updateCofinancing(response.body.id, partner.cofinancing);
      updateStateAid(response.body.id, partner.stateAid);
    });
  });
}

function updateAddress(partnerId: number, address: ProjectPartnerAddressDTO[]) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/address`,
    body: address
  });
}

function updateContact(partnerId: number, contact: ProjectContactDTO[]) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/contact`,
    body: contact
  });
}

function updateMotivation(partnerId: number, motivation: ProjectPartnerMotivationDTO) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/motivation`,
    body: motivation
  });
}

function updateBudget(partnerId: number, budget: PartnerBudget, investmentId?: number) {

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
}

function addTravelCosts(partnerId: number, travelCosts: BudgetTravelAndAccommodationCostEntryDTO[]) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/travel`,
    body: travelCosts
  });
}

function updateCofinancing(partnerId: number, cofinancing: ProjectPartnerCoFinancingAndContributionInputDTO) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/cofinancing`,
    body: cofinancing
  });
}

function updateStateAid(partnerId: number, stateAid: ProjectPartnerStateAidDTO, activityId?: number) {
  if (activityId)
    stateAid.activities[0].activityId = activityId;
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/stateAid`,
    body: stateAid
  });
}

function createAssociatedOrganization(applicationId: number, partnerId: number, associatedOrganization: InputProjectAssociatedOrganization) {
  associatedOrganization.partnerId = partnerId
  cy.request({
    method: 'POST',
    url: `api/project/${applicationId}/organization`,
    body: associatedOrganization
  });
}

export {}
