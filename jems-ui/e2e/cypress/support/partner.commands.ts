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

      addPartnerTravelCosts(partnerId: number, travelCosts: any[]);

      updatePartnerCofinancing(partnerId: number, cofinancing);
    }
  }
}

Cypress.Commands.add('createPartner', (applicationId: number, partner: ProjectPartnerDTO) => {
  createPartner(applicationId, partner).then(response => {
    cy.wrap(response.body.id).as('partnerId');
  });
});

Cypress.Commands.add('addPartnerTravelCosts', (partnerId: number, travelCosts: BudgetTravelAndAccommodationCostEntryDTO[]) => {
  addTravelCosts(partnerId, travelCosts);
});

Cypress.Commands.add('updatePartnerCofinancing', (partnerId: number, cofinancing: ProjectPartnerCoFinancingAndContributionInputDTO) => {
  updateCofinancing(partnerId, cofinancing);
});

export function createPartner(applicationId: number, partner: ProjectPartnerDTO) {
  return cy.request({
    method: 'POST',
    url: `api/project/partner/toProjectId/${applicationId}`,
    body: partner
  });
}

export function createPartners(applicationId: number, partners: ProjectPartner[]) {
  partners.forEach(function (partner) {
    createPartner(applicationId, partner.details).then(function (response) {
      cy.wrap(response.body.id).as('partnerId');
      // Address tab
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/address`,
        body: partner.address
      });
      // Contact tab
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/contact`,
        body: partner.contact
      });
      // Motivation tab
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/motivation`,
        body: partner.motivation
      });
      // Budget tab - options
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/budget/options`,
        body: partner.budget.options
      });
      // Budget tab - costs
      partner.budget.external[0].investmentId = this.investmentId;
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/budget/external`,
        body: partner.budget.external
      });
      partner.budget.equipment[0].investmentId = this.investmentId;
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/budget/equipment`,
        body: partner.budget.equipment
      });
      partner.budget.infrastructure[0].investmentId = this.investmentId;
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/budget/infrastructure`,
        body: partner.budget.infrastructure
      });
      if (partner.budget.unit) {
        cy.request({
          method: 'PUT',
          url: `api/project/partner/${response.body.id}/budget/unitcosts`,
          body: partner.budget.unit
        });
      }
      addTravelCosts(response.body.id, partner.budget.travel);

      // Co-financing tab
      updateCofinancing(response.body.id, partner.cofinancing);

      // State Aid tab
      // TODO uncomment when create activity request is fixed
      //partner.stateAid.activities[0].activityId = this.activityId;
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/stateAid`,
        body: partner.stateAid
      });
    });
  });
}

function addTravelCosts(partnerId, travelCosts: BudgetTravelAndAccommodationCostEntryDTO[]) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/travel`,
    body: travelCosts
  });
}

function updateCofinancing(partnerId, cofinancing: any) {
  cy.request({
    method: 'PUT',
    url: `api/project/partner/${partnerId}/budget/cofinancing`,
    body: cofinancing
  });
}

export {}
