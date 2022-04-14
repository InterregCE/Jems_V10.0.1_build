import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import partner from '../../../../fixtures/api/application/partner/partner.json';

context('Project budget tests', () => {

  beforeEach(() => {
    cy.viewport(1920, 1080);
  });

  it('TB-534 Amounts cross-checks within AF', () => {
    cy.fixture('project/application-form/project-budget/TB-534.json').then(testData => {

      cy.loginByRequest(user.programmeUser.email);
      call.budgetSettings.flatRates = testData.call.flatRates;
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      });

      cy.loginByRequest(user.applicantUser.email);

      application.partners = [];
      testData.partners.forEach(partnerData => {
        const tempPartner = JSON.parse(JSON.stringify(partner));
        tempPartner.details.abbreviation = partnerData.abbreviation;
        tempPartner.details.role = partnerData.role;
        tempPartner.budget = partnerData.budget;
        tempPartner.cofinancing = partnerData.cofinancing;
        application.partners.push(tempPartner);
      });

      cy.createFullApplication(application, user.programmeUser.email).then(applicationId => {
        cy.visit(`app/project/detail/${applicationId}/applicationFormOverviewTables`, {failOnStatusCode: false});
        cy.loginByRequest(user.applicantUser.email);

        // A.3
        cy.contains('tr', 'ERDF').should('contain', testData.fundingAmounts.totalERDF);
        cy.contains('tr', 'Neighbourhood CBC').should('contain', testData.fundingAmounts.totalCBC);
        cy.contains('tr', 'Total project budget').should('contain', testData.fundingAmounts.total);
        cy.contains('tr', 'Total project budget').should('contain', testData.fundingAmounts.automaticPublicContribution);
        cy.contains('tr', 'Total project budget').should('contain', testData.fundingAmounts.totalPublicContribution);
        cy.contains('tr', 'Total project budget').should('contain', testData.fundingAmounts.privateContribution);

        // D.1
        cy.visit(`app/project/detail/${applicationId}/applicationFormBudgetPerPartner`, {failOnStatusCode: false});
        cy.intercept(`api/project/${applicationId}/workPackage`).as('pageLoaded');
        cy.wait('@pageLoaded');

        cy.get('div.footer').should('contain', testData.fundingAmounts.totalERDF);
        cy.get('div.footer').should('contain', testData.fundingAmounts.totalCBC);
        cy.get('div.footer').should('contain', testData.fundingAmounts.total);
        cy.get('div.footer').should('contain', testData.fundingAmounts.automaticPublicContribution);
        cy.get('div.footer').should('contain', testData.fundingAmounts.totalPublicContribution);
        cy.get('div.footer').should('contain', testData.fundingAmounts.privateContribution);

        // D.2
        cy.visit(`app/project/detail/${applicationId}/applicationFormBudget`, {failOnStatusCode: false});

        testData.partners.forEach((partner, partnerIndex) => {
          cy.contains('div', partner.abbreviation).find('span').each((partnerBudget, budgetIndex) => {
            if (budgetIndex !== 0 && budgetIndex !== 1)
              expect(partnerBudget.text()).to.be.equal(testData.partners[partnerIndex].budgetOverview[budgetIndex - 2]);
          });
        });

        cy.get('div.footer').find('span').each((totalBudget, budgetIndex) => {
          if (budgetIndex !== 0 && budgetIndex !== 1)
            expect(totalBudget.text()).to.be.equal(testData.fundingAmounts.totalBudgetOverview[budgetIndex - 2]);
        });

        // D.3
        cy.visit(`app/project/detail/${applicationId}/applicationFormBudgetPerPeriod`, {failOnStatusCode: false});

        cy.contains('div', 'ERDF').should('contain', testData.fundingAmounts.totalERDF);
        cy.contains('div', 'Neighbourhood CBC').should('contain', testData.fundingAmounts.totalCBC);
        cy.get('div.footer').should('contain', testData.fundingAmounts.total);

        // Partner budget and co-financing
        testData.partners.forEach(partner => {
          cy.get(`@${partner.abbreviation}`).then(partnerId => {
            cy.visit(`app/project/detail/${applicationId}/applicationFormPartner/${partnerId}/budget`, {failOnStatusCode: false});

            cy.get('jems-budget-table div').eq(2).find('span').each((partnerBudget, index) => {
              if (index !== 0)
                expect(partnerBudget.text()).to.be.equal(partner.budgetOverview[index - 1]);
            });

            cy.contains('a', 'Co-financing').click();

            cy.contains('div.jems-table-config', 'Source').children().eq(1).find('div').should('contain', partner.cofinancingAmount);
            cy.contains('div.jems-table-config', 'Source').children().eq(3).find('div').eq(1).should('contain', partner.budgetOverview[9]);
          });
        });
      });
    });
  });
})
