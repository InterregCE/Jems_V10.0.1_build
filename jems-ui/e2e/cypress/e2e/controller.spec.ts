import user from '../fixtures/users.json';
import {faker} from '@faker-js/faker';
import call from '../fixtures/api/call/1.step.call.json';
import application from '../fixtures/api/application/application.json';
import controllerCreatorRole from '../fixtures/api/roles/controllerCreatorRole.json';
import controllerCreatorUser from '../fixtures/api/users/controllerCreatorUser.json';

context('Controller tests', () => {


  before(() => {
    cy.loginByRequest(user.admin.email);
    cy.createRole(controllerCreatorRole).then(roleId => {
      controllerCreatorUser.userRoleId = roleId;
      controllerCreatorUser.email = faker.internet.email();
      cy.createUser(controllerCreatorUser);
    });

    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call, user.programmeUser.email).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
      cy.loginByRequest(user.applicantUser.email);
      cy.createApprovedApplication(application, user.programmeUser.email);
    });
  });

  it('TB-810 Controller institutions can be created', function () {
    cy.fixture('controller/TB-810.json').then(testData => {

      cy.loginByRequest(user.admin.email);
      testData.controllerUser1.email = faker.internet.email();
      testData.controllerUser2.email = faker.internet.email();
      cy.createUser(testData.controllerUser1);
      cy.createUser(testData.controllerUser2);
      testData.institution.users[0].email = testData.controllerUser1.email;
      testData.institution.users[1].email = testData.controllerUser2.email;

      cy.loginByRequest(controllerCreatorUser.email);
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

  it('TB-935 Controller institutions can be assigned to project partner', function () {
    cy.fixture('controller/TB-935.json').then(testData => {

      testData.institution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
      cy.loginByRequest(controllerCreatorUser.email);

      cy.createInstitution(testData.institution).then(institutionId => {

        cy.loginByRequest(controllerCreatorUser.email);
        cy.visit('/');
        cy.contains('Controllers').click();
        cy.contains('Assignment').click();
        cy.get('table mat-row').then(_ => {
          cy.contains('mat-header-cell', 'ProjectID').click().click();
          cy.get(`mat-row:contains(${this.applicationId})`).filter(':contains("Project Partner")').contains('Select Institution').click();
          cy.contains('mat-option', testData.institution.name).click();
          cy.contains('Save changes').click();

          cy.contains('Controller institutions assignments have been successfully saved.').should('be.visible');
          cy.contains('Controller institutions assignments have been successfully saved.').should('not.exist');
          cy.get(`mat-row:contains(${this.applicationId})`).filter(':contains("Lead Partner")').contains('Select Institution').click();
          cy.contains('mat-option', testData.institution.name).should('not.exist');

          cy.loginByRequest(user.applicantUser.email);
          cy.visit(`/app/project/detail/${this.applicationId}/privileges`, {failOnStatusCode: false});
          cy.contains('mat-panel-title', 'Lead Partner').parent().contains('No control institution assigned').should('be.visible');
          cy.contains('mat-panel-title', 'Project Partner').parent().scrollIntoView().contains(`${testData.institution.name}`).should('be.visible');

          cy.loginByRequest(controllerCreatorUser.email);
          cy.visit(`/app/controller/${institutionId}`, {failOnStatusCode: false});
          cy.get('input').eq(0).clear().type('Updated institution name');
          cy.contains('NUTS explorer').should('be.visible');
          cy.contains('Save changes').click();
          cy.contains('Controller institution was updated successfully').should('be.visible');

          cy.loginByRequest(user.applicantUser.email);
          cy.visit(`/app/project/detail/${this.applicationId}/privileges`, {failOnStatusCode: false});
          cy.contains('mat-panel-title', 'Project Partner').parent().scrollIntoView().contains('Updated institution name').should('be.visible');
        });
      });
    });
  });
});
