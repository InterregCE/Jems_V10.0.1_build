import {faker} from '@faker-js/faker';
import user from '../../fixtures/users.json';
import application from '../../fixtures/api/application/application.json';
import partner from '../../fixtures/api/application/partner/partner.json';
import call from "../../fixtures/api/call/1.step.call.json";

context('Partner reports tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    call.preSubmissionCheckSettings.reportPartnerCheckPluginKey = 'report-partner-check-off';
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-554 Partner report can be created in the correct status and data in the first tab is taken correctly', function () {
    cy.fixture('project/reporting/TB-554.json').then(testData => {

      cy.loginByRequest(user.applicantUser.email);
      testData.partnerUser.email = faker.internet.email();
      testData.users[0].userEmail = testData.partnerUser.email;
      cy.createUser(testData.partnerUser, user.admin.email);
      const firstPartner = application.partners[0].details.abbreviation;
      const secondPartner = application.partners[1].details.abbreviation
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.setProjectToContracted(applicationId, user.programmeUser.email);
        const partnerId = this[secondPartner];
        cy.assignPartnerCollaborators(applicationId, partnerId, testData.users);
        cy.loginByRequest(testData.partnerUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

        cy.contains('div', 'Partner reports').within(() => {
          cy.contains(firstPartner).should('not.exist');
          cy.contains(secondPartner).click();
        });

        cy.contains('Add Partner Report').click();
        verifyReport(testData.firstReportInfo);

        cy.startModification(applicationId, user.programmeUser.email);
        cy.loginByRequest(user.applicantUser.email);
        cy.updatePartnerIdentity(partnerId, testData.updatedPartnerDetails);
        cy.updatePartnerAddress(partnerId, testData.updatedPartnerAddress);
        cy.updatePartnerCofinancing(partnerId, testData.updatedPartnerCofinancing);
        cy.submitProjectApplication(applicationId);
        cy.approveModification(applicationId, testData.approvalInfo, user.programmeUser.email);

        cy.loginByRequest(testData.partnerUser.email);
        cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports`, {failOnStatusCode: false});
        cy.contains(testData.firstReportInfo.partnerReportId).should('be.visible');
        cy.contains(testData.secondReportInfo.partnerReportId).should('not.exist');
        cy.contains(testData.firstReportInfo.partnerReportId).click();
        verifyReport(testData.firstReportInfo);

        cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports`, {failOnStatusCode: false});
        cy.contains('Add Partner Report').click();
        verifyReport(testData.secondReportInfo);
      });
    });
  });

  it('TB-840 Partner Reporting cost category overview table', function () {
    cy.fixture('project/reporting/TB-840.json').then(testData => {

      cy.loginByRequest(user.admin.email);
      testData.partnerUser1.email = faker.internet.email();
      testData.partnerUser2.email = faker.internet.email();
      testData.partnerUser3.email = faker.internet.email();
      cy.createUser(testData.partnerUser1, user.admin.email);
      cy.createUser(testData.partnerUser2, user.admin.email);
      cy.createUser(testData.partnerUser3, user.admin.email);

      partner.details.abbreviation = 'Another partner';
      application.partners.push(partner);
      const firstPartner = application.partners[0].details.abbreviation;
      const secondPartner = application.partners[1].details.abbreviation;
      const thirdPartner = application.partners[2].details.abbreviation;

      cy.loginByRequest(user.applicantUser.email);
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        const firstPartnerId = this[firstPartner];
        const secondPartnerId = this[secondPartner];
        const thirdPartnerId = this[thirdPartner];
        testData.firstUser[0].userEmail = testData.partnerUser1.email;
        cy.assignPartnerCollaborators(applicationId, firstPartnerId, testData.firstUser);
        testData.secondUser[0].userEmail = testData.partnerUser2.email;
        cy.assignPartnerCollaborators(applicationId, secondPartnerId, testData.secondUser);
        testData.thirdUser[0].userEmail = testData.partnerUser3.email;
        cy.assignPartnerCollaborators(applicationId, thirdPartnerId, testData.thirdUser);
        cy.setProjectToContracted(applicationId, user.programmeUser.email);

        cy.loginByRequest(testData.partnerUser1.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.contains('div', 'Partner reports').within(() => {
          cy.contains(firstPartner).click();
        });
        cy.contains('Add Partner Report').click();
        cy.contains('Financial overview').click({force: true});
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Lump sum').should('be.visible');
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Other costs').should('be.visible');
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Unit Costs').should('be.visible');

        cy.loginByRequest(testData.partnerUser2.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.contains('div', 'Partner reports').within(() => {
          cy.contains(secondPartner).click();
        });
        cy.contains('Add Partner Report').click();
        cy.contains('Financial overview').click({force: true});
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Lump sum').should('be.visible');
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Other costs').should('be.visible');
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Unit Costs').should('be.visible');

        cy.loginByRequest(testData.partnerUser3.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.contains('div', 'Partner reports').within(() => {
          cy.contains(thirdPartner).click();
        });
        cy.contains('Add Partner Report').click();
        cy.contains('Financial overview').click({force: true});
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Lump sum').should('be.visible');
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Other costs').should('be.visible');
        cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Unit Costs').should('be.visible');

        const declaredAmount = 20;
        const declaredAmountFormatted = new Intl.NumberFormat('de-DE').format(declaredAmount);
        createCostAsExpenditure(declaredAmountFormatted);

        cy.contains('Financial overview').click({force: true});
        const declaredAmountWithFlatRateValue = (declaredAmount * partner.budget.options.otherCostsOnStaffCostsFlatRate) / 100;
        const declaredAmountWithFlatRateFormatted = new Intl.NumberFormat('de-DE').format(declaredAmountWithFlatRateValue);
        cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parents().should('contain', declaredAmountFormatted);
        cy.get('jems-partner-breakdown-cost-category').contains('Other costs').parents().should('contain', declaredAmountWithFlatRateFormatted);

        cy.loginByRequest(testData.partnerUser2.email);
        cy.visit(`app/project/detail/${applicationId}/reporting/${secondPartnerId}/reports`, {failOnStatusCode: false});
        cy.contains('R.1').click();
        createLumpSumAsExpenditure();

        cy.contains('Financial overview').click({force: true});
        const lumpSumFormatted = new Intl.NumberFormat('de-DE').format(application.lumpSums[1].lumpSumContributions[1].amount);
        cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parents().should('contain', lumpSumFormatted);

        cy.loginByRequest(testData.partnerUser1.email);
        cy.visit(`app/project/detail/${applicationId}/reporting/${firstPartnerId}/reports`, {failOnStatusCode: false});
        cy.contains('R.1').click();
        createUnitCostsAsExpenditures();

        cy.contains('Financial overview').click({force: true});
        const unitCostExternalFormatted = new Intl.NumberFormat('de-DE').format(application.partners[0].budget.external[1].pricePerUnit);
        const unitCostsFormatted = new Intl.NumberFormat('de-DE').format(application.partners[0].budget.unit[0].rowSum / 2);
        cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parents().should('contain', unitCostExternalFormatted);
        cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parents().should('contain', unitCostsFormatted);

        submitPartnerReport();
        cy.visit(`app/project/detail/${applicationId}/reporting/${firstPartnerId}/reports`, {failOnStatusCode: false});
        cy.contains('Add Partner Report').click();
        createUnitCostsAsExpenditures();

        cy.contains('Financial overview').click({force: true});
        cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').siblings().eq(2).should('contain', unitCostExternalFormatted);
        cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').siblings().eq(3).should('contain', unitCostExternalFormatted);

        cy.loginByRequest(testData.partnerUser2.email);
        cy.visit(`app/project/detail/${applicationId}/reporting/${secondPartnerId}/reports`, {failOnStatusCode: false});
        cy.contains('R.1').click();
        submitPartnerReport();
        cy.visit(`app/project/detail/${applicationId}/reporting/${secondPartnerId}/reports`, {failOnStatusCode: false});
        cy.contains('Add Partner Report').click();
        createLumpSumAsExpenditure();

        cy.contains('Financial overview').click({force: true});
        cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').siblings().eq(2).should('contain', lumpSumFormatted);
        cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').siblings().eq(3).should('contain', lumpSumFormatted);

        cy.loginByRequest(testData.partnerUser3.email);
        cy.visit(`app/project/detail/${applicationId}/reporting/${thirdPartnerId}/reports`, {failOnStatusCode: false});
        cy.contains('R.1').click();
        submitPartnerReport();
        cy.visit(`app/project/detail/${applicationId}/reporting/${thirdPartnerId}/reports`, {failOnStatusCode: false});
        cy.contains('Add Partner Report').click();
        createCostAsExpenditure(declaredAmountFormatted);

        cy.contains('Financial overview').click({force: true});
        cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').siblings().eq(2).should('contain', declaredAmountFormatted);
        cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').siblings().eq(3).should('contain', declaredAmountFormatted);
        cy.get('jems-partner-breakdown-cost-category').contains('Other costs').siblings().eq(2).should('contain', declaredAmountWithFlatRateFormatted);
        cy.get('jems-partner-breakdown-cost-category').contains('Other costs').siblings().eq(3).should('contain', declaredAmountWithFlatRateFormatted);
      });
    });
  });

  function verifyReport(reportInfo) {
    cy.contains('Partner report ID').next().should('contain.text', reportInfo.partnerReportId);
    cy.contains('Partner report status').next().should('contain.text', reportInfo.partnerReportStatus);
    cy.contains('Partner number').next().should('contain.text', reportInfo.partnerNumber);
    cy.contains('Name of the organisation in original language').next().should('contain.text', reportInfo.nameInOriginalLanguage);
    cy.contains('Name of the organisation in english').next().should('contain.text', reportInfo.nameInEnglish);
    cy.contains('Legal status').next().should('contain.text', reportInfo.legalStatus);
    cy.contains('Type of partner').next().should('contain.text', reportInfo.typeOfPartner);
    cy.contains('Partner organisation can recover VAT for project activities').next().should('contain.text', reportInfo.vatRecovery);
    cy.contains('Co-financing source and rate').next()
      .should('contain.text', reportInfo.coFinancingSourceAndRate1)
      .should('contain.text', reportInfo.coFinancingSourceAndRate2);
    cy.contains('Country').next().should('contain.text', reportInfo.country);
    cy.contains('Local currency (according to InforEuro)').next().should('contain.text', reportInfo.localCurrency);
  }

  function submitPartnerReport() {
    cy.contains('Submit').click({force: true});
    cy.contains('Run pre-submission check').click();
    cy.contains('Submit partner report').click();
    cy.contains('Confirm').should('be.visible').click();
  }

  function createCostAsExpenditure(declaredAmountFormatted: string) {
    cy.contains('List of expenditures').click({force: true});
    cy.contains('add expenditure').click();
    cy.contains('Please select a cost category').click();
    cy.contains('mat-option', 'Staff costs').click();
    cy.get('mat-row').last().find('.mat-column-declaredAmount').scrollIntoView().click();
    cy.get('mat-row').last().find('.mat-column-declaredAmount').find('input').scrollIntoView().type(declaredAmountFormatted);
    cy.contains('Save changes').click();
  }

  function createLumpSumAsExpenditure() {
    cy.contains('List of expenditures').click({force: true});
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Implementation Lump sum DE - Period 1').click();
    cy.contains('Save changes').click();
  }

  function createUnitCostsAsExpenditures() {
    cy.contains('List of expenditures').click({force: true});
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Unit cost multi - all DE').click();
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Unit cost single - External DE').click();
    cy.contains('Save changes').click();
  }

});
