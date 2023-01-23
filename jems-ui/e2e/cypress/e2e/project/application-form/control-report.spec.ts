import user from '../../../fixtures/users.json';
import call from '../../../fixtures/api/call/1.step.call.json';
import application from '../../../fixtures/api/application/application.json';
import approvalInfo from '../../../fixtures/api/application/modification/approval.info.json';
import partnerReportIdentification from '../../../fixtures/api/partnerReport/partnerReportIdentification.json';
import partnerReportExpenditures from '../../../fixtures/api/partnerReport/partnerReportExpenditures.json';
import {faker} from "@faker-js/faker";

context('Control report tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-767 Control report creation', () => {
    cy.fixture('project/application-form/control-reports/TB-767.json').then(testData => {

      // create application
      application.partners[0].details.nameInOriginalLanguage = 'Original name to be changed';
      application.partners[0].details.nameInEnglish = 'Name in English to be changed';
      application.partners[1].details.nameInOriginalLanguage = 'Original name to be changed';
      application.partners[1].details.nameInEnglish = 'Name in English to be changed';
      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {

        cy.then(function () {
          const partnerId1 = this[application.partners[0].details.abbreviation];
          const partnerId2 = this[application.partners[1].details.abbreviation];

          // create controller role/user + assignment
          cy.loginByRequest(user.admin.email);
          testData.controllerRole.name = `controllerRole_${faker.random.alphaNumeric(5)}`;
          testData.controllerUserEdit.email = faker.internet.email();
          testData.controllerUserView.email = faker.internet.email();
          cy.createRole(testData.controllerRole).then(roleId => {
            testData.controllerUserEdit.userRoleId = roleId;
            testData.controllerUserView.userRoleId = roleId;
            cy.createUser(testData.controllerUserEdit);
            cy.createUser(testData.controllerUserView);
            testData.controllerInstitution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
            testData.controllerInstitution.institutionUsers[0].userEmail = testData.controllerUserEdit.email;
            testData.controllerInstitution.institutionUsers[1].userEmail = testData.controllerUserView.email;
            cy.createInstitution(testData.controllerInstitution).then(institutionId => {
              testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId1;
              testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
              cy.assignInstitution(testData.controllerAssignment);
            });
          });

          // create partner reports
          cy.loginByRequest(user.applicantUser.email);
          cy.assignPartnerCollaborators(applicationId, partnerId1, testData.partnerCollaborator);
          cy.assignPartnerCollaborators(applicationId, partnerId2, testData.partnerCollaborator);
          cy.addPartnerReport(partnerId1);
          cy.addPartnerReport(partnerId1).then(reportId => {
            cy.wrap(reportId).as('reportId');
            cy.updatePartnerReportIdentification(partnerId1, reportId, partnerReportIdentification);
            cy.updatePartnerReportExpenditures(partnerId1, reportId, partnerReportExpenditures);
            cy.runPreSubmissionPartnerReportCheck(partnerId1, reportId);
            cy.submitPartnerReport(partnerId1, reportId);
          });
          cy.addPartnerReport(partnerId2);
          cy.addPartnerReport(partnerId2).then(reportId => {
            cy.updatePartnerReportIdentification(partnerId2, reportId, partnerReportIdentification);
            cy.updatePartnerReportExpenditures(partnerId2, reportId, partnerReportExpenditures);
            cy.runPreSubmissionPartnerReportCheck(partnerId2, reportId);
            cy.submitPartnerReport(partnerId2, reportId);
          });

          cy.loginByRequest(testData.controllerUserView.email);
          cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
          cy.contains('mat-expansion-panel', 'Partner reports').within(() => {
            cy.contains(application.partners[0].details.abbreviation).click();
            cy.contains(application.partners[1].details.abbreviation).should('not.exist');
          });
          cy.contains('mat-row', 'Draft').should('not.contain', 'Start control');
          cy.contains('mat-row', 'Submitted').contains('button', 'Start control').should('be.disabled');

          cy.loginByRequest(testData.controllerUserEdit.email);
          cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
          cy.contains('mat-expansion-panel', 'Partner reports').within(() => {
            cy.contains(application.partners[0].details.abbreviation).click();
            cy.contains(application.partners[1].details.abbreviation).should('not.exist');
          });

          cy.contains('h3', application.partners[0].details.abbreviation).should('be.visible');
          cy.contains('mat-row', 'Draft').should('not.contain', 'Start control');
          cy.contains('mat-cell', 'Submitted').next().should('contain', '1.0');
          cy.contains('Start control').should('be.enabled').click();
          cy.contains('Confirm').should('be.visible').click();

          cy.contains('Project acronym').next().should('contain', application.identification.acronym);
          cy.contains('Name of partner organisation in English language').next().should('contain', 'Name in English to be changed');
          cy.contains('Name of partner organisation in original language').next().should('contain', 'Original name to be changed');
          cy.contains('Partner number').next().should('contain', '1');
          cy.contains('Partner role in the project').next().should('contain', 'Lead partner');

          cy.startModification(applicationId, user.programmeUser.email);
          cy.loginByRequest(user.applicantUser.email);
          application.partners[0].details.nameInEnglish = 'Updated name in english';
          application.partners[0].details.nameInOriginalLanguage = 'Updated name in original language';
          cy.updatePartner(partnerId1, application.partners[0].details);
          cy.runPreSubmissionCheck(applicationId);
          cy.submitProjectApplication(applicationId);
          cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

          cy.then(function () {
            cy.loginByRequest(testData.controllerUserEdit.email);
            cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${this.reportId}/controlReport/identificationTab`, {failOnStatusCode: false});

            cy.contains('Project acronym').next().should('contain', application.identification.acronym);
            cy.contains('Application Form version').next().should('contain', '1.0');
            cy.contains('Name of partner organisation in English language').next().should('contain', 'Name in English to be changed');
            cy.contains('Name of partner organisation in original language').next().should('contain', 'Original name to be changed');
            cy.contains('Partner number').next().should('contain', '1');
            cy.contains('Partner role in the project').next().should('contain', 'Lead partner');
          });

          cy.loginByRequest(user.applicantUser.email);
          cy.addPartnerReport(partnerId1).then(reportId => {
            cy.updatePartnerReportIdentification(partnerId1, reportId, partnerReportIdentification);
            cy.updatePartnerReportExpenditures(partnerId1, reportId, partnerReportExpenditures);
            cy.runPreSubmissionPartnerReportCheck(partnerId1, reportId);
            cy.submitPartnerReport(partnerId1, reportId);

            cy.loginByRequest(testData.controllerUserEdit.email);
            cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId1}/reports`, {failOnStatusCode: false});
            cy.contains('mat-cell', 'Submitted').next().should('contain', '2.0');
            cy.contains('Start control').should('be.enabled').click();
            cy.contains('Confirm').should('be.visible').click();

            cy.contains('Project acronym').next().should('contain', application.identification.acronym);
            cy.contains('Application Form version').next().should('contain', '2.0');
            cy.contains('Name of partner organisation in English language').next().should('contain', 'Updated name in english');
            cy.contains('Name of partner organisation in original language').next().should('contain', 'Updated name in original language');
            cy.contains('Partner number').next().should('contain', '1');
            cy.contains('Partner role in the project').next().should('contain', 'Lead partner');
          });
        });
      });
    });
  });

  it('TB-768 Control report checklist instantiation', () => {
    cy.fixture('project/application-form/control-reports/TB-768.json').then(testData => {

      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {
        const partnerId = this[application.partners[0].details.abbreviation];

        // create controller role/user + assignment
        cy.loginByRequest(user.admin.email);
        testData.controllerRole.name = `controllerRole_${faker.random.alphaNumeric(5)}`;
        testData.controllerUser1.email = faker.internet.email();
        testData.controllerUser2.email = faker.internet.email();
        cy.createRole(testData.controllerRole).then(roleId => {
          testData.controllerUser1.userRoleId = roleId;
          testData.controllerUser2.userRoleId = roleId;
          cy.createUser(testData.controllerUser1);
          cy.createUser(testData.controllerUser2);
          testData.controllerInstitution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
          testData.controllerInstitution.institutionUsers[0].userEmail = testData.controllerUser1.email;
          testData.controllerInstitution.institutionUsers[1].userEmail = testData.controllerUser2.email;
          cy.createInstitution(testData.controllerInstitution).then(institutionId => {
            testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId;
            testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
            cy.assignInstitution(testData.controllerAssignment);
          });
        });

        // create checklist
        testData.controlChecklist.name = `control_checklist_${faker.random.alphaNumeric(5)}`;
        cy.createChecklist(testData.controlChecklist);

        // create partner report
        cy.loginByRequest(user.applicantUser.email);
        cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborator);
        cy.addPartnerReport(partnerId).then(reportId => {
          cy.wrap(reportId).as('reportId');
          cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
          cy.updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures);
          cy.runPreSubmissionPartnerReportCheck(partnerId, reportId);
          cy.submitPartnerReport(partnerId, reportId);

          // start control work
          cy.loginByRequest(testData.controllerUser1.email);
          cy.startControlWork(partnerId, reportId);

          // RTM Group 1
          // instantiate control checklist
          instantiateEmptyChecklist(applicationId, partnerId, reportId, testData.controlChecklist.name);
          // fill form
          fillChecklistForm();
          // save form
          cy.contains('button', 'Save changes').should('be.enabled').click();
          // assert Checklist is in Draft
          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/controlChecklistsTab`, {failOnStatusCode: false});
          cy.get('table mat-row').then(row => {
            expect(row).has.length.of.at.least(1);
            expect(row.get(0).childNodes[1]).to.contain('Draft');
            expect(row.get(0).childNodes[2]).to.contain(testData.controlChecklist.name);
            expect(row.get(0).childNodes[3]).to.contain(testData.controllerUser1.email);
          });

          // RTM Group 2
          // instantiate control checklist
          cy.loginByRequest(testData.controllerUser2.email);
          instantiateEmptyChecklist(applicationId, partnerId, reportId, testData.controlChecklist.name);
          // fill form
          fillChecklistForm();
          // save form
          cy.contains('button', 'Save changes').should('be.enabled').click();
          // finish control checklist
          cy.contains('button', 'Finish checklist').scrollIntoView().should('be.enabled').click();
          cy.get('jems-confirm-dialog').should('be.visible');
          cy.get('jems-confirm-dialog').find('.mat-dialog-actions').contains('Confirm').click();
          // assert Checklist is Finished
          cy.get('table mat-row').then(row => {
            expect(row).has.length.of.at.least(1);
            expect(row.get(0).childNodes[1]).to.contain('Finished');
            expect(row.get(0).childNodes[2]).to.contain(testData.controlChecklist.name);
            expect(row.get(0).childNodes[3]).to.contain(testData.controllerUser2.email);
          });
          cy.get('table mat-row').eq(1).then(row => {
            expect(row).has.length.of.at.least(1);
            expect(row.get(0).childNodes[1]).to.contain('Draft');
            expect(row.get(0).childNodes[2]).to.contain(testData.controlChecklist.name);
            expect(row.get(0).childNodes[3]).to.contain(testData.controllerUser1.email);
          });

          // RTM Group 3
          // create Draft checklist
          instantiateEmptyChecklist(applicationId, partnerId, reportId, testData.controlChecklist.name);
          // -- Controller can delete only own checklist instances in status Draft
          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/controlChecklistsTab`, {failOnStatusCode: false});
          cy.get('mat-row mat-icon').eq(0).scrollIntoView();
          cy.get('mat-row').contains('button', 'delete').should('be.enabled').click();
          cy.contains('Confirm').should('be.enabled').click();
          // -- Controller can't delete checklist instances in status Finished
          cy.contains('button', 'delete').should('be.disabled');
          // -- Controller can't delete checklists instantiated by other users
          cy.get('button:contains("delete")').eq(1).should('be.disabled');
        });
      });
    });
  });
});

function instantiateEmptyChecklist(applicationId, partnerId, reportId, checklistName) {
  cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/controlChecklistsTab`, {failOnStatusCode: false});
  cy.contains('Control checklists').should('be.visible').click();
  cy.contains('Select checklist template').should('be.visible').click()
  cy.contains('span', checklistName).should('be.visible').click();
  cy.contains('instantiate new checklist').should('be.enabled').click();
}

function fillChecklistForm() {
  cy.get('mat-slider').scrollIntoView().should('be.visible').focus().type('{rightarrow}'.repeat(Number(faker.random.numeric())));
  cy.contains('Did I change this question?').should('be.visible')
  cy.contains('mat-form-field', 'Justification').find('textarea').clear().type(faker.random.words(5));
  cy.get('mat-button-toggle-group').scrollIntoView().should('be.visible');
  cy.contains('mat-button-toggle', 'Why not').click()
  cy.contains('mat-form-field', 'Pls just enter something').find('textarea').clear().type(faker.random.words(5));
}
