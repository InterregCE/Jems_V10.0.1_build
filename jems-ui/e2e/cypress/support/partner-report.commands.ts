declare global {

  namespace Cypress {
    interface Chainable {
      addPartnerReport(partnerId: number);

      updatePartnerReportIdentification(partnerId: number, reportId: number, partnerReportIdentification);

      updatePartnerReportExpenditures(partnerId: number, reportId: number, partnerReportExpenditures);

      runPreSubmissionPartnerReportCheck(partnerId: number, reportId: number);

      submitPartnerReport(partnerId: number, reportId: number);
    }
  }
}

Cypress.Commands.add('addPartnerReport', (partnerId: number) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/partner/create/${partnerId}`,
  }).then(response => {
    return response.body.id
  });
});

Cypress.Commands.add('updatePartnerReportIdentification', (partnerId: number, reportId: number, partnerReportIdentification) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/partner/identification/byPartnerId/${partnerId}/byReportId/${reportId}`,
    body: partnerReportIdentification
  });
});

Cypress.Commands.add('updatePartnerReportExpenditures', (partnerId: number, reportId: number, partnerReportExpenditures) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}`,
    body: partnerReportExpenditures
  });
});

Cypress.Commands.add('runPreSubmissionPartnerReportCheck', (partnerId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/partner/preCheck/${partnerId}/${reportId}`,
  });
});

Cypress.Commands.add('submitPartnerReport', (partnerId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/partner/submit/${partnerId}/${reportId}`,
  });
});

export {}
