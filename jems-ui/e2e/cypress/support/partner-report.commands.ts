declare global {

  namespace Cypress {
    interface Chainable {
      addPartnerReport(partnerId: number);

      updatePartnerReportIdentification(partnerId: number, reportId: number, partnerReportIdentification);

      updatePartnerReportExpenditures(partnerId: number, reportId: number, partnerReportExpenditures);

      runPreSubmissionPartnerReportCheck(partnerId: number, reportId: number);

      submitPartnerReport(partnerId: number, reportId: number);

      startControlWork(partnerId: number, reportId: number);

      setExpenditureItemsAsParked(partnerId: number, reportId: number, partnerReportExpenditures);

      finalizeControl(partnerId: number, reportId: number);

      addPublicProcurement(partnerId: number, reportId: number, procurementDetails)

      getRegularLumpSums(applicationId: number)

      getFastTrackLumpSums(applicationId: number)

      getUnitCostsByPartnerAndReportIds(partnerId: number, reportId: number)

      getLumpSumsByPartnerAndReportIds(partnerId: number, reportId: number)
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
  assignUnitCostIds(partnerId, reportId, partnerReportExpenditures);
  assignLumpSumIds(partnerId, reportId, partnerReportExpenditures);

  cy.request({
    method: 'PUT',
    url: `api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}`,
    body: partnerReportExpenditures
  }).then(response => response.body);
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

Cypress.Commands.add('startControlWork', (partnerId: number, reportId: number) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/partner/startControl/${partnerId}/${reportId}`,
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

Cypress.Commands.add('addPublicProcurement', (partnerId: number, reportId: number, procurementDetails) => {
  cy.request({
    method: 'POST',
    url: `api/project/report/partner/procurement/byPartnerId/${partnerId}/byReportId/${reportId}`,
    body: procurementDetails
  }).then(response => response.body);
});

Cypress.Commands.add('getRegularLumpSums', (applicationId: number) => {
  cy.request({
    method: 'GET',
    url: `api/project/byId/${applicationId}`
  }).then((response) => {
    return response.body.callSettings.lumpSums.filter(lumpSum => !lumpSum.fastTrack);
  })
});

Cypress.Commands.add('getFastTrackLumpSums', (applicationId: number) => {
  cy.request({
    method: 'GET',
    url: `api/project/byId/${applicationId}`
  }).then((response) => {
    return response.body.callSettings.lumpSums.filter(lumpSum => lumpSum.fastTrack);
  })
});

Cypress.Commands.add('getLumpSumsByPartnerAndReportIds', (partnerId: number, reportId: number) => {
  cy.request({
    method: 'GET',
    url: `api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}/lumpSums`
  }).then(response => response.body)
});

Cypress.Commands.add('getUnitCostsByPartnerAndReportIds', (partnerId: number, reportId: number) => {
  cy.request({
    method: 'GET',
    url: `api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}/unitCosts`
  }).then(response => response.body);
});

function assignUnitCostIds(partnerId, reportId, partnerReportExpenditures) {
  partnerReportExpenditures.forEach(expenditure => {
    if (expenditure.cypressReferenceUnit && expenditure.cypressReferenceUnit !== 'shouldHaveLumpSum') {
      cy.get(`@${expenditure.cypressReferenceUnit}`)
        .then(unitCostId => {
          cy.getUnitCostsByPartnerAndReportIds(partnerId, reportId)
            .then(unitCosts => unitCosts.forEach(unitCost => {
              if (unitCost.unitCostProgrammeId === unitCostId) expenditure.unitCostId = unitCost.id;
            }));
        });
    }
  })
}

function assignLumpSumIds(partnerId, reportId, partnerReportExpenditures) {
  partnerReportExpenditures.forEach(expenditure => {
    if (expenditure.cypressReferenceUnit === 'shouldHaveLumpSum') {
      cy.getLumpSumsByPartnerAndReportIds(partnerId, reportId)
        .then(lumpSums => lumpSums.forEach(lumpSum => {
          expenditure.lumpSumId = lumpSum.id
        }))
    }
  })
}

export {}
