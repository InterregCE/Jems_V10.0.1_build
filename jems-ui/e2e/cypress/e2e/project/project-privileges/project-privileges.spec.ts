import user from '../../../fixtures/users.json';
import {faker} from '@faker-js/faker';
import call from '../../../fixtures/api/call/1.step.call.json';
import application from '../../../fixtures/api/application/application.json';

context('Project privileges tests', () => {

  it('TB-379 Automatically assign users to projects', () => {
    cy.fixture('project/project-privileges/TB-379.json').then(testData => {
      cy.loginByRequest(user.admin.email);
      testData.privilegedUser.email = faker.internet.email();
      cy.createUser(testData.privilegedUser);
      cy.visit('app/project', {failOnStatusCode: false});

      cy.contains('Assignment').click();
      cy.get('.mat-paginator-range-label').then(paginatorRange => {
        const numberOfPages = +paginatorRange.text().match(/\d - (\d+) of \d+/)[1];

        cy.get(`mat-chip:contains('${testData.privilegedUser.email}')`).should(chipElements => {
          expect(chipElements).to.have.length(numberOfPages);
          expect(chipElements).not.to.have.class('mat-chip-selected-user');
        });
      });
    });
  });

  it('TB-363 Add/remove user privileges to/from project', () => {
    cy.fixture('project/project-privileges/TB-363.json').then(testData => {
      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      });
      cy.loginByRequest(user.applicantUser.email);
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        testData.projectCollaborator.email = faker.internet.email();
        cy.createUser(testData.projectCollaborator, user.admin.email);

        // Add user privileges to the project
        cy.visit(`app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});
        cy.get('jems-application-form-privileges-expansion-panel').then(applicationFormUsers => {
          cy.wrap(applicationFormUsers).contains('+').click();
          cy.wrap(applicationFormUsers).find('input[formcontrolname="userEmail"]').last().type(testData.projectCollaborator.email);
          cy.wrap(applicationFormUsers).contains('Save changes').click();

          cy.get('div.jems-alert-success').should('contain', 'Project collaborators were saved successfully');
        });

        cy.loginByRequest(testData.projectCollaborator.email);
        cy.visit('/', {failOnStatusCode: false});
        cy.get('jems-project-application-list').contains(application.identification.acronym).should('be.visible');

        // Remove user privileges from the project
        cy.loginByRequest(user.applicantUser.email);
        cy.visit(`app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});
        cy.get('jems-application-form-privileges-expansion-panel').then(applicationFormUsers => {
          cy.wrap(applicationFormUsers).find('mat-icon:contains("delete")').last().click();
          cy.wrap(applicationFormUsers).contains('Save changes').click();

          cy.get('div.jems-alert-success').should('contain', 'Project collaborators were saved successfully');
        });

        cy.loginByRequest(testData.projectCollaborator.email);
        cy.visit('/', {failOnStatusCode: false});
        cy.get('jems-project-application-list').contains(application.identification.acronym).should('not.exist');

        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.get('jems-project-detail-page').should('be.empty');
      });
    });
  });

  it('TB-364 Restrict management of project specific privileges', () => {
    cy.fixture('project/project-privileges/TB-364.json').then(testData => {
      cy.loginByRequest(user.admin.email);
      testData.programmeRole.name = `programmeRole_${faker.random.alphaNumeric(5)}`;
      cy.createRole(testData.programmeRole).then(roleId => {
        testData.programmeUser.userRoleId = roleId;
        testData.programmeUser.email = faker.internet.email();
        cy.createUser(testData.programmeUser);
        cy.loginByRequest(testData.programmeUser.email);

        cy.visit('app/project/detail/1', {failOnStatusCode: false});
        cy.contains('Project privileges').should('not.exist');

        cy.visit('app/project/detail/1/privileges', {failOnStatusCode: false});
        cy.get('jems-application-form-privileges-expansion-panel').should('not.exist');
      });
    });
  });

  it('TB-374 Add user to project using project privileges', () => {
    cy.fixture('project/project-privileges/TB-374.json').then(testData => {
      // Preparation for the tests
      cy.loginByRequest(user.admin.email);

      testData.projectOwner.email = faker.internet.email();
      testData.applicantView.email = faker.internet.email();
      testData.applicantEdit.email = faker.internet.email();
      testData.applicantManage.email = faker.internet.email();
      cy.createUser(testData.applicantView, user.admin.email);
      cy.createUser(testData.applicantEdit, user.admin.email);
      cy.createUser(testData.applicantManage, user.admin.email);
      cy.createUser(testData.projectOwner, user.admin.email);

      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      });
      cy.loginByRequest(testData.projectOwner.email);
      cy.createApplication(application).then(applicationId => {
        cy.visit(`/app/project/detail/${applicationId}/annexes`);
        cy.get('input[type=file]').selectFile('cypress\\fixtures\\project\\project-privileges\\TB-374-testFile.txt', {force: true});



        addNewApplicationPrivilegeUser(applicationId, testData.projectOwner, testData.applicantView, 'view');

        cy.contains('div', 'Project collaborators were saved successfully').should('be.visible');

        //checking whether the user can be added twice
        cy.contains('button', '+').click();
        cy.get('input:last').type(testData.applicantView.email);
        cy.contains('div', 'The user emails must be unique').should('be.visible');
        cy.contains('button', 'Discard changes').click();
        cy.logoutByRequest();

        // Testing the view privileges
        cy.loginByRequest(testData.applicantView.email);
        cy.visit('/');
        cy.get('#table:first').contains('div', applicationId).should('be.visible');
        cy.visit(`/app/project/detail/${applicationId}`);
        cy.contains('div', 'Project identification').click();
        cy.get("textarea:first").should('have.attr', 'readonly');
        cy.contains('Project privileges').click();
        cy.get('mat-button-toggle-group:last').contains('span', 'view').click();
        cy.contains('button', 'Save changes').should('not.exist');
        cy.contains('Export').click();
        cy.contains('button', 'Export').should('be.visible');
        cy.contains('Project privileges').click();
        cy.get('mat-button-toggle-group:last').contains('span', 'view').click();
        cy.contains('button', 'Save changes').should('not.exist');
        cy.contains('annexes').click();
        cy.contains('button', 'Upload').should('not.exist');
        cy.contains('mat-icon', 'download').should('be.visible');
        cy.contains('mat-icon', 'edit').should('not.exist');
        cy.contains('Check & Submit').click();
        cy.contains('button', 'Run pre-submission check').should('be.visible');
        cy.contains('button', 'Run pre-submission check').click();
        cy.get('jems-project-application-pre-condition-check-result').should('be.visible');
        cy.contains('button', 'Submit project application').should('not.exist');

        addNewApplicationPrivilegeUser(applicationId, testData.projectOwner, testData.applicantEdit, 'edit');

        // Testing the edit privileges
        cy.loginByRequest(testData.applicantEdit.email);
        cy.visit('/');
        testEditPrivileges(applicationId);
        cy.contains('Project privileges').click();
        cy.get('mat-button-toggle-group:last').contains('span', 'view').click();
        cy.contains('button', 'Save changes').should('not.exist');

        addNewApplicationPrivilegeUser(applicationId, testData.projectOwner, testData.applicantManage, 'manage');

        // Testing the manage privileges
        cy.loginByRequest(testData.applicantManage.email);
        cy.visit('/');
        testEditPrivileges(applicationId);
        cy.contains('Project privileges').click();
        cy.get('mat-button-toggle-group:last').contains('span', 'view').click();
        cy.get('mat-button-toggle-group:last').contains('span', 'edit').click();
        cy.contains('button', 'Save changes').should('be.visible');
      });
    });
  });

  function testEditPrivileges(applicationId) {
    cy.visit('/');
    cy.get('#table:first').contains('div', applicationId).should('be.visible');
    cy.visit(`/app/project/detail/${applicationId}/applicationFormIdentification`);
    cy.get("textarea:first").should('not.have.attr', 'readonly');
    cy.visit(`/app/project/detail/${applicationId}/export`);
    cy.contains('button', 'Export').should('be.visible');
    cy.contains('Application annexes').click();
    cy.contains('button', 'Upload file').should('be.visible');
    cy.contains('mat-icon', 'download').should('be.visible');
    cy.contains('mat-icon', 'edit').should('be.visible');
    cy.contains('Check & Submit').click();
    cy.contains('button', 'Run pre-submission check').should('be.visible');
    cy.contains('button', 'Run pre-submission check').click();
    cy.get('jems-project-application-pre-condition-check-result').should('be.visible');
    cy.contains('button', 'Submit project application').should('be.visible');
  }

  function addNewApplicationPrivilegeUser(applicationId, applicationOwner, newUser, privilegeLevel) {
    cy.loginByRequest(applicationOwner.email);
    cy.visit(`/app/project/detail/${applicationId}`);
    cy.contains('div', 'Project privileges').click();
    cy.contains('button', '+').click();
    cy.get('input:last').type(newUser.email);
    cy.get('mat-button-toggle-group:last').contains('span', privilegeLevel).click();
    cy.contains('button', 'Save changes').click();
  }
})
