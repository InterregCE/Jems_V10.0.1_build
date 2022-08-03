import {faker} from '@faker-js/faker';
import user from '../../../fixtures/users.json';
import application from '../../../fixtures/api/application/application.json';
import partner from '../../../fixtures/api/application/partner/partner.json';
import call from "../../../fixtures/api/call/1.step.call.json";

context('Partner reports tests', () => {

  beforeEach(() => {
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-554 A partner report can be created in the correct status and data in the first tab is taken correctly', () => {
    cy.fixture('project/reporting/TB-554.json').then(testData => {
      testData.partnerUser.email = faker.internet.email();
      testData.users[0].userEmail = testData.partnerUser.email;
      cy.createUser(testData.partnerUser, user.admin.email);
      const firstPartner = application.partners[0].details.abbreviation;
      const secondPartner = partner.details.abbreviation;
      cy.createCall(call, user.programmeUser.email).then(callId => {
        application.details.projectCallId = callId;
        application.partners.push(partner as any);
        cy.publishCall(callId, user.programmeUser.email);
      });
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.setProjectToContracted(applicationId, user.programmeUser.email);
        cy.get(`@${partner.details.abbreviation}`).then((partnerId: any) => {
          cy.assignPartnerCollaborators(applicationId, partnerId, testData.users);
        });
        cy.loginByRequest(testData.partnerUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

        cy.contains('div', 'Partner reports').within(() => {
          cy.contains(firstPartner).should('not.exist');
          cy.contains(secondPartner).click();
        });
        
        cy.contains('Add Partner Report').click();
        verifyReport(testData.firstReportInfo);
        
        cy.get(`@${partner.details.abbreviation}`).then((partnerId: any) => {
          cy.startModification(applicationId, user.programmeUser.email);
          cy.loginByRequest(user.applicantUser.email);
          cy.updatePartner(partnerId, testData.updatedPartnerDetails);
          cy.updatePartnerAddress(partnerId, testData.updatedPartnerAddress);
          cy.updatePartnerCofinancing(partnerId, testData.updatedPartnerCofinancing);
          cy.submitProjectApplication(applicationId);
          cy.approveModification(applicationId, testData.approvalInfo, user.programmeUser.email);
        });

        cy.loginByRequest(testData.partnerUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.wait(1000); // TODO remove after MP2-2391 is fixed
        cy.contains(testData.updatedPartnerDetails.abbreviation).click();
        cy.contains(testData.secondReportInfo.partnerReportId).should('not.exist');
        cy.contains(testData.firstReportInfo.partnerReportId).click();
        verifyReport(testData.firstReportInfo);

        cy.contains(testData.updatedPartnerDetails.abbreviation).click();
        cy.contains('Add Partner Report').click();
        verifyReport(testData.secondReportInfo);
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
    cy.contains('Local currency (accoridng to InforEuro)').next().should('contain.text', reportInfo.localCurrency);
  }
});
