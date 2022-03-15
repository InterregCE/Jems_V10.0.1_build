import {ProjectCreateDTO} from '../../../build/swagger-code-jems-api/model/projectCreateDTO'
import {InputProjectData} from '../../../build/swagger-code-jems-api/model/inputProjectData'
import {InputProjectRelevance} from '../../../build/swagger-code-jems-api/model/inputProjectRelevance'
import {InputWorkPackageCreate} from '../../../build/swagger-code-jems-api/model/inputWorkPackageCreate'
import {WorkPackageInvestmentDTO} from '../../../build/swagger-code-jems-api/model/workPackageInvestmentDTO'
import {WorkPackageActivityDTO} from '../../../build/swagger-code-jems-api/model/workPackageActivityDTO'
import {WorkPackageOutputDTO} from '../../../build/swagger-code-jems-api/model/workPackageOutputDTO'
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
import {ProjectResultDTO} from '../../../build/swagger-code-jems-api/model/projectResultDTO'
import {InputProjectManagement} from '../../../build/swagger-code-jems-api/model/inputProjectManagement'
import {InputProjectLongTermPlans} from '../../../build/swagger-code-jems-api/model/inputProjectLongTermPlans'
import {
  ProjectAssessmentEligibilityDTO
} from '../../../build/swagger-code-jems-api/model/projectAssessmentEligibilityDTO'
import {ProjectAssessmentQualityDTO} from '../../../build/swagger-code-jems-api/model/projectAssessmentQualityDTO'
import {ApplicationActionInfoDTO} from '../../../build/swagger-code-jems-api/model/applicationActionInfoDTO'
import {InputTranslation} from '../../../build/swagger-code-jems-api/model/inputTranslation'
import faker from "@faker-js/faker";
import user from '../fixtures/users.json';

declare global {

  interface Application {
    id: number,
    details: ProjectCreateDTO,
    identification: InputProjectData,
    partners: ProjectPartner[],
    description: ProjectDescription
  }

  interface ProjectPartner {
    details: ProjectPartnerDTO,
    address: ProjectPartnerAddressDTO[],
    contact: ProjectContactDTO[],
    motivation: ProjectPartnerMotivationDTO,
    budget: PartnerBudget,
    cofinancing: ProjectPartnerCoFinancingAndContributionInputDTO,
    stateAid: ProjectPartnerStateAidDTO
  }

  interface ProjectDescription {
    overallObjective: InputTranslation[],
    relevanceAndContext: InputProjectRelevance,
    partnership: InputTranslation[],
    workPlan: WorkPackage[],
    results: ProjectResultDTO[],
    management: InputProjectManagement,
    longTermPlans: InputProjectLongTermPlans
  }

  interface PartnerBudget {
    options: ProjectPartnerBudgetOptionsDto,
    external: BudgetGeneralCostEntryDTO[],
    equipment: BudgetGeneralCostEntryDTO[],
    infrastructure: BudgetGeneralCostEntryDTO[],
    unitcosts?: BudgetUnitCostEntryDTO[],
    travel: BudgetTravelAndAccommodationCostEntryDTO[]
  }

  interface WorkPackage {
    details: InputWorkPackageCreate,
    investment: WorkPackageInvestmentDTO,
    activities: WorkPackageActivityDTO[],
    outputs: WorkPackageOutputDTO[]
  }

  namespace Cypress {
    interface Chainable {
      createFullApplication(application: Application);

      runPreSubmissionCheck(applicationId: number);

      submitProjectApplication(applicationId: number);

      enterEligibilityAssessment(applicationId: number, assessment: ProjectAssessmentEligibilityDTO);

      enterQualityAssessment(applicationId: number, assessment: ProjectAssessmentQualityDTO);

      enterEligibilityDecision(applicationId: number, decision: ApplicationActionInfoDTO);

      enterFundingDecision(applicationId: number, decision: ApplicationActionInfoDTO);
    }
  }
}

