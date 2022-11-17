import user from '../../../fixtures/users.json';
import call from '../../../fixtures/api/call/1.step.call.json';
import application from '../../../fixtures/api/application/application.json';
import partner from '../../../fixtures/api/application/partner/partner.json';
import approvalInfo from '../../../fixtures/api/application/modification/approval.info.json';
import {faker} from "@faker-js/faker";

context('Control report tests', () => {
  it('TB-767 Control report creation', () => {
    cy.fixture('project/application-form/control-reports/TB-767.json').then(testData => {
      cy.loginByRequest(user.programmeUser.email);

      // create call and application
      call.preSubmissionCheckSettings.pluginKey = 'jems-pre-condition-check-off';
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        application.partners.push(partner);
        application.partners[0].details.nameInOriginalLanguage = '';
        application.partners[0].details.nameInEnglish = '';
        application.partners[1].details.nameInOriginalLanguage = '';
        application.partners[1].details.nameInEnglish = '';
        cy.publishCall(callId);
        cy.loginByRequest(user.applicantUser.email);
        cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {

          cy.then(function () {
            const partnerId1 = this[application.partners[0].details.abbreviation];
            const partnerId2 = this[application.partners[1].details.abbreviation];

            // create controller role/user + assignment
            cy.loginByRequest(user.admin.email);
            testData.controllerRole.name = `controllerRole_${faker.random.alphaNumeric(5)}`;
            testData.controllerUser.email = faker.internet.email();
            cy.createRole(testData.controllerRole).then(roleId => {
              testData.controllerUser.userRoleId = roleId;
              cy.createUser(testData.controllerUser);
              testData.controllerInstitution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
              testData.controllerInstitution.institutionUsers[0].userEmail = testData.controllerUser.email;
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
              cy.submitPartnerReport(partnerId1, reportId);
            });
            cy.addPartnerReport(partnerId2);
            cy.addPartnerReport(partnerId2).then(reportId => {
              cy.submitPartnerReport(partnerId2, reportId);
            });

            cy.loginByRequest(testData.controllerUser.email);
            cy.visit('/');
            cy.contains(applicationId).click();

            cy.contains(application.partners[0].details.abbreviation).click();
            cy.contains('h3', application.partners[0].details.abbreviation).should('be.visible');
            cy.contains('mat-row', 'Draft').should('not.contain', 'Start control');
            cy.contains('mat-cell', 'Submitted').next().should('contain', '1.0');
            cy.contains('Start control').should('be.enabled').click();
            cy.contains('Confirm').should('be.visible').click();

            cy.contains('Project acronym').next().should('contain', application.identification.acronym);
            cy.contains('Name of partner organisation in English language').next().should('be.empty');
            cy.contains('Name of partner organisation in original language').next().should('be.empty');
            cy.contains('Partner number').next().should('contain', '1');
            cy.contains('Partner role in the project').next().should('contain', 'Lead partner');


            cy.contains(application.partners[1].details.abbreviation).click();
            cy.contains('mat-row', 'Draft').should('not.contain', 'Start control');
            cy.contains('mat-row', 'Submitted').contains('button', 'Start control').should('be.disabled');

            cy.startModification(applicationId, user.programmeUser.email);
            cy.loginByRequest(user.applicantUser.email);
            application.partners[0].details.nameInEnglish = 'Updated name in english';
            application.partners[0].details.nameInOriginalLanguage = 'Updated name in original language';
            cy.updatePartner(partnerId1, application.partners[0].details);
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);
            cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

            cy.then(function () {
              cy.loginByRequest(testData.controllerUser.email);
              cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${this.reportId}/controlReport/identificationTab`, {failOnStatusCode: false});
            });

            cy.contains('Project acronym').next().should('contain', application.identification.acronym);
            cy.contains('Application Form version').next().should('contain', '1.0');
            cy.contains('Name of partner organisation in English language').next().should('be.empty');
            cy.contains('Name of partner organisation in original language').next().should('be.empty');
            cy.contains('Partner number').next().should('contain', '1');
            cy.contains('Partner role in the project').next().should('contain', 'Lead partner');

            cy.loginByRequest(user.applicantUser.email);
            cy.addPartnerReport(partnerId1).then(reportId => {
              cy.submitPartnerReport(partnerId1, reportId);
              cy.loginByRequest(testData.controllerUser.email);
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
  });
});