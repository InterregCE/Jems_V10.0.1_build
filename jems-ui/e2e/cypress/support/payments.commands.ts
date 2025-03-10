declare global {
  namespace Cypress {
    interface Chainable {
      addAuthorizedPayments(applicationId, authorizedPayments);
      findProjectPayments(applicationId);
    }
  }
}

Cypress.Commands.add('addAuthorizedPayments', (applicationId, testDataAuthorizedPayments) => {
  // map real ids from backend to test data payments
  testDataAuthorizedPayments.forEach(testDataAuthorizedPayment => {
    findProjectPayments(applicationId).then(projectPayments => {
      const fundPayment = projectPayments.find(payment =>
          payment.fund.type === testDataAuthorizedPayment.fundType && payment.paymentType === testDataAuthorizedPayment.paymentType);
      testDataAuthorizedPayment.payments.forEach(testDataPayment => {
        cy.request(`api/payments/${fundPayment.id}`).then(function (response: any) {
          const partnerPaymentInfo = response.body.partnerPayments.find((partnerPaymentInfo) => partnerPaymentInfo.partnerAbbreviation === testDataPayment.partnerAbbreviation)
            testDataPayment.partnerId = this[partnerPaymentInfo.partnerAbbreviation]
            testDataPayment.id = partnerPaymentInfo.id
        });
      });
      cy.request({
        method: 'PUT',
        url: `api/payments/${fundPayment.id}/partnerInstallments/`,
        body: testDataAuthorizedPayment.payments
      });
    });
  });
});

Cypress.Commands.add('findProjectPayments', applicationId => {
  findProjectPayments(applicationId)
});

function findProjectPayments(applicationId) {
  return cy.request({
    method: 'POST',
    url: 'api/payments?page=0&size=25&sort=id,desc',
    body: {projectIdentifiers: [applicationId]}
  }).then(response => {
    return response.body.content
  });
}

export {}
