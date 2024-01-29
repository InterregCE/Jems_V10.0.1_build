import {loginByRequest} from "./login.commands";

declare global {

  namespace Cypress {
    interface Chainable {
      createProjectReport(applicationId: number, projectReportIdentification);
      
      createVerifiedProjectReport(applicationId: number, projectReportDetails: any, verificationUserEmail: string): any;

      runProjectReportPreSubmissionCheck(applicationId: number, reportId: number);

      submitProjectReport(applicationId: number, reportId: number);

      startProjectReportVerification(applicationId: number, reportId: number);

      updateProjectReportExpenditureVerification(applicationId: number, reportId: number, partnerReportExpenditures)

      finalizeProjectReportVerification(applicationId: number, reportId: number);
    }
  }
}

Cypress.Commands.add('createProjectReport', (applicationId: number, projectReportIdentification) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/byProjectId/${applicationId}`,
    body: projectReportIdentification,
  }).then(response => response.body.id);
});

Cypress.Commands.add('createVerifiedProjectReport', (applicationId: number, projectReportDetails, verificationUserEmail) => {
  cy.createProjectReport(applicationId, projectReportDetails.financeDeadline).then(projectReportId => {
    cy.submitProjectReport(applicationId, projectReportId);
    loginByRequest(verificationUserEmail);
    cy.startProjectReportVerification(applicationId, projectReportId);
    getProjectReportExpenditures(applicationId, projectReportId).then(expenditureList => {
      expenditureList.forEach((expenditure, i) => {
        projectReportDetails.verificationWork[i].expenditureId = expenditure.expenditure.id;
      });
    })
    cy.updateProjectReportExpenditureVerification(applicationId, projectReportId, projectReportDetails.verificationWork)
    cy.finalizeProjectReportVerification(applicationId, projectReportId);
    cy.get('@currentUser').then((currentUser: any) => {
      loginByRequest(currentUser.name);
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

Cypress.Commands.add('updateProjectReportExpenditureVerification', (applicationId: number, reportId: number, expenditureVerification) => {
    cy.request({
        method: 'PUT',
        url: `api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/verification/expenditure`,
        body: expenditureVerification
    }).then( response => response.body);
});


Cypress.Commands.add('finalizeProjectReportVerification', (applicationId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `/api/project/report/byProjectId/${applicationId}/byReportId/${reportId}/finalizeVerification`,
  });
});

function getProjectReportExpenditures(applicationId: number, projectReportId: number): any {
  return cy.request({
    method: 'GET',
    url: `/api/project/report/byProjectId/${applicationId}/byReportId/${projectReportId}/verification/expenditure`
  }).then(response => {
    return response.body
  });
}


export {};
