import {faker} from '@faker-js/faker';
import {createPartners} from './partner.commands';
import {loginByRequest} from './login.commands';

declare global {

  namespace Cypress {
    interface Chainable {
      createApplication(application);

      createSubmittedApplication(application);

      createApprovedApplication(application, approvingUserEmail?: string);

      createContractedApplication(application, contractingUserEmail?: string);

      updateProjectIdentification(applicationId: number, identification);

      updateProjectOverallObjective(applicationId: number, overallObjective);

      updateProjectRelevanceAndContext(applicationId: number, relevanceAndContext);

      updateProjectPartnership(applicationId: number, partnership);

      createProjectWorkPlan(applicationId: number, workPlan);

      createProjectResults(applicationId: number, results);

      updateProjectManagement(applicationId: number, management);

      updateProjectLongTermPlans(applicationId: number, longTermPlans);

      updateLumpSums(applicationId: number, lumpSums);

      runPreSubmissionCheck(applicationId: number);

      submitProjectApplication(applicationId: number);

      enterEligibilityAssessment(applicationId: number, assessment);

      enterQualityAssessment(applicationId: number, assessment);

      enterEligibilityDecision(applicationId: number, decision);

      enterFundingDecision(applicationId: number, decision);

      approveApplication(applicationId: number, assessment, approvingUserEmail?: string);

      startSecondStep(applicationId: number, userEmail?: string);

      startModification(applicationId: number, userEmail?: string);

      approveModification(applicationId: number, approvalInfo, userEmail?: string);

      rejectModification(applicationId: number, rejectionInfo, userEmail?: string);

      setProjectToContracted(applicationId: number, userEmail?: string);

      assignPartnerCollaborators(applicationId: number, partnerId: number, users: string[]);

      returnToApplicant(applicationId: number, userEmail?: string);

      createProjectProposedUnitCost(applicationId: number, unitCost);
    }
  }
}

Cypress.Commands.add('createApplication', (application) => {
  createApplication(application.details).then(applicationId => {
    cy.wrap(applicationId).as('applicationId');
  });
});

Cypress.Commands.add('createSubmittedApplication', (application) => {
  createApplication(application.details).then(applicationId => {
    submitApplication(applicationId, application);
    cy.wrap(applicationId).as('applicationId');
  });
});

Cypress.Commands.add('createApprovedApplication', (application, approvingUserEmail?: string) => {
  createApplication(application.details).then(applicationId => {
    submitApplication(applicationId, application);
    approveApplication(applicationId, application.assessments, approvingUserEmail);
    cy.wrap(applicationId).as('applicationId');
  });
});

Cypress.Commands.add('createContractedApplication', (application, contractingUserEmail?: string) => {
  createApplication(application.details).then(applicationId => {
    submitApplication(applicationId, application);
    if (contractingUserEmail)
      loginByRequest(contractingUserEmail);
    approveApplication(applicationId, application.assessments);
    cy.request({
      method: 'PUT',
      url: `api/project/${applicationId}/set-to-contracted`
    });
    if (contractingUserEmail) {
      cy.get('@currentUser').then((currentUser: any) => {
        loginByRequest(currentUser.name);
      });
    }
    cy.wrap(applicationId).as('applicationId');
  });
});

Cypress.Commands.add('approveApplication', (applicationId: number, assessment, approvingUserEmail?: string) => {
  approveApplication(applicationId, assessment, approvingUserEmail);
});

/* A - Project identification */

Cypress.Commands.add('updateProjectIdentification', (applicationId: number, identification) => {
  updateIdentification(applicationId, identification);
});

/* C - Project description */

Cypress.Commands.add('updateProjectOverallObjective', (applicationId: number, overallObjective: []) => {
  updateOverallObjective(applicationId, overallObjective);
});

Cypress.Commands.add('updateProjectRelevanceAndContext', (applicationId: number, relevanceAndContext) => {
  updateRelevanceAndContext(applicationId, relevanceAndContext);
});

Cypress.Commands.add('updateProjectPartnership', (applicationId: number, partnership: []) => {
  updatePartnership(applicationId, partnership);
});

Cypress.Commands.add('createProjectWorkPlan', (applicationId: number, workPlan: []) => {
  const options = createWorkPlan(applicationId, workPlan);
  cy.wrap(options).as('options');
});

Cypress.Commands.add('createProjectResults', (applicationId: number, projectResults: []) => {
  createResults(applicationId, projectResults);
});

Cypress.Commands.add('updateProjectManagement', (applicationId: number, management) => {
  updateManagement(applicationId, management);
});

Cypress.Commands.add('updateProjectLongTermPlans', (applicationId: number, longTermPlans) => {
  updateLongTermPlans(applicationId, longTermPlans);
});

Cypress.Commands.add('updateLumpSums', (applicationId: number, lumpSums: []) => {
  updateLumpSums(applicationId, lumpSums);
});

Cypress.Commands.add('runPreSubmissionCheck', (applicationId: number) => {
  runPreSubmissionCheck(applicationId);
});

Cypress.Commands.add('submitProjectApplication', (applicationId: number) => {
  submitProjectApplication(applicationId);
});

Cypress.Commands.add('enterEligibilityAssessment', (applicationId: number, assessment) => {
  enterEligibilityAssessment(applicationId, assessment);
});

Cypress.Commands.add('enterQualityAssessment', (applicationId: number, assessment) => {
  enterQualityAssessment(applicationId, assessment);
});

Cypress.Commands.add('enterEligibilityDecision', (applicationId: number, decision) => {
  enterEligibilityDecision(applicationId, decision);
});

