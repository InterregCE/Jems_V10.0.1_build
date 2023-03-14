import user from "../../../fixtures/users.json";
import call from "../../../fixtures/api/call/1.step.call.json";
import partner from "../../../fixtures/api/application/partner/partner.json";

context('Partner reports tests', () => {

  it.only('TB-745 Partner page', function () {
    cy.fixture('project/reporting/TB-745.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {
        prepareTestData(testData, application);

        cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
          const partnerIdsToDisable = [2, 3];

          openApplicationModification(applicationId);
          disableSomePartners(testData, applicationId, partnerIdsToDisable);

          cy.loginByRequest(user.admin.email);
          cy.visit(`https://amsterdam.interact-eu.net/app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false}).then(() => {
            partnerIdsToDisable.forEach(id => {
              cy.contains('mat-row', testData.partners[id].abbreviation)
                .contains('Inactive')
                .should('be.visible');

              cy.contains('mat-row', testData.partners[id].abbreviation)
                .contains('mat-icon', 'person_off')
                .should('be.visible');

              cy.contains('mat-expansion-panel-header', 'Partner details')
                .next('div')
                .find(`li:contains("${testData.partners[id].abbreviation}")`)
                .contains('mat-icon', 'person_off')
                .should('not.exist');

              cy.contains('mat-expansion-panel-header', 'Project partners')
                .next('div')
                .find(`li:contains("${testData.partners[id].abbreviation}")`)
                .contains('mat-icon', 'person_off')
                .scrollIntoView()
                .should('be.visible');
            })
          });

          // cy.approveModification(applicationId, testData.approvalInfo, user.programmeUser.email)
          // verify that they're deactivated only within the Application Form > NOT THE PARTNER DETAILS SECTION
        });



      });
    });

    // VERIFICAITON PHASE

    // re-submit application form
    // confirm changes
    // verify that PPs are deactivated within reporting

    // TODO: Clarify that deactivation of PP means there's an additional slot for another PP or not
  });
});

function prepareTestData(testData, application) {
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
}

function openApplicationModification(applicationId) {
  cy.loginByRequest(user.programmeUser.email);
  cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false}).then(() => {
    cy.contains('Open new modification').click();
    cy.get('jems-confirm-dialog').should('be.visible');
    cy.get('jems-confirm-dialog').find('.mat-dialog-actions').contains('Confirm').click();
    cy.contains('You have successfully opened a modification').should('be.visible');
  });
}

function disableSomePartners(testData, applicationId, partnerIdsToDisable) {
  cy.loginByRequest(user.applicantUser.email);
  cy.visit(`https://amsterdam.interact-eu.net/app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false}).then(() => {
    //TODO: add more disabled partners after returning to the previous JSON file
    disablePartnersByIds(testData, partnerIdsToDisable);

    cy.runPreSubmissionCheck(applicationId);
    cy.submitProjectApplication(applicationId);
  });
}

function disablePartnersByIds(testData, partnerIdsToDisable) {
  partnerIdsToDisable.forEach(id => {
    cy.contains('mat-row', testData.partners[id].abbreviation)
      .contains('button', 'Deactivate partner')
      .click();

    cy.contains('button', 'Confirm')
      .click();

    cy.contains('div', `Partner "${testData.partners[id].abbreviation}" deactivated successfully`)
      .should('be.visible');

    cy.contains('div', `Partner "${testData.partners[id].abbreviation}" deactivated successfully`)
      .should('not.exist');

    cy.contains('mat-row', testData.partners[id].abbreviation).contains('button', 'Deactivate partner')
      .should('be.disabled');

    cy.contains('mat-row', testData.partners[id].abbreviation).contains('Inactive')
      .should('be.visible');

    cy.contains('mat-row', testData.partners[id].abbreviation).contains('mat-icon', 'person_off')
      .should('be.visible');

    cy.contains('mat-expansion-panel-header', 'Project partners')
      .next('div')
      .find(`li:contains("${testData.partners[id].abbreviation}")`)
      .contains('mat-icon', 'person_off')
      .should('be.visible');
  })
}
