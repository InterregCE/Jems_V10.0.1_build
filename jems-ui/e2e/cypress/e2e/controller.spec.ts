import user from "../fixtures/users.json";
import {faker} from "@faker-js/faker";
import call from "../fixtures/api/call/1.step.call.json";
import application from "../fixtures/api/application/application.json";
import partnerReportIdentification from "../fixtures/api/partnerReport/partnerReportIdentification.json";
import partnerReportExpenditures from "../fixtures/api/partnerReport/partnerReportExpenditures.json";

context('Controller tests', () => {

  it('TB-810 Controller institutions can be created', () => {
    cy.fixture('controller/TB-810.json').then(testData => {
      cy.loginByRequest(user.admin.email);
      testData.controllerUser1.email = faker.internet.email();
      testData.controllerUser2.email = faker.internet.email();
      cy.createUser(testData.controllerUser1);
      cy.createUser(testData.controllerUser2);
      testData.institution.users[0].email = testData.controllerUser1.email;
      testData.institution.users[1].email = testData.controllerUser2.email;
      testData.controllerCreatorRole.name = `controllerCreator_${faker.random.alphaNumeric(5)}`;
      testData.controllerCreatorUser.email = faker.internet.email();
      cy.createRole(testData.controllerCreatorRole).then(roleId => {
        testData.controllerCreatorUser.userRoleId = roleId;
        cy.createUser(testData.controllerCreatorUser);
      });
      cy.loginByRequest(testData.controllerCreatorUser.email);

      cy.visit('/');
      cy.contains('Controllers').click();

      cy.contains('Add institution').click();
      testData.institution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
      cy.contains('div', 'Name').find('input').type(testData.institution.name);
      cy.contains('div', 'Description').find('textarea').type(testData.institution.description);

      testData.institution.nuts.forEach(nuts => {
        cy.contains('mat-checkbox', nuts).click();
      });

      testData.institution.users.forEach(user => {
        cy.contains('button', 'Add user').click();
        cy.contains('div #institution-collaborators-table-content', 'Cannot be blank').within(() => {
          cy.contains('button', user.accessLevel).click();
          cy.contains('div', 'Jems username').find('input').type(user.email);
        });
      });

      cy.contains('button', 'Create').click();
      cy.get('#institution-collaborators-table-content input').should('be.disabled');

      cy.contains('Add user').click()
      cy.contains('div #institution-collaborators-table-content', 'Cannot be blank').then((row) => {
        cy.wrap(row).contains('div', 'Jems username').find('input').type('applicant');
        cy.wrap(row).get('div:contains("Input data are not valid")').should('be.visible');
        cy.wrap(row).contains('div', 'Jems username').find('input').type('.user@jems.eu');
      });

      cy.contains('Save changes').click();
      cy.contains('Not possible to save: Make sure the username is correctly typed and privileged to monitor projects.').should('be.visible');
      cy.get('button:contains("delete")').last().click();
      cy.contains('Save changes').should('be.visible').click();
      cy.contains('Controller institution was updated successfully').should('be.visible');

      cy.contains('Institutions').click();
      cy.contains('mat-row', testData.institution.name).should('be.visible');
      cy.contains(testData.institution.nutsVerification).should('be.visible');
    });
  });

  it('TB-935 Controller institutions can be assigned to project partner', () => {
    cy.fixture('controller/TB-935.json').then(testData => {

      cy.loginByRequest(user.admin.email);
      testData.controllerRole.name = `controller_${faker.random.alphaNumeric(5)}`;
      testData.controllerUser.email = faker.internet.email();
      cy.createRole(testData.controllerRole).then(roleId => {
        testData.controllerUser.userRoleId = roleId;
        cy.createUser(testData.controllerUser);
      });

      testData.institution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
      cy.loginByRequest(testData.controllerUser.email);

      cy.createInstitution(testData.institution).then(institutionId => {
        cy.loginByRequest(user.programmeUser.email);
        cy.createCall(call, user.programmeUser.email).then(callId => {
          application.details.projectCallId = callId;
          cy.publishCall(callId);
          cy.loginByRequest(user.applicantUser.email);
          cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
            cy.loginByRequest(testData.controllerUser.email);
            cy.visit('/');
            cy.contains('Controllers').click();
            cy.contains('Assignment').click();
            cy.get('table mat-row').then(row => {
              cy.contains('mat-header-cell', 'ProjectID').click().click();
              cy.get(`mat-row:contains(${applicationId})`).filter(':contains("Project Partner")').contains('Select Institution').click();
              cy.contains('mat-option', testData.institution.name).click();
              cy.contains('Save changes').click();

              cy.wait(2000);
              cy.get(`mat-row:contains(${applicationId})`).filter(':contains("Lead Partner")').contains('Select Institution').click();
              cy.contains('mat-option', testData.institution.name).should('not.exist');

              cy.loginByRequest(user.applicantUser.email);
              cy.visit(`/app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});
              cy.contains('mat-panel-title', 'Lead Partner').parent().contains('No control institution assigned').should('be.visible');
              cy.contains('mat-panel-title', 'Project Partner').parent().scrollIntoView().contains(`${testData.institution.name}`).should('be.visible');

              cy.loginByRequest(testData.controllerUser.email);
              cy.visit(`/app/controller/${institutionId}`, {failOnStatusCode: false});
              cy.get('input').eq(0).clear().type('Updated institution name');
              cy.contains('Save changes').click();

              cy.loginByRequest(user.applicantUser.email);
              cy.visit(`/app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});
              cy.contains('mat-panel-title', 'Project Partner').parent().scrollIntoView().contains('Updated institution name').should('be.visible');
            });
          });
        });
      });
    });
  });

  it('TB-933 Controller identification - Controller selection', () => {
    cy.fixture('controller/TB-933.json').then(testData => {

      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
        cy.loginByRequest(user.applicantUser.email);
        cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {

          cy.loginByRequest(user.admin.email);
          const partnerId = this[application.partners[0].details.abbreviation];

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

          cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborator);

          cy.createInstitution(testData.controllerInstitution).then(institutionId => {
            testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId;
            testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
            cy.assignInstitution(testData.controllerAssignment);
          });

          cy.addPartnerReport(partnerId).then(reportId => {
            cy.wrap(reportId).as('reportId');
            cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
            cy.updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures);
            cy.runPreSubmissionPartnerReportCheck(partnerId, reportId);
            cy.submitPartnerReport(partnerId, reportId);

            cy.loginByRequest(testData.controllerUser1.email);
            cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports`, {failOnStatusCode: false});
            cy.contains('Start control').should('be.enabled').click();
            cy.contains('Confirm').should('be.visible').click();
            cy.wait(2000);

            cy.get('#control-project-partner-controller').scrollIntoView().contains('Controller name').click({force: true});
            cy.get('.mat-autocomplete-visible').children('mat-option').should('have.length', 2);
            cy.get('.mat-autocomplete-visible').children('mat-option').eq(0).contains(testData.controllerInstitution.institutionUsers[0].userEmail);
            cy.get('.mat-autocomplete-visible').children('mat-option').eq(1).contains(testData.controllerInstitution.institutionUsers[1].userEmail);
            cy.contains('mat-option', testData.controllerInstitution.institutionUsers[0].userEmail).click({force: true});
            cy.contains('Save changes').click();

            cy.get('input[name="controlUser"]').eq(1).click();
            cy.contains('mat-option', testData.controllerInstitution.institutionUsers[0].userEmail).click();
            cy.wait(2000);
            cy.contains('Save changes').click();

            cy.loginByRequest(user.admin.email);
            cy.visit(`app/system/user`, {failOnStatusCode: false});

            cy.contains('mat-row', testData.controllerInstitution.institutionUsers[0].userEmail).click();
            cy.contains('button', 'Edit').click();

            testData.controllerUser1.name = faker.name.firstName();
            testData.controllerUser1.surname = faker.name.lastName();
            testData.controllerUser1.email = faker.internet.email();

            cy.get('input[name="name"]').clear().type(testData.controllerUser1.name);
            cy.get('input[name="surname"]').clear().type(testData.controllerUser1.surname);
            cy.get('input[name="email"]').clear().type(testData.controllerUser1.email);
            cy.contains('Save changes').click();

            const userInfoInitial = `${testData.controllerUser1.name} ${testData.controllerUser1.surname} - ${testData.controllerUser1.email}`;
            cy.loginByRequest(testData.controllerUser1.email);
            cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/identificationTab`, {failOnStatusCode: false});
            cy.get('#control-project-partner-controller input[name="controlUser"]').eq(0).should('have.value', userInfoInitial);
            cy.get('#control-project-partner-controller input[name="controlUser"]').eq(1).should('have.value', userInfoInitial);

            cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/overviewAndFinalizeTab`, {failOnStatusCode: false});
            cy.contains('Finalize control').click();
            cy.contains('Confirm').should('be.visible').click();
            cy.wait(2000);

            cy.loginByRequest(user.admin.email);
            cy.visit(`app/system/user`, {failOnStatusCode: false});

            cy.contains('mat-row', testData.controllerUser1.email).click();
            cy.contains('button', 'Edit').click();

            testData.controllerUser1.name = faker.name.firstName();
            testData.controllerUser1.surname = faker.name.lastName();
            testData.controllerUser1.email = faker.internet.email();

            cy.get('input[name="name"]').clear().type(testData.controllerUser1.name);
            cy.get('input[name="surname"]').clear().type(testData.controllerUser1.surname);
            cy.get('input[name="email"]').clear().type(testData.controllerUser1.email);
            cy.contains('Save changes').click();

            const userInfoUpdated = `${testData.controllerUser1.name} ${testData.controllerUser1.surname} - ${testData.controllerUser1.email}`;
            cy.loginByRequest(testData.controllerUser1.email);
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
  });

});