Cypress.Commands.add('enterFundingDecision', (applicationId: number, decision) => {
  enterFundingDecision(applicationId, decision);
});

Cypress.Commands.add('startSecondStep', (applicationId: number, userEmail?: string) => {
  if (userEmail)
    loginByRequest(userEmail);
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/start-second-step`
  });
  if (userEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
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

Cypress.Commands.add('approveModification', (applicationId: number, approvalInfo, userEmail?: string) => {
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

Cypress.Commands.add('rejectModification', (applicationId: number, rejectionInfo, userEmail?: string) => {
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

Cypress.Commands.add('setProjectToContracted', (applicationId: number, userEmail?: string) => {
  if (userEmail)
    loginByRequest(userEmail);
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/set-to-contracted`
  });
  if (userEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
});

Cypress.Commands.add('assignPartnerCollaborators', (applicationId: number, partnerId: number, users: string[]) => {
  cy.request({
    method: 'PUT',
    url: `api/projectPartnerCollaborators/forProject/${applicationId}/forPartner/${partnerId}`,
    body: users
  });
});

Cypress.Commands.add('returnToApplicant', (applicationId: number, userEmail?: string) => {
  if (userEmail)
    loginByRequest(userEmail);
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/return-to-applicant`
  });
  if (userEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
});

Cypress.Commands.add('createProjectProposedUnitCost', (applicationId: number, unitCost) => {
  cy.request({
    method: 'POST',
    url: `api/project/${applicationId}/costOption/unitCost`,
    body: unitCost
  }).then(response => {
    return response.body.id;
  });
});

function createApplication(applicationDetails) {
  applicationDetails.acronym = `${faker.hacker.adjective()} ${faker.hacker.noun()}`.substr(0, 25);
  return cy.request({
    method: 'POST',
    url: 'api/project',
    body: applicationDetails
  }).then(response => {
    return response.body.id;
  });
}

function submitApplication(applicationId, application) {

  updateIdentification(applicationId, application.identification);

  // C - project description
  updateOverallObjective(applicationId, application.description.overallObjective);
  updateRelevanceAndContext(applicationId, application.description.relevanceAndContext);
  updatePartnership(applicationId, application.description.partnership);
  const options = createWorkPlan(applicationId, application.description.workPlan);
  createResults(applicationId, application.description.results);
  updateManagement(applicationId, application.description.management);
  updateLongTermPlans(applicationId, application.description.longTermPlans);

  // B - project partners
  createPartners(applicationId, application.partners, options);

  // E - project lump sums
  cy.then(function () {
    application.lumpSums.forEach(lumpSum => {
      lumpSum.lumpSumContributions.forEach(contributions => {
        contributions.partnerId = this[contributions.partnerAbbreviation];
      });
    });
    updateLumpSums(applicationId, application.lumpSums);
  });

  runPreSubmissionCheck(applicationId);
  submitProjectApplication(applicationId);
}

function updateIdentification(applicationId: number, projectIdentification) {
  projectIdentification.acronym = `${faker.hacker.adjective()} ${faker.hacker.noun()}`.substr(0, 25);
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}`,
    body: projectIdentification
  });
}

function updateOverallObjective(applicationId: number, overallObjective: []) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c1`,
    body: {overallObjective: overallObjective}
  });
}

function updateRelevanceAndContext(applicationId: number, relevanceAndContext) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c2`,
    body: relevanceAndContext
  });
}

function updatePartnership(applicationId: number, partnership: []) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c3`,
    body: {partnership: partnership}
  });
}

function createWorkPlan(applicationId: number, workPlan: any[]) {
  const options = {workPlanId: null, activityId: null};
  workPlan.forEach(workPackage => {
    cy.request({
      method: 'POST',
      url: `api/project/${applicationId}/workPackage`,
      body: workPackage.details
    }).then(result => {
      cy.wrap(result.body.id).as('workPlanId');
      options.workPlanId = result.body.id;
      if (workPackage.investment) {
        cy.request({
          method: 'POST',
          url: `api/project/${applicationId}/workPackage/${result.body.id}/investment`,
          body: workPackage.investment
        }).then(response => {
          cy.wrap(response.body).as('investmentId');
        });
      }
      cy.request({
        method: 'PUT',
        url: `api/project/${applicationId}/workPackage/${result.body.id}/activity`,
        body: workPackage.activities
      }).then(response => {
        cy.wrap(response.body[0].id).as('activityId');
        options.activityId = response.body[0].id;
      });
      cy.request({
        method: 'PUT',
        url: `api/project/${applicationId}/workPackage/${result.body.id}/output`,
        body: workPackage.outputs
      });
    });
  });
  return options;
}

function createResults(applicationId: number, results: []) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/result`,
    body: results
  });
}

function updateManagement(applicationId: number, management) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c7`,
    body: management
  });
}

function updateLongTermPlans(applicationId: number, longTermPlans) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/description/c8`,
    body: longTermPlans
  });
}

function updateLumpSums(applicationId: number, lumpSums: []) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/lumpSum`,
    body: lumpSums
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

function approveApplication(applicationId: number, assessments, approvingUserEmail?: string) {
  if (approvingUserEmail)
    loginByRequest(approvingUserEmail);
  enterEligibilityAssessment(applicationId, assessments.eligibilityAssessment);
  enterQualityAssessment(applicationId, assessments.qualityAssessment);
  enterEligibilityDecision(applicationId, assessments.eligibilityDecision);
  enterFundingDecision(applicationId, assessments.fundingDecision);
  if (approvingUserEmail) {
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
    });
  }
}

export {}
