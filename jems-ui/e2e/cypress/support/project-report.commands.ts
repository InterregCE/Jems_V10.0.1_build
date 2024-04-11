import user from "@fixtures/users.json";

declare global {

  namespace Cypress {
    interface Chainable {
      createProjectReport(applicationId: number, projectReportDetails: any): any;

      completeReporting(applicationId: number, reporting: any): any;

      createVerifiedProjectReport(applicationId: number, projectReportDetails: any, verificationUserEmail: string): any;

      updateProjectReportIdentification(applicationId: number, reportId: number, projectReportIdentification: any): any;

      updateProjectReportWorkPlan(applicationId: number, reportId: number, projectReportWorkPlan: any[]);

      updateProjectReportResults(applicationId: number, reportId: number, projectReportResults: any[]);

      updateProjectReportExpenditureVerificationRisk(applicationId: number, reportId: number, projectReportRisk: any): any;

      updateProjectReportExpenditureVerification(applicationId: number, reportId: number, projectReportExpenditureVerification: any): any;

      updateProjectReportClarifications(applicationId: number, reportId: number, projectReportClarifications: any): any;

      updateProjectReportVerification(applicationId: number, reportId: number, projectReportVerification: any): any;

      runProjectReportPreSubmissionCheck(applicationId: number, reportId: number);

      submitProjectReport(applicationId: number, reportId: number);

      startProjectReportVerification(applicationId: number, reportId: number);

      finalizeProjectReportVerification(applicationId: number, reportId: number);
    }
  }
}

Cypress.Commands.add('createProjectReport', (applicationId: number, projectReportIdentification) => {
  matchReportingDeadlineIds(projectReportIdentification);
  cy.request({
    method: 'POST',
    url: `api/project/report/byProjectId/${applicationId}`,
    body: projectReportIdentification,
  }).then(response => response.body.id);
});

Cypress.Commands.add('completeReporting', function (applicationId: number, reporting) {
  reporting.projectReports.forEach(projectReport => {
    projectReport.partnerReports.forEach(partnerReport => {
      const partnerId = this[partnerReport.partnerAcronym];
      cy.createCertifiedPartnerReport(partnerId, partnerReport, user.controllerUser.email);
    });
    cy.createVerifiedProjectReport(applicationId, projectReport, user.verificationUser.email);
  });
});

Cypress.Commands.add('createVerifiedProjectReport', (applicationId: number, projectReportDetails, verificationUserEmail) => {
  cy.createProjectReport(applicationId, projectReportDetails.projectReport.details).then(projectReportId => {
    cy.updateProjectReportIdentification(applicationId, projectReportId, projectReportDetails.projectReport.identification);
    cy.updateProjectReportWorkPlan(applicationId, projectReportId, projectReportDetails.projectReport.workPlan);
    cy.updateProjectReportResults(applicationId, projectReportId, projectReportDetails.projectReport.results);
    cy.submitProjectReport(applicationId, projectReportId);

    cy.loginByRequest(verificationUserEmail, false);
    cy.startProjectReportVerification(applicationId, projectReportId);
    cy.updateProjectReportExpenditureVerificationRisk(applicationId, projectReportId, projectReportDetails.verificationWork.risk);
    cy.updateProjectReportExpenditureVerification(applicationId, projectReportId, projectReportDetails.verificationWork.expenditures);
    cy.updateProjectReportClarifications(applicationId, projectReportId, projectReportDetails.verificationWork.clarifications);
    cy.updateProjectReportVerification(applicationId, projectReportId, projectReportDetails.verificationWork.verification);
    cy.finalizeProjectReportVerification(applicationId, projectReportId);
    cy.get('@currentUser').then((currentUser: any) => {
      cy.loginByRequest(currentUser.name, false);
    });
    cy.wrap(projectReportId);
  });
});

Cypress.Commands.add('runProjectReportPreSubmissionCheck', (applicationId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/preCheck`,
  });
});

Cypress.Commands.add('submitProjectReport', (applicationId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/submit`,
  });
});

Cypress.Commands.add('startProjectReportVerification', (applicationId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/startVerification`,
  });
});

Cypress.Commands.add('updateProjectReportIdentification', (applicationId: number, projectReportId: number, projectReportIdentification) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/identification/byProjectId/${applicationId}/byReportId/${projectReportId}`,
    body: projectReportIdentification
  });
});

Cypress.Commands.add('updateProjectReportWorkPlan', (applicationId: number, projectReportId: number, projectReportWorkPlan: any[]) => {
  getApplicationWorkPlan(applicationId, projectReportId).then(workPlan => {
    workPlan.forEach((workPackage, i) => {
      projectReportWorkPlan[i].id = workPackage.id;
      workPackage.activities.forEach((activity, k) => {
        projectReportWorkPlan[i].activities[k].id = activity.id;
        activity.deliverables.forEach((deliverable, j) => {
          projectReportWorkPlan[i].activities[k].deliverables[j].id = deliverable.id;
        });
      });
      workPackage.outputs.forEach((output, k) => {
        projectReportWorkPlan[i].outputs[k].id = output.id;
      });
      workPackage.investments.forEach((investment, k) => {
        projectReportWorkPlan[i].investments[k].id = investment.id;
      });
    });
    cy.request({
      method: 'PUT',
      url: `api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/workPlan`,
      body: projectReportWorkPlan
    });
  });
});

Cypress.Commands.add('updateProjectReportResults', (applicationId: number, projectReportId: number, projectReportResults) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/resultPrinciple`,
    body: projectReportResults
  });
});

Cypress.Commands.add('updateProjectReportExpenditureVerificationRisk', (applicationId: number, projectReportId: number, projectReportExpenditureVerificationRisk) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/verification/expenditure/riskBased`,
    body: projectReportExpenditureVerificationRisk
  });
});

Cypress.Commands.add('updateProjectReportExpenditureVerification', (applicationId: number, projectReportId: number, projectReportExpenditureVerification) => {
  getProjectReportExpenditures(applicationId, projectReportId).then(expenditureList => {
    expenditureList.forEach((expenditure, i) => {
      projectReportExpenditureVerification[i].expenditureId = expenditure.expenditure.id;
    });
    cy.request({
      method: 'PUT',
      url: `api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/verification/expenditure`,
      body: projectReportExpenditureVerification
    }).then(response => response.body);
  });
});

Cypress.Commands.add('updateProjectReportClarifications', (applicationId: number, projectReportId: number, projectReportClarifications) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/verification/clarifications`,
    body: projectReportClarifications
  });
});

Cypress.Commands.add('updateProjectReportVerification', (applicationId: number, projectReportId: number, projectReportVerification) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/verification`,
    body: projectReportVerification
  });
});

Cypress.Commands.add('finalizeProjectReportVerification', (applicationId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/finalizeVerification`,
  });
});

function getProjectReportExpenditures(applicationId: number, projectReportId: number): any {
  return cy.request(`/api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/verification/expenditure`).then(response => {
    return response.body
  });
}

function getApplicationWorkPlan(applicationId: number, projectReportId: number): any {
  return cy.request(`api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/workPlan`).then(response => {
    return response.body
  });
}

function matchReportingDeadlineIds(projectReportIdentification) {
  if (projectReportIdentification.cypressDeadlineReference) {
    cy.then(function () {
      projectReportIdentification.deadlineId = this[projectReportIdentification.cypressDeadlineReference]
    });
  }
}

export {};
