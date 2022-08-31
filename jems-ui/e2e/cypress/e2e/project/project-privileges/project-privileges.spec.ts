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
      Cypress.on('uncaught:exception', (err) => {
        return !err.message.includes('ResizeObserver loop limit exceeded');
      })

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
        cy.visit(`/app/project/detail/${applicationId}/annexes`, {failOnStatusCode: false});
        cy.get('input[type=file]').selectFile('cypress/fixtures/project/project-privileges/TB-374-testFile.txt', {force: true});

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
        cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.contains('div', 'Project identification').click();
        cy.get("textarea:first").should('have.attr', 'readonly');
        cy.wait(100);
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
        cy.visit('/', {failOnStatusCode: false});
        testEditPrivileges(applicationId);
        cy.contains('Project privileges').click();
        cy.get('mat-button-toggle-group:last').contains('span', 'view').click();
        cy.contains('button', 'Save changes').should('not.exist');

        addNewApplicationPrivilegeUser(applicationId, testData.projectOwner, testData.applicantManage, 'manage');

        // Testing the manage privileges
        cy.loginByRequest(testData.applicantManage.email);
        cy.visit('/', {failOnStatusCode: false});
        testEditPrivileges(applicationId);
        cy.contains('Project privileges').click();
        cy.get('mat-button-toggle-group:last').contains('span', 'view').click();
        cy.get('mat-button-toggle-group:last').contains('span', 'edit').click();
        cy.contains('button', 'Save changes').should('be.visible');
      });
    });
  });

  it('TB-375 Add non-valid users to project using project privileges; remove all managers', () => {
    cy.fixture('project/project-privileges/TB-375.json').then(testData => {
      cy.loginByRequest(user.admin.email);
      testData.programmeUser.email = faker.internet.email();
      testData.applicationCreator.email = faker.internet.email();
      cy.createUser(testData.programmeUser);
      cy.createUser(testData.applicationCreator);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      });
      cy.loginByRequest(testData.applicationCreator.email);
      cy.createApplication(application).then(applicationId => {
        cy.visit(`/app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});
      });
      cy.contains('button', '+').click();
      cy.get('input:last').type(faker.internet.email());
      cy.contains('button', 'Save changes').click();
      cy.contains('div', 'Input data are not valid').should('be.visible');
      cy.get('.jems-layout-row:last').contains('mat-icon', 'delete').click();
      cy.contains('button', '+').click();
      cy.get('input:last').type(testData.programmeUser.email);
      cy.contains('button', 'Save changes').click();
      cy.contains('div', 'Input data are not valid').should('be.visible');
      cy.get('.jems-layout-row:first').contains('span', 'edit').click();
      cy.contains('div', 'At least one user must have the level manage').should('be.visible');
      cy.contains('button', 'Save changes').should('be.disabled');
    });
  });

  it('TB-380 Manually assign/clear users to a project', () => {
    cy.fixture('project/project-privileges/TB-380.json').then(testData => {
      cy.loginByRequest(user.admin.email);

      testData.monitorRole.name = `monitorRole_${faker.random.alphaNumeric(5)}`;
      cy.createRole(testData.monitorRole).then(roleId => {
        testData.monitorUser1.userRoleId = roleId;
        testData.monitorUser2.userRoleId = roleId;
        testData.monitorUser1.email = faker.internet.email();
        testData.monitorUser2.email = faker.internet.email();
        cy.createUser(testData.monitorUser1);
        cy.createUser(testData.monitorUser2);
      });

      // Create and submit project
      cy.loginByRequest(user.programmeUser.email);
      call.preSubmissionCheckSettings.pluginKey = "jems-pre-condition-check-off";
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      });
      cy.loginByRequest(user.applicantUser.email);
      cy.createApplication(application).then(applicationId1 => {
        const firstApplicationAcronym = application.details.acronym;
        cy.submitProjectApplication(applicationId1);

        cy.createApplication(application).then(applicationId2 => {
          const secondApplicationAcronym = application.details.acronym;
          cy.submitProjectApplication(applicationId2);

          // Adding users to the projects
          cy.loginByRequest(user.admin.email);
          cy.visit('/');
          cy.contains('span.nav-text', 'Applications').click();
          cy.contains('div.mat-tab-label-content', 'Assignment').click();

          cy.contains('jems-project-application-list-user-assignments mat-row', applicationId1).find('input').click();
          cy.get('div[role="listbox"]:first').children().within(()=>{
            cy.contains('mat-option', testData.monitorUser1.email).click();
          })
          cy.contains('mat-row', applicationId1).find('input').click();
          cy.get('div[role="listbox"]:first').children().within(()=>{
            cy.contains('mat-option', testData.monitorUser2.email).click();
          })
          cy.contains('mat-row', applicationId2).find('input').click();
          cy.get('div[role="listbox"]:first').children().within(()=>{
            cy.contains('mat-option', testData.monitorUser1.email).click();
          });

          cy.contains('button', 'Save changes').click();
          cy.contains('Users has been successfully assigned to project(s).').should('be.visible');
          cy.contains('Users has been successfully assigned to project(s).').should('not.exist');
          
          // checking for the added users
          cy.contains('mat-row', applicationId1).within(() => {
            cy.contains('mat-chip.mat-chip-selected-user', testData.monitorUser1.email).should('exist');
            cy.contains('mat-chip.mat-chip-selected-user', testData.monitorUser2.email).should('exist');
          })
          cy.contains('mat-row', applicationId2).within(() => {
            cy.contains('mat-chip.mat-chip-selected-user', testData.monitorUser1.email).should('exist');
          })

          // removing users from one of the projects
          cy.contains('mat-row', applicationId1).within(() => {
            cy.contains('mat-icon', 'highlight_off').click();
          })
          cy.contains('button', 'Save changes').click();
          cy.contains('Users has been successfully assigned to project(s).').should('be.visible');
          cy.contains('Users has been successfully assigned to project(s).').should('not.exist');

          // checking for the removed users
          cy.contains('mat-row', applicationId1).within(() => {
            cy.get('mat-chip.mat-chip-selected-user').should('not.exist');
          })
          cy.contains('mat-row', applicationId2).within(() => {
            cy.contains('mat-chip.mat-chip-selected-user', testData.monitorUser1.email).should('exist');
          })

          // testing by logging into the users accounts
          cy.loginByRequest(testData.monitorUser1.email);
          cy.visit('/', {failOnStatusCode: false});
          cy.contains(firstApplicationAcronym).should('not.exist');
          cy.contains(secondApplicationAcronym).should('be.visible');
          cy.contains(secondApplicationAcronym).click();
          cy.contains('div', 'Project identification').click();
          cy.get('textarea.mat-input-element:first').should('have.attr', 'readonly');

          cy.loginByRequest(testData.monitorUser2.email);
          cy.visit('/', {failOnStatusCode: false});
          cy.contains(firstApplicationAcronym).should('not.exist');
          cy.contains(secondApplicationAcronym).should('not.exist');
          cy.contains('No projects submitted').should('be.visible');
        });
      });
    });
  });

  it('TB-376 Restricting management of project specific privileges in system roles has priority over project specific privileges in the application', () => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
    cy.loginByRequest(user.applicantUser.email);
    cy.createApplication(application).then(applicationId => {
      cy.visit(`/app/project/detail/${applicationId}/`,{failOnStatusCode: false});
      cy.contains('Project privileges').should('exist');
      cy.visit(`/app/project/detail/${applicationId}/privileges`,{failOnStatusCode: false});
      cy.contains('Application Form users').should('exist');
      cy.loginByRequest(user.admin.email);
      cy.visit('/');
      cy.contains('System').click();
      cy.contains('applicant user').click();
      cy.contains('mat-tree-node','Project privileges').contains('button','hide').click();
      cy.contains('Save changes').click();
      cy.loginByRequest(user.applicantUser.email);
      cy.visit(`/app/project/detail/${applicationId}/`,{failOnStatusCode: false});
      cy.contains('Project privileges').should('not.exist');
      cy.visit(`/app/project/detail/${applicationId}/privileges`,{failOnStatusCode: false});
      cy.contains('Application Form users').should('not.exist');
      cy.loginByRequest(user.admin.email);
      cy.visit('/');
      cy.contains('System').click();
      cy.contains('applicant user').click();
      cy.contains('mat-tree-node','Project privileges').contains('button','edit').click();
      cy.contains('Save changes').click();
    });
  });

  function testEditPrivileges(applicationId) {
    cy.visit('/');
    cy.get('#table:first').contains('div', applicationId).should('be.visible');
    cy.visit(`/app/project/detail/${applicationId}/applicationFormIdentification`, {failOnStatusCode: false});
    cy.get("textarea:first").should('not.have.attr', 'readonly');
    cy.visit(`/app/project/detail/${applicationId}/export`, {failOnStatusCode: false});
    cy.contains('button', 'Export').should('be.visible');
    cy.wait(100);
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
    cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
    cy.wait(100);
    cy.contains('div', 'Project privileges').click();
    cy.contains('button', '+').click();
    cy.get('input:last').type(newUser.email);
    cy.get('mat-button-toggle-group:last').contains('span', privilegeLevel).click();
    cy.contains('button', 'Save changes').click();
  }
});
