declare global {
  namespace Cypress {
    interface Chainable {
      setContractingFastTrackLumpSums(applicationId, contractingFastTrackLumpSums);
    }
  }
}

Cypress.Commands.add('setContractingFastTrackLumpSums', (applicationId, contractingFastTrackLumpSums) => {
  cy.request(`api/project/${applicationId}/contracting/monitoring`).then((response: any) => {
    contractingFastTrackLumpSums.forEach(finalLumpSum => {
      const sourceLumpSum = response.body.fastTrackLumpSums.find((sourceLumpSum) => sourceLumpSum.programmeLumpSumId === finalLumpSum.programmeLumpSumId)
      sourceLumpSum.comment = finalLumpSum.comment
      sourceLumpSum.readyForPayment = finalLumpSum.readyForPayment
    })
    cy.request({
      method: 'PUT',
      url: `api/project/${applicationId}/contracting/monitoring`,
      body: response.body
    });
  });
});

export {}
