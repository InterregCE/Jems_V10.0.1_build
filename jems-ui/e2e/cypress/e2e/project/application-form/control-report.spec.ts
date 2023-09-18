import user from '../../../fixtures/users.json';
import call from '../../../fixtures/api/call/1.step.call.json';
import application from '../../../fixtures/api/application/application.json';
import approvalInfo from '../../../fixtures/api/application/modification/approval.info.json';
import partnerReportIdentification from '../../../fixtures/api/partnerReport/partnerReportIdentification.json';
import partnerReportExpenditures from '../../../fixtures/api/partnerReport/partnerReportExpenditures.json';
import {faker} from "@faker-js/faker";
import controllerUser from "../../../fixtures/api/users/controllerUser.json";
import controllerRole from "../../../fixtures/api/roles/controllerRole.json";

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
          testData.controllerRole.name = `controllerRole_${faker.string.alphanumeric(5)}`;
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
          cy.contains('mat-cell', 'Submitted').next().find('div').should('be.empty')
          cy.contains('mat-cell', 'Submitted').next().next().should('contain', '1.0');
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
          cy.updatePartnerIdentity(partnerId1, application.partners[0].details);
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
            cy.contains('mat-cell', 'Submitted').next().find('div').should('be.empty')
            cy.contains('mat-cell', 'Submitted').next().next().should('contain', '2.0');
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
        testData.controllerRole.name = `controllerRole_${faker.string.alphanumeric(5)}`;
        testData.controllerUser1.email = faker.internet.email();
        testData.controllerUser2.email = faker.internet.email();
        cy.createRole(testData.controllerRole).then((roleId: any) => {
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
        testData.controlChecklist.name = `control_checklist_${faker.string.alphanumeric(5)}`;
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

  it("TB-1083 Control report checklist instantiation after control work is finalized", () => {
    cy.fixture('project/application-form/control-reports/TB-1083.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
        cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {
        const partnerId = this[application.partners[0].details.abbreviation];

        // create controller role/user + assignment
        cy.loginByRequest(user.admin.email);
        testData.controllerRole.name = `controllerRole_${faker.string.alphanumeric(5)}`;
        testData.controllerUser1.email = faker.internet.email();
        cy.createRole(testData.controllerRole).then(roleId => {
          testData.controllerUser1.userRoleId = roleId;
          cy.createUser(testData.controllerUser1);
          testData.controllerInstitution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
          testData.controllerInstitution.institutionUsers[0].userEmail = testData.controllerUser1.email;
          cy.createInstitution(testData.controllerInstitution).then(institutionId => {
            testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId;
            testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
            cy.assignInstitution(testData.controllerAssignment);
          });
        });
        cy.loginByRequest(user.applicantUser.email);
        cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborator);
        cy.addPartnerReport(partnerId).then(reportId => {
          cy.wrap(reportId).as('reportId');
          cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
          cy.updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures);
          cy.runPreSubmissionPartnerReportCheck(partnerId, reportId);
          cy.submitPartnerReport(partnerId, reportId);

          cy.loginByRequest(testData.controllerUser1.email);
          cy.startControlWork(partnerId, reportId);

          cy.startControlChecklist(partnerId, reportId, testData.checklist[0]);
          cy.startControlChecklist(partnerId, reportId, testData.checklist[1]).then(checklistId => {
            cy.finishControlChecklist(partnerId, reportId, checklistId);
          });

          cy.finalizeControl(partnerId, reportId);

          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/identification`, {failOnStatusCode: false});

          cy.contains('button', 'Open controller work').click();
          cy.contains('a span', 'Control checklists').click();

          cy.contains('Select checklist template').parent().parent().click();
          cy.getProgrammeChecklists().then(response => {
            let checklists = response.body.map(e => e['name']);
            console.log(checklists);
            cy.get('mat-option span span').each(checklist => expect(checklist.text()).be.oneOf(checklists));
          });
          cy.contains('mat-option span', 'HIT - Accounting').click();

          cy.contains('button', 'start new checklist').click();

          cy.get('mat-button-toggle-group button').click({multiple: true});
          cy.get('textarea').should('exist').each(e => cy.wrap(e).type(faker.random.word()));

          cy.contains('button', 'Save changes').click();
          cy.contains('button', 'Finish checklist').click();
          cy.contains('button', 'Confirm').click();

          for (let i = 1; i < 3; i++) {
            cy.get('mat-row').eq(i).click();

            cy.contains('button', 'Return to checklist initiator').should('not.exist');
            cy.get('mat-button-toggle-group button').should('be.disabled').should('exist');
            cy.get('textarea').should('be.disabled').should('exist');

            cy.contains('a span', 'Control checklists').click();
          }
        });
      });
    });
  });

  it("TB-815 Control documents", function() {
    cy.fixture("controller/TB-815.json").then(testData => {

      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {
        const partnerId = this[application.partners[0].details.abbreviation];

        // create controller role/user + assignment
        cy.loginByRequest(user.admin.email);
        testData.controllerRole.name = `controllerRole_${faker.string.alphanumeric(5)}`;
        testData.controllerUser1.email = faker.internet.email();
        cy.createRole(testData.controllerRole).then(roleId => {
          testData.controllerUser1.userRoleId = roleId;
          cy.createUser(testData.controllerUser1);
          testData.controllerInstitution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
          testData.controllerInstitution.institutionUsers[0].userEmail = testData.controllerUser1.email;
          cy.createInstitution(testData.controllerInstitution).then(institutionId => {
            testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId;
            testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
            cy.assignInstitution(testData.controllerAssignment);
          });
        });
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

          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/document`, {failOnStatusCode: false});

          cy.get('input[type="file"]')
            .scrollIntoView()
            .invoke('show')
            .selectFile('cypress/fixtures/controller/fileToUpload.txt')
            .invoke('hide');

          cy.contains('fileToUpload.txt').should('be.visible');

          cy.contains('mat-icon', 'edit').scrollIntoView().click();
          cy.contains('div.mat-form-field-flex', 'Description').scrollIntoView().within(() => {
            cy.get('textarea').type(faker.random.words(10));
          })
          cy.contains('button', 'Save').click();
          cy.contains('File description for \'fileToUpload.txt\' has been updated.').should('be.visible');

          cy.contains('mat-icon', 'delete').click();
          cy.contains('span', 'Confirm').click();
          cy.contains('File \'fileToUpload.txt\' has been deleted successfully.').should('be.visible');

          cy.get('input[type="file"]')
            .scrollIntoView()
            .invoke('show')
            .selectFile('cypress/fixtures/controller/fileToUpload.txt')
            .invoke('hide');

          cy.contains('fileToUpload.txt').should('be.visible');


          cy.loginByRequest(user.applicantUser.email);
          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/document`, {failOnStatusCode: false});

          cy.contains('fileToUpload.txt').should('be.visible');

          cy.contains('mat-icon', 'delete').should('not.exist');
          cy.contains('mat-icon', 'edit').should('not.exist');

          cy.get('input[type="file"]')
            .scrollIntoView()
            .invoke('show')
            .selectFile('cypress/fixtures/controller/fileToUpload2.txt')
            .invoke('hide');

          cy.contains('fileToUpload.txt').should('be.visible');

          cy.contains('mat-icon', 'edit').scrollIntoView().click();
          cy.contains('div.mat-form-field-flex', 'Description').scrollIntoView().within(() => {
            cy.get('textarea').type(faker.random.words(10));
          })
          cy.contains('button', 'Save').click();
          cy.contains('File description for \'fileToUpload2.txt\' has been updated.').should('be.visible');

          cy.contains('mat-icon', 'delete').click();
          cy.contains('span', 'Confirm').click();
          cy.contains('File \'fileToUpload2.txt\' has been deleted successfully.').should('be.visible');

          cy.wait(500);
          cy.contains('mat-icon', 'file_download').clickToDownload(`/api/project/report/partner/control/byPartnerId/${partnerId}/byReportId/${reportId}/byFileId/*?`, 'txt').then(returnValue => {
            cy.wrap(returnValue.fileName === 'fileToUpload.txt').as('assertion');
            cy.get('@assertion').should('eq', true);
          });
        });
      });
    });
  });

  it('TB-933 Controller identification - Controller selection', function () {
    cy.fixture('controller/TB-933.json').then(testData => {

      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
        const partnerId = this[application.partners[0].details.abbreviation];

        cy.loginByRequest(user.admin.email);
        const controllerUser1 = {...controllerUser};
        const controllerUser2 = {...controllerUser};
        cy.createRole(controllerRole).then(roleId => {
          controllerUser1.email = faker.internet.email();
          controllerUser1.userRoleId = roleId;
          controllerUser2.email = faker.internet.email();
          controllerUser2.userRoleId = roleId;
          cy.createUser(controllerUser1);
          cy.createUser(controllerUser2);
          testData.institution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
          testData.institution.institutionUsers[0].userEmail = controllerUser1.email;
          testData.institution.institutionUsers[1].userEmail = controllerUser2.email;
          cy.createInstitution(testData.institution).then(institutionId => {
            testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId;
            testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
            cy.assignInstitution(testData.controllerAssignment);
          });
        });
        cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborator);

        cy.createInstitution(testData.institution).then(institutionId => {
          testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId;
          testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
          cy.assignInstitution(testData.controllerAssignment);
        });

        cy.addPartnerReport(partnerId).then(reportId => {
          cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
          cy.updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures);
          cy.runPreSubmissionPartnerReportCheck(partnerId, reportId);
          cy.submitPartnerReport(partnerId, reportId);

          cy.loginByRequest(controllerUser1.email);
          cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports`, {failOnStatusCode: false});
          cy.contains('Start control').should('be.enabled').click();
          cy.contains('Confirm').should('be.visible').click();
          cy.contains('Control Identification').should('be.visible').click();

          cy.get('#control-project-partner-controller').scrollIntoView().contains('div', 'Controller name').click();
          cy.get('.mat-autocomplete-visible').children('mat-option').should('have.length', 2);
          cy.get('.mat-autocomplete-visible').children('mat-option').eq(0).contains(testData.institution.institutionUsers[0].userEmail);
          cy.get('.mat-autocomplete-visible').children('mat-option').eq(1).contains(testData.institution.institutionUsers[1].userEmail);
          cy.contains('mat-option', testData.institution.institutionUsers[0].userEmail).click();
          cy.contains('Save changes').should('be.visible').click();

          cy.get('input[name="controlUser"]').eq(1).click();
          cy.contains('mat-option', testData.institution.institutionUsers[0].userEmail).click();
          cy.contains('Save changes').should('be.visible').click();

          cy.loginByRequest(user.admin.email);
          cy.visit(`app/system/user`, {failOnStatusCode: false});

          cy.contains('mat-row', testData.institution.institutionUsers[0].userEmail).click();
          cy.contains('button', 'Edit').click();

          controllerUser1.name = faker.name.firstName();
          controllerUser1.surname = faker.name.lastName();
          controllerUser1.email = faker.internet.email();

          cy.get('input[name="name"]').clear().type(controllerUser1.name);
          cy.get('input[name="surname"]').clear().type(controllerUser1.surname);
          cy.get('input[name="email"]').clear().type(controllerUser1.email);
          cy.contains('Save changes').should('be.visible').click();

          const userInfoInitial = `${controllerUser1.name} ${controllerUser1.surname} - ${controllerUser1.email}`;
          cy.loginByRequest(controllerUser1.email);
          cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/identificationTab`, {failOnStatusCode: false});
          cy.get('#control-project-partner-controller input[name="controlUser"]').eq(0).should('have.value', userInfoInitial);
          cy.get('#control-project-partner-controller input[name="controlUser"]').eq(1).should('have.value', userInfoInitial);

          cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/overviewAndFinalizeTab`, {failOnStatusCode: false});


          cy.contains('Run pre-submission check').scrollIntoView();
          cy.contains('Run pre-submission check').click();
          cy.get('jems-project-application-pre-condition-check-result').should('be.visible');
          cy.contains('button', 'Finalize control').click();
          cy.contains('Confirm').should('be.visible').click();
          cy.get('mat-chip > mat-icon').next().contains('Certified');

          cy.loginByRequest(user.admin.email);
          cy.visit(`app/system/user`, {failOnStatusCode: false});

          cy.contains('mat-row', controllerUser1.email).click();
          cy.contains('button', 'Edit').click();

          controllerUser1.name = faker.name.firstName();
          controllerUser1.surname = faker.name.lastName();
          controllerUser1.email = faker.internet.email();

          cy.get('input[name="name"]').clear().type(controllerUser1.name);
          cy.get('input[name="surname"]').clear().type(controllerUser1.surname);
          cy.get('input[name="email"]').clear().type(controllerUser1.email);
          cy.contains('Save changes').should('be.visible').click();

          const userInfoUpdated = `${controllerUser1.name} ${controllerUser1.surname} - ${controllerUser1.email}`;
          cy.loginByRequest(controllerUser1.email);
          cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/identificationTab`, {failOnStatusCode: false});
          cy.get('#control-project-partner-controller input[name="controlUser"]').eq(0).should('have.value', userInfoUpdated);
          cy.get('#control-project-partner-controller input[name="controlUser"]').eq(0).should('be.disabled')
          cy.get('#control-project-partner-controller input[name="controlUser"]').eq(1).should('have.value', userInfoUpdated);
          cy.get('#control-project-partner-controller input[name="controlUser"]').eq(1).should('be.disabled')
          cy.contains(userInfoInitial).should('not.exist');
        });
      });
    });
  });

  it('TB-934 Controller identification - Control institution name change', function () {
    cy.fixture('controller/TB-934.json').then(testData => {

      cy.loginByRequest(user.applicantUser.email);
      cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {
        const partnerId = this[application.partners[0].details.abbreviation];

        // create controller role/user + assignment
        cy.loginByRequest(user.admin.email);
        testData.controllerRole.name = `controllerRole_${faker.string.alphanumeric(5)}`;
        testData.controllerUser1.email = faker.internet.email();
        cy.createRole(testData.controllerRole).then(roleId => {
          testData.controllerUser1.userRoleId = roleId;
          cy.createUser(testData.controllerUser1);
          testData.controllerInstitution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
          testData.controllerInstitution.institutionUsers[0].userEmail = testData.controllerUser1.email;
          cy.createInstitution(testData.controllerInstitution).then(institutionId => {
            testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId;
            testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
            cy.assignInstitution(testData.controllerAssignment);
          });
        });
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
          // check if institution is displayed correctly
          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/identificationTab`, {failOnStatusCode: false});
          cy.contains('mat-form-field', 'Control institution/body/intermediate body responsible for the verification (filled automatically)').find('input').should('be.disabled', 'have.value', testData.controllerInstitution.name);
          cy.get('#control-project-partner-controller').scrollIntoView().contains('div', 'Controller name').click();
          cy.contains('mat-option', testData.controllerUser1.email).click();
          cy.contains('button', 'Save changes').click();

          // RTM Group 2
          // change name of institution
          cy.loginByRequest(user.admin.email);
          cy.visit(`/app/controller/`, {failOnStatusCode: false});
          cy.contains('div', testData.controllerInstitution.name).parents('mat-row').find('.mat-column-anchor').click();
          testData.controllerInstitution.updatedName = `${faker.word.adjective()} ${faker.word.noun()}`;
          cy.contains('mat-form-field', 'Name').find('input').clear().type(testData.controllerInstitution.updatedName);
          cy.contains('button', 'Save changes').click();

          // RTM Group 3
          // check if institution was updated
          cy.loginByRequest(testData.controllerUser1.email);
          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/identificationTab`, {failOnStatusCode: false});
          cy.contains('mat-form-field', 'Control institution/body/intermediate body responsible for the verification (filled automatically)').find('input').should('have.value', testData.controllerInstitution.updatedName);

          // RTM Group 4
          // submit control report
          cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/overviewAndFinalizeTab`, {failOnStatusCode: false});
          cy.contains('Run pre-submission check').scrollIntoView();
          cy.contains('Run pre-submission check').click();
          cy.get('jems-project-application-pre-condition-check-result').should('be.visible');
          cy.contains('button', 'Finalize control').click();
          cy.contains('Confirm').should('be.visible').click();
          cy.get('mat-chip > mat-icon').next().contains('Certified');

          //change name of institution again to the original
          cy.loginByRequest(user.admin.email);
          cy.visit(`/app/controller/`, {failOnStatusCode: false});
          cy.contains('div', testData.controllerInstitution.updatedName).parents('mat-row').find('.mat-column-anchor').click();
          cy.contains('mat-form-field', 'Name').find('input').clear().type(testData.controllerInstitution.name);
          cy.contains('button', 'Save changes').click();

          //check if institution was not updated
          cy.loginByRequest(testData.controllerUser1.email);
          cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/identificationTab`, {failOnStatusCode: false});
          cy.contains('mat-form-field', 'Control institution/body/intermediate body responsible for the verification (filled automatically)').find('input').should('have.value', testData.controllerInstitution.updatedName);
        });
      });
    });
  });
});

function instantiateEmptyChecklist(applicationId, partnerId, reportId, checklistName) {
  cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/controlChecklistsTab`, {failOnStatusCode: false});
  cy.contains('Control checklists').should('be.visible').click();
  cy.wait(500);
  cy.contains('div', 'Select checklist template').should('be.visible').click()
  cy.contains('span', checklistName).should('be.visible').click();
  cy.contains('start new checklist').should('be.enabled').click();
}

function fillChecklistForm() {
  cy.get('mat-slider').scrollIntoView().should('be.visible').focus().type('{rightarrow}'.repeat(Number(faker.random.numeric())));
  cy.contains('Did I change this question?').should('be.visible')
  cy.contains('mat-form-field', 'Justification').find('textarea').clear().type(faker.random.words(5));
  cy.get('mat-button-toggle-group').scrollIntoView().should('be.visible');
  cy.contains('mat-button-toggle', 'Why not').click()
  cy.contains('mat-form-field', 'Pls just enter something').find('textarea').clear().type(faker.random.words(5));
}
