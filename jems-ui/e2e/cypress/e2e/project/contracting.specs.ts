import user from '../../fixtures/users.json';
import application from '../../fixtures/api/application/application.json';
import approvalInfo from '../../fixtures/api/application/modification/approval.info.json';
import call from "../../fixtures/api/call/1.step.call.json";
import {faker} from "@faker-js/faker";

context('Application contracting tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-519 A project can be set to contracted and partner report created', () => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
      cy.contains('Add entry into force date').should('be.visible');

      cy.startModification(applicationId);
      cy.reload();
      cy.contains('Add entry into force date').should('be.visible');
      cy.contains('button', 'Set project to contracted').should('be.disabled');
      cy.loginByRequest(user.applicantUser.email);
      cy.submitProjectApplication(applicationId);

      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
      cy.contains('Add entry into force date').should('be.visible');
      cy.contains('button', 'Set project to contracted').should('be.disabled');

      cy.approveModification(applicationId, approvalInfo);
      cy.reload();
      cy.contains('Add entry into force date').should('be.visible');
      cy.contains('button', 'Set project to contracted').should('be.enabled');
      cy.contains('button', 'Set project to contracted').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('You have successfully contracted project').should('be.visible');

      cy.contains('Project overview').click();
      cy.contains('mat-chip', 'Contracted').should('be.visible');
      cy.contains('mat-panel-title', 'Reporting').should('be.visible');

      cy.contains('mat-expansion-panel', 'Partner reports').should('contain', 'Lead Partner').contains('Lead Partner').click();
      cy.contains('button', 'Add Partner Report').click();
      cy.contains('Partner progress report identification').should('be.visible');
    });
  });

  it('TB-735 Contracting sections appear at the right moment and remains editable', () => {
      cy.fixture('project/contracting/TB-735.json').then(testData => {
      
      // create contracting role/user
      cy.loginByRequest(user.admin.email);
      testData.contractingRole.name = `contractingRole_${faker.random.alphaNumeric(5)}`;
      testData.contractingUser.email = faker.internet.email();
      cy.createRole(testData.contractingRole).then(roleId => {
        testData.contractingUser.userRoleId = roleId;
        cy.createUser(testData.contractingUser);
      });
      
      cy.loginByRequest(user.applicantUser.email);
      cy.createSubmittedApplication(application).then(applicationId => {
        cy.loginByRequest(testData.contractingUser.email)
        cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});

        cy.contains('Application form').should('be.visible');
        cy.contains('Contracting').should('not.exist');

        cy.approveApplication(applicationId, application.assessments, user.programmeUser.email);

        cy.reload();
        cy.contains('Contracting').scrollIntoView().should('be.visible');
        cy.contains('Contract monitoring').should('be.visible');
        cy.contains('Project management').should('be.visible');
        cy.contains('Reporting schedule').should('be.visible');
        
        cy.startModification(applicationId, user.programmeUser.email);
        cy.reload();
        
        cy.contains('Contracting').scrollIntoView().should('be.visible');
        cy.contains('Contract monitoring').should('be.visible');
        cy.contains('Project management').should('be.visible');
        cy.contains('Reporting schedule').should('be.visible');
        
        cy.contains('Contract monitoring').click();
        cy.contains('div', 'Comment').find('textarea').should('not.have.attr', 'disabled');

        cy.loginByRequest(user.applicantUser.email);
        cy.runPreSubmissionCheck(applicationId);
        cy.submitProjectApplication(applicationId);

        cy.loginByRequest(testData.contractingUser.email)
        cy.reload();
        cy.contains('Contract monitoring').click({force: true});
        cy.contains('div', 'Comment').find('textarea').should('not.have.attr', 'disabled');
      });
    });
  });

  it('TB-755 Contracting Project management', () => {
    cy.fixture('project/contracting/TB-755.json').then(testData => {
      cy.loginByRequest(user.admin.email);
      testData.applicationUser.email = faker.internet.email();
      cy.createUser(testData.applicationUser);
      testData.anotherUser.email = faker.internet.email();
      cy.createUser(testData.anotherUser);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);
      })
      cy.loginByRequest(testData.applicationUser.email);
      cy.createApprovedApplication(application, user.admin.email).then(applicationId => {
        cy.loginByRequest(testData.applicationUser.email);
        cy.visit(`/app/project/detail/${applicationId}/projectManagement`, {failOnStatusCode: false});
        cy.get('input[name="title"]').should('not.have.attr', 'disabled');
        cy.contains('Project manager').should('be.visible');
        cy.contains('Finance manager').scrollIntoView().should('be.visible');
        cy.contains('Communication manager').scrollIntoView().should('be.visible');
        cy.contains('div.mat-form-field-flex', 'Title').type(faker.word.noun());
        cy.contains('button', 'Save changes').click();

        cy.loginByRequest(user.admin.email);
        cy.visit(`/app/project/detail/${applicationId}/privileges`, {failOnStatusCode: false});

        cy.get('mat-expansion-panel#partner-collaborators-panel').find('input.mat-input-element').type(testData.anotherUser.email);
        cy.contains('button', 'Save changes').click();

        cy.loginByRequest(testData.anotherUser.email);
        cy.visit(`/app/project/detail/${applicationId}/projectManagement`, {failOnStatusCode: false});
        cy.get('input[name="title"]').should('have.attr', 'disabled');

        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.wait(2000);
        cy.contains('mat-expansion-panel', 'Contracting').contains('Project Monitoring').should('not.exist');
      });
    });
  });
});
