import {ProjectCreateDTO} from '../../../build/swagger-code-jems-api/model/projectCreateDTO'
import {InputProjectData} from '../../../build/swagger-code-jems-api/model/inputProjectData'
import {InputProjectRelevance} from '../../../build/swagger-code-jems-api/model/inputProjectRelevance'
import {InputWorkPackageCreate} from '../../../build/swagger-code-jems-api/model/inputWorkPackageCreate'
import {WorkPackageInvestmentDTO} from '../../../build/swagger-code-jems-api/model/workPackageInvestmentDTO'
import {WorkPackageActivityDTO} from '../../../build/swagger-code-jems-api/model/workPackageActivityDTO'
import {WorkPackageOutputDTO} from '../../../build/swagger-code-jems-api/model/workPackageOutputDTO'
import {ProjectResultDTO} from '../../../build/swagger-code-jems-api/model/projectResultDTO'
import {InputProjectManagement} from '../../../build/swagger-code-jems-api/model/inputProjectManagement'
import {InputProjectLongTermPlans} from '../../../build/swagger-code-jems-api/model/inputProjectLongTermPlans'
import {
  ProjectAssessmentEligibilityDTO
} from '../../../build/swagger-code-jems-api/model/projectAssessmentEligibilityDTO'
import {ProjectAssessmentQualityDTO} from '../../../build/swagger-code-jems-api/model/projectAssessmentQualityDTO'
import {ApplicationActionInfoDTO} from '../../../build/swagger-code-jems-api/model/applicationActionInfoDTO'
import {InputTranslation} from '../../../build/swagger-code-jems-api/model/inputTranslation'
import faker from '@faker-js/faker';
import {createPartners} from './partner.commands';

declare global {

  interface Application {
    id: number,
    details: ProjectCreateDTO,
    identification?: InputProjectData,
    partners?: ProjectPartner[],
    description?: ProjectDescription
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

  interface WorkPackage {
    details: InputWorkPackageCreate,
    investment: WorkPackageInvestmentDTO,
    activities: WorkPackageActivityDTO[],
    outputs: WorkPackageOutputDTO[]
  }

  namespace Cypress {
    interface Chainable {
      createFullApplication(application, userEmail: string);

      createApplication(application, userEmail: string);

      runPreSubmissionCheck(applicationId: number);

      submitProjectApplication(applicationId: number);

      enterEligibilityAssessment(applicationId: number, assessment);

      enterQualityAssessment(applicationId: number, assessment);

      enterEligibilityDecision(applicationId: number, decision);

      enterFundingDecision(applicationId: number, decision);

      startModification(applicationId: number);

      approveModification(applicationId: number, approve);
    }
  }
}

Cypress.Commands.add('createApplication', (application: Application, userEmail: string) => {
  createApplication(application.details, userEmail).then(function (response) {
    application.id = response.body.id;
    cy.wrap(application.id).as('applicationId');
  });
});

Cypress.Commands.add('createFullApplication', (application: Application, userEmail: string) => {
  createApplication(application.details, userEmail).then(function (response) {
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

Cypress.Commands.add('startModification', (applicationId: number) => {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/start-modification`,
  });
});

Cypress.Commands.add('approveModification', (applicationId: number, approve: ApplicationActionInfoDTO) => {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/approve modification`,
    body: approve
  });
});

function createApplication(applicationDetails: ProjectCreateDTO, userEmail: string) {
  if (applicationDetails.acronym === 'randomize') {
    applicationDetails.acronym = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
  }
  return cy.request({
    method: 'POST',
    url: 'api/project',
    auth: {'user': userEmail, 'pass': Cypress.env('defaultPassword')},
    body: applicationDetails
  })
}

function updateIdentification(applicationId: number, projectIdentification: InputProjectData) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}`,
    body: projectIdentification
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
