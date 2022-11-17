declare global {

  namespace Cypress {
    interface Chainable {
      addPartnerReport(partnerId: number);
      
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

Cypress.Commands.add('submitPartnerReport', (partnerId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/partner/submit/${partnerId}/${reportId}`,
  });
});

export {}
