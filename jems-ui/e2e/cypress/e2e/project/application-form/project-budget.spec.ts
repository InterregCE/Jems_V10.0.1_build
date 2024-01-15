import user from '../../../fixtures/users.json';
import call from '../../../fixtures/api/call/1.step.call.json';
import partner from '../../../fixtures/api/application/partner/partner.json';

const baselinePath = '/project/application-form/project-budget/';
const comparePdfMask = [
  {pageIndex: 0, coordinates: {x0: 387, x1: 440, y0: 324, y1: 343}},
  {pageIndex: 0, coordinates: {x0: 260, x1: 580, y0: 373, y1: 404}},
  {pageIndex: 0, coordinates: {x0: 250, x1: 570, y0: 515, y1: 535}},
  {pageIndex: 1, coordinates: {x0: 400, x1: 450, y0: 207, y1: 224}},
  {pageIndex: 1, coordinates: {x0: 400, x1: 580, y0: 370, y1: 390}}
];

context('Project budget tests', () => {

  it('TB-534 Amounts cross-checks within AF', () => {
    cy.fixture('project/application-form/project-budget/TB-534.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {

      cy.loginByRequest(user.programmeUser.email);
      call.budgetSettings.flatRates = testData.call.flatRates;
      call.generalCallSettings.additionalFundAllowed = false;
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
      application.associatedOrganisations = null;
      application.lumpSums = [];
      application.description.workPlan[0].activities[0].cypressReferencePartner = application.partners[0].details.abbreviation;
      application.description.workPlan[0].activities[1].cypressReferencePartner = application.partners[1].details.abbreviation;

      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.visit(`app/project/detail/${applicationId}/applicationFormOverviewTables`, {failOnStatusCode: false});

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
            if (![0, 1, 2].includes(budgetIndex))
              expect(partnerBudget.text()).to.be.equal(testData.partners[partnerIndex].budgetOverview[budgetIndex - 3]);
          });
        });

        cy.get('div.footer').find('span').each((totalBudget, budgetIndex) => {
          if (![0, 1, 2].includes(budgetIndex))
            expect(totalBudget.text()).to.be.equal(testData.fundingAmounts.totalBudgetOverview[budgetIndex - 3]);
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
              if (![0,1].includes(index))
                expect(partnerBudget.text()).to.be.equal(partner.budgetOverview[index - 2]);
            });
            
            cy.visit(`app/project/detail/${applicationId}/applicationFormPartner/${partnerId}/coFinancing`, {failOnStatusCode: false});

            cy.contains('div.jems-table-config', 'Source').children().eq(1).find('div').should('contain', partner.cofinancingAmount);
            cy.contains('div.jems-table-config', 'Source').children().eq(3).find('div').eq(1).should('contain', partner.budgetOverview[9]);
          });
        });
      });
      });
    });
  });


  it('TB-383 Rounding values in partner co-financing', () => {
    cy.fixture('project/application-form/project-budget/TB-383.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      call.generalCallSettings.additionalFundAllowed = false;
      cy.createCall(call, user.programmeUser.email).then(callId => {
        testData.application.details.projectCallId = callId;
        cy.publishCall(callId, user.programmeUser.email);

        cy.createApplication(testData.application).then(applicationId => {
          cy.updateProjectIdentification(applicationId, testData.application.identification);
          const projectPartner = testData.application.partners[0];
          cy.createPartner(applicationId, projectPartner.details).then(partnerId => {
            cy.addPartnerTravelCosts(partnerId, projectPartner.budget.travel);

            cy.visit(`app/project/detail/${applicationId}/applicationFormPartner/${partnerId}/coFinancing`, {failOnStatusCode: false});
            cy.get('jems-project-partner-co-financing-tab').should('exist');

            cy.contains('div.jems-table-config', 'Source').then(cofinancingSection => {
              cy.wrap(cofinancingSection).find('mat-select').click();
              cy.contains('mat-option', testData.fundSource).click();
              cy.wrap(cofinancingSection).find('input').type(testData.fundPercentage.toString());

              cy.wrap(cofinancingSection).children().eq(1).children('div').eq(0).should('contain', testData.roundedDownAmount);
              cy.wrap(cofinancingSection).children().eq(2).children('div').eq(1).should('contain', testData.roundedUpAmount);
              cy.wrap(cofinancingSection).children().eq(2).children('div').eq(2).should('contain', testData.percentageDifference);
              cy.wrap(cofinancingSection).children().eq(3).children('div').eq(1).should('contain', testData.partnerTotalEligibleBudget);
            });

            cy.contains('div.jems-table-config', 'Source of contribution').then(partnerContributionSection => {
              cy.wrap(partnerContributionSection).find('mat-select').click();
              cy.contains('mat-option', testData.contributionStatus).click();
              cy.wrap(partnerContributionSection).find('input').type(testData.contributionAmount);
            });

            cy.contains('button', 'Save changes').click();

            // verify A.3 section
            cy.visit(`app/project/detail/${applicationId}/applicationFormOverviewTables`, {failOnStatusCode: false});

            cy.contains('tr', 'Neighbourhood CBC').then(budgetBreakdown => {
              cy.wrap(budgetBreakdown).children().eq(1).should('contain', testData.roundedDownAmount);
              cy.wrap(budgetBreakdown).children().eq(2).should('contain', testData.fundPercentage);
              cy.wrap(budgetBreakdown).children().eq(6).should('contain', testData.roundedUpAmount);
              cy.wrap(budgetBreakdown).children().eq(8).should('contain', testData.partnerTotalEligibleBudget);
            });

            cy.contains('tr', 'Total project budget').then(totalBudgetBreakdown => {
              cy.wrap(totalBudgetBreakdown).children().eq(1).should('contain', testData.roundedDownAmount);
              cy.wrap(totalBudgetBreakdown).children().eq(2).should('contain', testData.fundPercentage);
              cy.wrap(totalBudgetBreakdown).children().eq(6).should('contain', testData.roundedUpAmount);
              cy.wrap(totalBudgetBreakdown).children().eq(8).should('contain', testData.partnerTotalEligibleBudget);
            });

            // verify D.1 section
            cy.visit(`app/project/detail/${applicationId}/applicationFormBudgetPerPartner`, {failOnStatusCode: false});
            cy.contains('100 % of total').should('be.visible');
            cy.get('div.jems-table-config').children().eq(1).then(partnerBreakdown => {
              cy.wrap(partnerBreakdown).children().eq(5).should('contain', testData.roundedDownAmount);
              cy.wrap(partnerBreakdown).children().eq(6).should('contain', testData.fundPercentage);
              cy.wrap(partnerBreakdown).children().eq(11).should('contain', testData.roundedUpAmount);
              cy.wrap(partnerBreakdown).children().eq(13).should('contain', testData.partnerTotalEligibleBudget);
            });

            // verify PDF export
            cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});
            cy.contains('div', 'Export Plugin').find('mat-select').click();
            cy.contains('mat-option', 'Standard application form export').click();
            cy.contains('button', 'Export').clickToDownload('**/export/application?*', 'pdf').then(exportFile => {
              const templateFile = '/project/application-form/project-budget/TB-383-export-template.pdf';
              cy.comparePdf(templateFile, exportFile, comparePdfMask, baselinePath).then(result => {
                expect(result.status === 'passed', 'Verify downloaded pdf file').to.be.true;
              });
            });

            // verify CSV export
            cy.contains('div', 'Export Plugin').find('mat-select').click();
            cy.contains('mat-option', 'Standard budget export').click();
            cy.contains('button', 'Export').clickToDownload('**/export/budget?*', 'xlsx').then(exportFile => {
              cy.fixture('project/application-form/project-budget/TB-383-export.xlsx', null).parseXLSX().then(testDataFile => {
                const assertionMessage = 'Verify downloaded xlsx file';
                expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
                expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
              });
            });
          });
        });
      });
    });
  });
})
