import user from "../../../fixtures/users.json";
import call from "../../../fixtures/api/call/1.step.call.json";
import partner from "../../../fixtures/api/application/partner/partner.json";
import approvalInfo from "../../../fixtures/api/application/modification/approval.info.json";
import {loginByRequest} from "../../../support/login.commands";

context('Partner reports tests', () => {
  it('TB-745 Partner user can deactivate multiple partners and changes are displayed only after approval', function () {
    cy.fixture('project/reporting/TB-745.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {
        prepareTestData(testData, application);

        cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
          const partnerIndexesToDisable = [2, 3];

          openModification(applicationId);
          disableSelectedPartners(application, applicationId, partnerIndexesToDisable);
          verifyPartnerChangesBeforeApproving(application, applicationId, partnerIndexesToDisable);
          cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

          cy.loginByRequest(user.admin.email).then(() => {
            cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false})
              .then(() => {
                partnerIndexesToDisable.forEach(id => {
                  cy.get('mat-expansion-panel-header:contains("Partner details")')
                    .next('div')
                    .find(`li:contains("${application.partners[id].details.abbreviation}")`)
                    .contains('mat-icon', 'person_off')
                    .should('exist');
                });

                verifyIconsInProjectPrivileges(application, partnerIndexesToDisable, true);
              });
          });

          openModification(applicationId);
          loginByRequest(user.applicantUser.email).then(() => {
            cy.createFullPartner(applicationId, partner);
            submitProjectApp(applicationId);
          });

          cy.loginByRequest(user.admin.email).then(() => {
            verifyPartnerAvailability(applicationId, false);
          });

          cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

          cy.loginByRequest(user.admin.email).then(() => {
            verifyPartnerAvailability(applicationId, true);
          });

          openModification(applicationId);
          verify30PartnersLimit(applicationId);
        });
      });
    });
  });
});

function prepareTestData(testData, application) {
  cy.loginByRequest(user.programmeUser.email);

  cy.createCall(call)
    .then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });

  cy.loginByRequest(user.applicantUser.email);

  preparePartnersList(testData, application);

  application.lumpSums = [];
  application.description.workPlan[0].activities[0].cypressReferencePartner = application.partners[0].details.abbreviation;
  application.description.workPlan[0].activities[1].cypressReferencePartner = application.partners[1].details.abbreviation;
}

function preparePartnersList(testData, application) {
  application.partners = [];

  for (let i = 0; i < 29; i++) {
    const tempPartner = JSON.parse(JSON.stringify(testData.partner));

    if (i === 0) {
      application.partners.push(tempPartner);
      continue;
    }

    const index = i + 1;

    tempPartner.details.abbreviation = "PP" + index;
    tempPartner.details.role = "PARTNER";
    application.partners.push(tempPartner);
  }
}

function openModification(applicationId) {
  cy.loginByRequest(user.programmeUser.email);

  cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false})
  cy.contains('Open new modification')
    .click();
  cy.get('jems-confirm-dialog')
    .should('be.visible');
  cy.get('jems-confirm-dialog')
    .find('.mat-dialog-actions')
    .contains('Confirm')
    .click();
  cy.contains('You have successfully opened a modification')
    .should('be.visible');
}

function disableSelectedPartners(application, applicationId, partnerIdsToDisable) {
  cy.loginByRequest(user.applicantUser.email);
  cy.visit(`app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false});
  disablePartnersByIds(application, partnerIdsToDisable);
  submitProjectApp(applicationId);
}

function submitProjectApp(applicationId) {
  cy.runPreSubmissionCheck(applicationId);
  cy.submitProjectApplication(applicationId);
}

function disablePartnersByIds(application, partnerIdsToDisable) {
  partnerIdsToDisable.forEach(id => {
    cy.contains('mat-row', application.partners[id].details.abbreviation)
      .contains('button', 'Deactivate partner')
      .click();

    cy.contains('button', 'Confirm')
      .click();

    cy.contains('div', `Partner "${application.partners[id].details.abbreviation}" deactivated successfully`)
      .scrollIntoView()
      .should('be.visible');

    cy.contains('div', `Partner "${application.partners[id].details.abbreviation}" deactivated successfully`)
      .should('not.exist');

    cy.contains('mat-row', application.partners[id].details.abbreviation)
      .contains('button', 'Deactivate partner')
      .scrollIntoView()
      .should('be.disabled');

    cy.contains('mat-row', application.partners[id].details.abbreviation)
      .contains('Inactive')
      .scrollIntoView()
      .should('be.visible');

    cy.contains('mat-row', application.partners[id].details.abbreviation)
      .contains('mat-icon', 'person_off')
      .scrollIntoView()
      .should('be.visible');

    verifyIconsInProjectPartners(application, id, true);
  })
}

function verifyPartnerChangesBeforeApproving(application, applicationId, partnerIdsToDisable) {
  const disabledPartnerAbbreviation = application.partners[partnerIdsToDisable[0]].details.abbreviation;

  cy.loginByRequest(user.admin.email);

  cy.visit(`app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false})
    .then(() => {
      cy.get('mat-sidenav')
        .should('be.visible');

      partnerIdsToDisable.forEach(id => {
        cy.contains('mat-row', application.partners[id].details.abbreviation)
          .contains('Inactive')
          .scrollIntoView()
          .should('be.visible');

        cy.contains('mat-row', application.partners[id].details.abbreviation)
          .contains('mat-icon', 'person_off')
          .scrollIntoView()
          .should('be.visible');

        verifyIconsInPartnerDetails(application, id, false);
        verifyIconsInProjectPartners(application, id, true);
      });

      verifyIconsInProjectPrivileges(application, partnerIdsToDisable, false)
      verifyDeactivatedPartnerBannerDisplay("Partner details", disabledPartnerAbbreviation, false);
      verifyDeactivatedPartnerBannerDisplay("Project partners", disabledPartnerAbbreviation, true);
    });
}

