import {faker} from '@faker-js/faker';
import {createPartners, updatePartnerData, createAssociatedOrganisations} from './partner.commands';
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

      createProjectProposedUnitCosts(applicationId: number, unitCosts: any[]);

      createAssociatedOrganisation(applicationId: number, associatedOrganisation);

      createAssociatedOrganisations(applicationId: number, associatedOrganisation: any[]);

      getProgrammeUnitCostsEnabledInCall(callId: number);

      getProjectUnitCosts(applicationId: number);

      getProjectProposedUnitCosts(applicationId: number);

      getContractMonitoring(applicationId: number);

      updateContractMonitoring(applicationId: number, contractMonitoring: {});
    }
  }
}

Cypress.Commands.add('createApplication', (application) => {
  createApplication(application.details).then(applicationId => {
    cy.wrap(applicationId).as('applicationId');
  });
});

Cypress.Commands.add('createSubmittedApplication', application => {
  createApplication(application.details).then(applicationId => {
    updateApplicationSections(applicationId, application);
    runPreSubmissionCheck(applicationId);
    submitProjectApplication(applicationId);
    cy.wrap(applicationId).as('applicationId');
  });
});

Cypress.Commands.add('createApprovedApplication', (application, approvingUserEmail?: string) => {
  createApplication(application.details).then(applicationId => {
    updateApplicationSections(applicationId, application);
    runPreSubmissionCheck(applicationId);
    submitProjectApplication(applicationId);
    approveApplication(applicationId, application.assessments, approvingUserEmail);
    cy.wrap(applicationId).as('applicationId');
  });
});

Cypress.Commands.add('createContractedApplication', (application, contractingUserEmail?: string) => {
  createApplication(application.details).then(applicationId => {
    updateApplicationSections(applicationId, application);
    runPreSubmissionCheck(applicationId);
    submitProjectApplication(applicationId);
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
  createWorkPlan(applicationId, workPlan);
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
  createProjectProposedUnitCost(applicationId, unitCost);
});

Cypress.Commands.add('createProjectProposedUnitCosts', (applicationId: number, unitCosts: []) => {
  unitCosts.forEach(unitCost => {
    createProjectProposedUnitCost(applicationId, unitCost);
  });
});

Cypress.Commands.add('getProgrammeUnitCostsEnabledInCall', (callId: number) => {
  cy.request({
    method: 'GET',
    url: `api/call/byId/${callId}`
  }).then(response => response.body.unitCosts);
});

Cypress.Commands.add('getProjectUnitCosts', (applicationId: number) => {
  cy.request({
    method: 'GET',
    url: `api/project/${applicationId}/budget/unitCosts`
  }).then(response => response.body);
});

Cypress.Commands.add('getProjectProposedUnitCosts', (applicationId: number) => {
  cy.request({
    method: 'GET',
    url: `api/project/${applicationId}/costOption/unitCost?version=1.0`
  }).then(response => response.body);
});

Cypress.Commands.add('getContractMonitoring', (applicationId: number) => {
  getContractMonitoring(applicationId);
});

Cypress.Commands.add('updateContractMonitoring', (applicationId: number, contractMonitoring: {}) => {
  updateContractMonitoring(applicationId, contractMonitoring);
});

function createApplication(applicationDetails) {
  applicationDetails.acronym = `${faker.hacker.adjective()} ${faker.hacker.noun()}`.substr(0, 25);
  return cy.request({
    method: 'POST',
    url: 'api/project',
    body: applicationDetails
  }).then(response => response.body.id);
}

function updateApplicationSections(applicationId, application) {

  // A
  updateIdentification(applicationId, application.identification);

  // Partial B
  createPartners(applicationId, application.partners);

  // C
  updateOverallObjective(applicationId, application.description.overallObjective);
  updateRelevanceAndContext(applicationId, application.description.relevanceAndContext);
  updatePartnership(applicationId, application.description.partnership);
  createWorkPlan(applicationId, application.description.workPlan);
  createResults(applicationId, application.description.results);
  updateManagement(applicationId, application.description.management);
  updateLongTermPlans(applicationId, application.description.longTermPlans);

  // E
  createProjectProposedUnitCosts(applicationId, application.projectProposedUnitCosts);

  // B
  application.partners.forEach(partner => {
    cy.then(function () {
      updatePartnerData(this[partner.details.abbreviation], partner);
    });
  });
  createAssociatedOrganisations(applicationId, application.associatedOrganisations);

  // E
  updateLumpSums(applicationId, application.lumpSums);
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
  workPlan.forEach(workPackage => {
    cy.request({
      method: 'POST',
      url: `api/project/${applicationId}/workPackage`,
      body: workPackage.details
    }).then(result => {
      cy.wrap(result.body.id).as('workPlanId');
      if (workPackage.investments) {
        workPackage.investments.forEach(investment => {
          cy.request({
            method: 'POST',
            url: `api/project/${applicationId}/workPackage/${result.body.id}/investment`,
            body: investment
          }).then(response => {
            if (investment.cypressReferenceInvestment) {
              cy.wrap(response.body).as(investment.cypressReferenceInvestment);
            }
          });
        });
      }

      // update work plan activities with partnerIds
      matchPartnersToActivities(workPackage.activities);
      cy.request({
        method: 'PUT',
        url: `api/project/${applicationId}/workPackage/${result.body.id}/activity`,
        body: workPackage.activities
      }).then(response => {
        response.body.forEach((activity, index) => {
          if (workPackage.activities[index].cypressReferenceStateAid) {
            cy.wrap(activity.id).as(workPackage.activities[index].cypressReferenceStateAid);
          }
        });
      });
      cy.request({
        method: 'PUT',
        url: `api/project/${applicationId}/workPackage/${result.body.id}/output`,
        body: workPackage.outputs
      });
    });
  });
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
  // set partnerId in the lump sum based on partner abbreviation
  cy.then(function () {
    lumpSums?.forEach((lumpSum: any) => {
      lumpSum.lumpSumContributions.forEach(contributions => {
        contributions.partnerId = this[contributions.partnerAbbreviation];
      });
    });
    cy.request({
      method: 'PUT',
      url: `api/project/${applicationId}/lumpSum`,
      body: lumpSums
    });
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

function createProjectProposedUnitCost(applicationId, projectProposedUnitCost) {
  cy.request({
    method: 'POST',
    url: `api/project/${applicationId}/costOption/unitCost`,
    body: projectProposedUnitCost
  }).then(response => {
    cy.wrap(response.body.id).as(projectProposedUnitCost.cypressReferenceUnit);
  });
}

function createProjectProposedUnitCosts(applicationId: number, projectProposedUnitCosts: any[]) {
  projectProposedUnitCosts.forEach((projectProposedUnitCost: any) => {
    createProjectProposedUnitCost(applicationId, projectProposedUnitCost);
  });
}

function matchPartnersToActivities(activities) {
  cy.then(function () {
    activities.forEach(activity => {
      if (activity.cypressReferencePartner) {
        activity.partnerIds = [this[activity.cypressReferencePartner]];
      }
    });
  });
}

function getContractMonitoring(applicationId) {
  cy.request({
    method: 'GET',
    url: `api/project/${applicationId}/contracting/monitoring`,
  }).then((response) => response.body);
}

function updateContractMonitoring(applicationId, contractMonitoring) {
  cy.request({
    method: 'PUT',
    url: `api/project/${applicationId}/contracting/monitoring`,
    body: contractMonitoring
  });
}

export {}