Cypress.Commands.add('createFullApplication', (application: Application) => {
  // randomize name
  application.identification.acronym = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
  application.details.acronym = application.identification.acronym;
  cy.request({
    method: 'POST',
    url: 'api/project',
    auth: {'user': user.applicantUser.email, 'pass': Cypress.env('defaultPassword')},
    body: application.details
  }).then(function (response) {
    application.id = response.body.id;
    updateIdentification(application.id, application.identification);

    // C - project description
    updateOverallObjective(application.id, application.description.overallObjective);
    updateRelevanceAndContext(application.id, application.description.relevanceAndContext);
    updatePartnership(application.id, application.description.partnership);
    createWorkPlan(application.id, application.description.workPlan);
    createResults(application.id, application.description.results);
    updateManagement(application.id, application.description.management);
    updateLongTermPlans(application.id, application.description.longTermPlans);

    // B - project partners
    createPartners(application.id, application.partners);
    cy.wrap(application.id).as('applicationId');
  });
});

Cypress.Commands.add('runPreSubmissionCheck', (applicationId: number) => {
  cy.request({
    method: 'GET',
    url: `api/project/${applicationId}/preCheck`
  });
});

Cypress.Commands.add('submitProjectApplication', (applicationId: number) => {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/submit`
  });
});

Cypress.Commands.add('enterEligibilityAssessment', (applicationId: number, assessment: ProjectAssessmentEligibilityDTO) => {
  cy.request({
    method: 'POST',
    url: `api/project/${applicationId}/assessment/eligibility`,
    body: assessment
  });
});

Cypress.Commands.add('enterQualityAssessment', (applicationId: number, assessment: ProjectAssessmentQualityDTO) => {
  cy.request({
    method: 'POST',
    url: `api/project/${applicationId}/assessment/quality`,
    body: assessment
  });
});

Cypress.Commands.add('enterEligibilityDecision', (applicationId: number, decision: ApplicationActionInfoDTO) => {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/set-as-eligible`,
    body: decision
  });
});

Cypress.Commands.add('enterFundingDecision', (applicationId: number, decision: ApplicationActionInfoDTO) => {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/approve`,
    body: decision
  });
});


function updateIdentification(applicationId: number, projectIdentification: InputProjectData) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}`,
    body: projectIdentification
  });
}

function createPartners(applicationId: number, partners: ProjectPartner[]) {
  partners.forEach(function (partner) {
    cy.request({
      method: 'POST',
      url: `api/project/partner/toProjectId/${applicationId}`,
      body: partner.details
    }).then(function (response) {
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
      if (partner.budget.unitcosts) {
        cy.request({
          method: 'PUT',
          url: `api/project/partner/${response.body.id}/budget/unitcosts`,
          body: partner.budget.unitcosts
        });
      }
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/budget/travel`,
        body: partner.budget.travel
      });
      // Co-financing tab
      cy.request({
        method: 'PUT',
        url: `api/project/partner/${response.body.id}/budget/cofinancing`,
        body: partner.cofinancing
      });
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

function updateOverallObjective(applicationId: number, overallObjective: InputTranslation[]) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c1`,
    body: {overallObjective: overallObjective}
  });
}

function updateRelevanceAndContext(applicationId: number, relevanceAndContext: InputProjectRelevance) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c2`,
    body: relevanceAndContext
  });
}

function updatePartnership(applicationId: number, partnership: InputTranslation[]) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c3`,
    body: {partnership: partnership}
  });
}

function createWorkPlan(applicationId: number, workPlan: WorkPackage[]) {
  workPlan.forEach(workPackage => {
    cy.request({
      method: 'POST',
      url: `api/project/${applicationId}/workPackage`,
      body: workPackage.details
    }).then(result => {
      cy.request({
        method: 'POST',
        url: `api/project/${applicationId}/workPackage/${result.body.id}/investment`,
        body: workPackage.investment
      }).then(response => {
        cy.wrap(response.body).as('investmentId');
      });
      cy.request({
        method: 'PUT',
        url: `api/project/${applicationId}/workPackage/${result.body.id}/activity`,
        body: workPackage.activities
      }).then(response => {
        // TODO request does not return proper id
        cy.wrap(response.body.id).as('activityId');
      });
      cy.request({
        method: 'PUT',
        url: `api/project/${applicationId}/workPackage/${result.body.id}/output`,
        body: workPackage.outputs
      });
    });
  });
}

function createResults(applicationId: number, results: ProjectResultDTO[]) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/result`,
    body: results
  });
}

function updateManagement(applicationId: number, management: InputProjectManagement) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c7`,
    body: management
  });
}

function updateLongTermPlans(applicationId: number, longTermPlans: InputProjectLongTermPlans) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c8`,
    body: longTermPlans
  });
}

export {}