function verifyIconsInProjectPrivileges(application, partnerIdsToDisable, shouldIconsBeDisplayed) {
  const displayFlag = shouldIconsBeDisplayed ? 'be.visible' : 'not.exist';

  cy.contains('Project privileges')
    .click()
    .then(() => {
      partnerIdsToDisable.forEach(id => {
        cy.get(`mat-expansion-panel-header:contains("${application.partners[id].details.abbreviation}")`)
          .scrollIntoView()
          .contains('mat-icon', 'person_off')
          .should(displayFlag)
      });
    });
}

function verifyIconsInPartnerDetails(application, id, shouldIconsBeDisplayed) {
  const displayFlag = shouldIconsBeDisplayed ? 'be.visible' : 'not.exist';

  cy.get('mat-expansion-panel-header:contains("Partner details")')
    .next('div')
    .find(`li:contains("${application.partners[id].details.abbreviation}")`)
    .then((foundElement) => {
      if (shouldIconsBeDisplayed) {
        cy.wrap(foundElement)
          .contains('mat-icon', 'person_off')
          .scrollIntoView()
          .should(displayFlag)
      } else {
        cy.wrap(foundElement)
          .contains('mat-icon', 'person_off')
          .should(displayFlag)
      }
    })
}

function verifyIconsInProjectPartners(application, id, shouldIconsBeDisplayed) {
  const displayFlag = shouldIconsBeDisplayed ? 'be.visible' : 'not.exist';

  cy.get('mat-expansion-panel-header:contains("Project partners")')
    .next('div')
    .find(`li:contains("${application.partners[id].details.abbreviation}")`)
    .contains('mat-icon', 'person_off')
    .then((foundElement) => {
      if (shouldIconsBeDisplayed) {
        cy.wrap(foundElement)
          .scrollIntoView()
          .should(displayFlag);
      } else {
        cy.wrap(foundElement)
          .should(displayFlag);
      }
    })
}

function verifyDeactivatedPartnerBannerDisplay(headerTitle, disabledPartnerAbbreviation, shouldBannerBeDisplayed) {
  const displayFlag = shouldBannerBeDisplayed ? 'be.visible' : 'not.exist';

  cy.get(`mat-expansion-panel-header:contains(${headerTitle})`)
    .next('div')
    .find(`span:contains("${disabledPartnerAbbreviation}")`)
    .click()
    .then(() => {
      cy.contains('div', "You are currently viewing a deactivated partner.")
        .should(displayFlag);
    });
}

function verifyPartnerInPartnerDetails(shouldPartnerBeDisplayed) {
  const displayFlag = shouldPartnerBeDisplayed ? 'be.visible' : 'not.exist';

  cy.get('mat-expansion-panel-header:contains("Partner details")')
    .next('div')
    .then((foundElement) => {
      if (shouldPartnerBeDisplayed) {
        cy.wrap(foundElement)
          .find(`li:contains("PP30")`)
          .scrollIntoView()
          .should(displayFlag)
      } else {
        cy.wrap(foundElement)
          .find(`li:contains("PP30")`)
          .should(displayFlag)
      }
    });
}

function verifyPartnerAvailabilityInProjectPrivileges(shouldPartnerBeDisplayed) {
  const displayFlag = shouldPartnerBeDisplayed ? 'be.visible' : 'not.exist';

  cy.contains('Project privileges')
    .click()
    .then((foundElement) => {
      if (shouldPartnerBeDisplayed) {
        cy.wrap(foundElement)
          .get(`mat-expansion-panel-header:contains("PP30")`)
          .scrollIntoView()
          .should(displayFlag)
      } else {
        cy.wrap(foundElement)
          .get(`mat-expansion-panel-header:contains("PP30")`)
          .should(displayFlag)
      }
    });
}

function verifyPartnerAvailability(applicationId, shouldPartnerBeDisplayed) {
  cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

  verifyPartnerInPartnerDetails(shouldPartnerBeDisplayed);
  verifyPartnerAvailabilityInProjectPrivileges(shouldPartnerBeDisplayed);
}

function verify30PartnersLimit(applicationId) {
  loginByRequest(user.applicantUser.email).then(() => {
    cy.visit(`app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false});
    cy.contains('Add new partner')
      .click()
    cy.contains('button', 'Partner')
      .click();
    cy.get(`[name='abbreviation']`)
      .type('PP31');
    cy.get(`[name='legalStatusId']`)
      .click()
    cy.contains('Public')
      .click();
    cy.contains('button', 'Create')
      .click();

    cy.contains('Failed to create the project partner (error code: S-CPP)')
      .should('be.visible');
    cy.contains('It is not possible to add more than "30" partner to the project application (error code: S-CPP-005)')
      .should('be.visible');
  });
}
