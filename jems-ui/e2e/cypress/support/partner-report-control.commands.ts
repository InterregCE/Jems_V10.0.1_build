declare global {

  namespace Cypress {
    interface Chainable {
      startControlWork(partnerId: number, reportId: number);

      updateControlReportIdentification(partnerId: number, reportId: number, controlReportIdentification);

      setExpenditureItemsAsParked(partnerId: number, reportId: number, partnerReportExpenditures);

      finalizeControl(partnerId: number, reportId: number);

      startControlChecklist(partnerId: number, reportId: number, checklist);

      finishControlChecklist(partnerId, reportId, checklistId);

      updateControlReportExpenditureVerification(partnerId: number, reportId: number, partnerReportExpenditures);
    }
  }
}

Cypress.Commands.add('startControlWork', (partnerId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/partner/startControl/${partnerId}/${reportId}`,
  })
});

Cypress.Commands.add('updateControlReportIdentification', (partnerId: number, reportId: number, controlReportIdentification) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/partner/control/identification/byPartnerId/${partnerId}/byReportId/${reportId}`,
    body: controlReportIdentification
  })
});

Cypress.Commands.add('setExpenditureItemsAsParked', (partnerId: number, reportId: number, parkedExpenditures) => {
  cy.request({
    method: 'PUT',
    url: `api/project/report/partner/control/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}`,
    body: parkedExpenditures
  })
});

Cypress.Commands.add('finalizeControl', (partnerId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/partner/finalizeControl/${partnerId}/${reportId}`,
  })
});

Cypress.Commands.add('startControlChecklist', (partnerId, reportId, checklist) => {
  startControlChecklist(partnerId, reportId, checklist);
});

Cypress.Commands.add('finishControlChecklist', (partnerId, reportId, checklistId) => {
    finishControlChecklist(partnerId, reportId, checklistId)
});


Cypress.Commands.add('updateControlReportExpenditureVerification', (partnerId: number, reportId: number, partnerReportExpenditures) => {
    cy.request({
        method: 'PUT',
        url: `api/project/report/partner/control/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}`,
        body: partnerReportExpenditures
    }).then(response => response.body);
});

function startControlChecklist(partnerId, reportId, checklist) {
  return cy.request({
    method: 'POST',
    url: `api/controlChecklist/byPartnerId/${partnerId}/byReportId/${reportId}`,
    body: checklist
  }).then(response => {
    return response.body.id;
  });
}

function finishControlChecklist(partnerId, reportId, checklistId) {
    cy.request({
      method: 'PUT',
      url: `api/controlChecklist/byPartnerId/${partnerId}/byReportId/${reportId}/status/${checklistId}/FINISHED`
    });
}

export {}
