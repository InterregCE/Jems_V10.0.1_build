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
import user from '../fixtures/users.json';
import {loginByRequest} from './login.commands';

declare global {

  interface Application {
    id: number,
    details: ProjectCreateDTO,
    identification?: InputProjectData,
    partners?: ProjectPartner[],
    description?: ProjectDescription,
    assessments: {}
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

      updateProjectIdentification(applicationId: number, identification);

      updateProjectOverallObjective(applicationId: number, overallObjective);

      updateProjectRelevanceAndContext(applicationId: number, relevanceAndContext);

      updateProjectPartnership(applicationId: number, partnership);

      createProjectWorkPlan(applicationId: number, workPlan);

      createProjectResults(applicationId: number, results);

      updateProjectManagement(applicationId: number, management);

      updateProjectLongTermPlans(applicationId: number, longTermPlans);

      runPreSubmissionCheck(applicationId: number);

      submitProjectApplication(applicationId: number);

      enterEligibilityAssessment(applicationId: number, assessment);

      enterQualityAssessment(applicationId: number, assessment);

      enterEligibilityDecision(applicationId: number, decision);

      enterFundingDecision(applicationId: number, decision);

      approveApplication(applicationId: number, assessment);

      startSecondStep(applicationId: number);

      startModification(applicationId: number, userEmail?: string);

      approveModification(applicationId: number, approvalInfo, userEmail?: string);

      rejectModification(applicationId: number, rejectionInfo, userEmail?: string);
    }
  }
}

Cypress.Commands.add('createApplication', (application: Application, userEmail: string) => {
  createApplication(application.details, userEmail).then(function (response) {
    application.id = response.body.id;
    cy.wrap(application.id).as('applicationId');
  });
});

Cypress.Commands.add('createFullApplication', function (application: Application, userEmail: string) {
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

    approveApplication(application.id, application.assessments);
    cy.wrap(application.id).as('applicationId');
  });
});

Cypress.Commands.add('approveApplication', (applicationId: number, assessment: InputProjectData) => {
  approveApplication(applicationId, assessment);
});

/* A - Project identification */

Cypress.Commands.add('updateProjectIdentification', (applicationId: number, identification: InputProjectData) => {
  updateIdentification(applicationId, identification);
});

/* C - Project description */

Cypress.Commands.add('updateProjectOverallObjective', (applicationId: number, overallObjective: InputTranslation[]) => {
  updateOverallObjective(applicationId, overallObjective);
});

Cypress.Commands.add('updateProjectRelevanceAndContext', (applicationId: number, relevanceAndContext: InputProjectRelevance) => {
  updateRelevanceAndContext(applicationId, relevanceAndContext);
});

Cypress.Commands.add('updateProjectPartnership', (applicationId: number, partnership: InputTranslation[]) => {
  updatePartnership(applicationId, partnership);
});

Cypress.Commands.add('createProjectWorkPlan', (applicationId: number, workPlan: WorkPackage[]) => {
  createWorkPlan(applicationId, workPlan);
});

Cypress.Commands.add('createProjectResults', (applicationId: number, projectResults: ProjectResultDTO[]) => {
  createResults(applicationId, projectResults);
});

Cypress.Commands.add('updateProjectManagement', (applicationId: number, management: InputProjectManagement) => {
  updateManagement(applicationId, management);
});

Cypress.Commands.add('updateProjectLongTermPlans', (applicationId: number, longTermPlans: InputProjectLongTermPlans) => {
  updateLongTermPlans(applicationId, longTermPlans);
});

Cypress.Commands.add('runPreSubmissionCheck', (applicationId: number) => {
  runPreSubmissionCheck(applicationId);
});

Cypress.Commands.add('submitProjectApplication', (applicationId: number) => {
  submitProjectApplication(applicationId);
});

Cypress.Commands.add('enterEligibilityAssessment', (applicationId: number, assessment: ProjectAssessmentEligibilityDTO) => {
  enterEligibilityAssessment(applicationId, assessment);
});

Cypress.Commands.add('enterQualityAssessment', (applicationId: number, assessment: ProjectAssessmentQualityDTO) => {
  enterQualityAssessment(applicationId, assessment);
});

Cypress.Commands.add('enterEligibilityDecision', (applicationId: number, decision: ApplicationActionInfoDTO) => {
  enterEligibilityDecision(applicationId, decision);
});

Cypress.Commands.add('enterFundingDecision', (applicationId: number, decision: ApplicationActionInfoDTO) => {
  enterFundingDecision(applicationId, decision);
});

Cypress.Commands.add('startSecondStep', (applicationId: number) => {
  loginByRequest(user.programmeUser.email);
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/start-second-step`
  });
  cy.get('@currentUser').then((currentUser: any) => {
    loginByRequest(currentUser.name);
  });
});

Cypress.Commands.add('startModification', (applicationId: number, userEmail?: string) => {
  if (userEmail)
    loginByRequest(userEmail);
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/start-modification`,
  });
  if (userEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
});

Cypress.Commands.add('approveModification', (applicationId: number, approvalInfo: ApplicationActionInfoDTO, userEmail?: string) => {
  if (userEmail)
    loginByRequest(userEmail);
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/approve modification`,
    body: approvalInfo
  });
  if (userEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
});

Cypress.Commands.add('rejectModification', (applicationId: number, rejectionInfo: ApplicationActionInfoDTO, userEmail?: string) => {
  if (userEmail)
    loginByRequest(userEmail);
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/reject`,
    body: rejectionInfo
  });
  if (userEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
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
  if (projectIdentification.acronym === 'randomize') {
    projectIdentification.acronym = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
  }
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
        cy.wrap(response.body[0].id).as('activityId');
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

function runPreSubmissionCheck(applicationId: number) {
  cy.request({
    method: 'GET',
    url: `api/project/${applicationId}/preCheck`
  });
}

function submitProjectApplication(applicationId: number) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/submit`
  });
}

function enterEligibilityAssessment(applicationId: number, assessment) {
  cy.request({
    method: 'POST',
    url: `api/project/${applicationId}/assessment/eligibility`,
    body: assessment
  });
}

function enterQualityAssessment(applicationId: number, assessment) {
  cy.request({
    method: 'POST',
    url: `api/project/${applicationId}/assessment/quality`,
    body: assessment
  });
}

function enterEligibilityDecision(applicationId, decision) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/set-as-eligible`,
    body: decision
  });
}

function enterFundingDecision(applicationId, decision) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/approve`,
    body: decision
  });
}

function approveApplication(applicationId: number, assessments) {
  runPreSubmissionCheck(applicationId);
  submitProjectApplication(applicationId);
  loginByRequest(user.programmeUser.email);
  enterEligibilityAssessment(applicationId, assessments.eligibilityAssessment);
  enterQualityAssessment(applicationId, assessments.qualityAssessment);
  enterEligibilityDecision(applicationId, assessments.eligibilityDecision);
  enterFundingDecision(applicationId, assessments.fundingDecision);
  cy.get('@currentUser').then((currentUser: any) => {
    loginByRequest(currentUser.name);
  });
}

export {}
