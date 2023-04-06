import user from '../../fixtures/users.json';
import application from '../../fixtures/api/application/application.json';
import approvalInfo from '../../fixtures/api/application/modification/approval.info.json';
import call from '../../fixtures/api/call/1.step.call.json';
import rejectionInfo from '../../fixtures/api/application/modification/rejection.info.json';
import {faker} from '@faker-js/faker';

context('Application contracting tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-519 A project can be set to contracted', () => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
      cy.contains('Add amendment entry into force date').should('be.visible');

      cy.startModification(applicationId);
      cy.reload();
      cy.contains('Add amendment entry into force date').should('be.visible');
      cy.contains('button', 'Set project to contracted').should('be.disabled');
      cy.loginByRequest(user.applicantUser.email);
      cy.submitProjectApplication(applicationId);

      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
      cy.contains('Add amendment entry into force date').should('be.visible');
      cy.contains('button', 'Set project to contracted').should('be.disabled');

      cy.approveModification(applicationId, approvalInfo);
      cy.reload();
      cy.contains('Add amendment entry into force date').should('be.visible');
      cy.contains('button', 'Set project to contracted').should('be.enabled');
      cy.contains('button', 'Set project to contracted').click();
      cy.contains('button', 'Confirm').click();
      cy.contains('You have successfully contracted project').should('be.visible');

      cy.contains('Project overview').click();
      cy.contains('mat-chip', 'Contracted').should('be.visible');
      cy.contains('mat-panel-title', 'Reporting').should('be.visible');

      cy.contains('mat-expansion-panel', 'Partner reports').should('contain', 'Lead Partner').contains('Lead Partner').click();
      cy.contains('No Reports').should('be.visible');
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
        cy.contains('Project managers').should('be.visible');
        cy.contains('Project reporting schedule').should('be.visible');
        
        cy.startModification(applicationId, user.programmeUser.email);
        cy.reload();
        
        cy.contains('Contracting').scrollIntoView().should('be.visible');
        cy.contains('Contract monitoring').should('be.visible');
        cy.contains('Project managers').should('be.visible');
        cy.contains('Project reporting schedule').should('be.visible');
        
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

        cy.contains('jems-partner-team-privileges-expansion-panel', 'LP1 Lead Partner')
          .find('input.mat-input-element').type(testData.anotherUser.email);
        cy.contains('button', 'Save changes').click();

        cy.loginByRequest(testData.anotherUser.email);
        cy.visit(`/app/project/detail/${applicationId}/projectManagement`, {failOnStatusCode: false});
        cy.get('input[name="title"]').should('have.attr', 'disabled');

        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.contains('mat-expansion-panel', 'Contracting').contains('Project Monitoring').should('not.exist');
      });
    });
  });

  it('TB-873 Fast Track Lump Sum can be set as Ready for payment', () => {
    cy.fixture('project/contracting/TB-873.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`/app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.contains('Contract monitoring').click();

        cy.get('.jems-table-config > div').last().within(() => {
          cy.contains(testData.fastTrackLumpSum.name).scrollIntoView().should('be.visible');
          cy.contains(testData.fastTrackLumpSum.amount).should('be.visible');
          cy.contains(testData.fastTrackLumpSum.period).should('be.visible');
          cy.get('textarea').type(testData.fastTrackLumpSum.comment);
          cy.contains('Yes').click();
        });
        cy.contains('Save changes').should('be.visible').click();
        cy.contains('Contract monitoring form saved successfully.').should('be.visible');
        
        cy.startModification(applicationId);
        cy.reload();
        cy.get('.jems-table-config > div').last().find('button').should('be.disabled');
        cy.get('.jems-table-config > div').last().find('textarea').should('be.disabled');
        cy.loginByRequest(user.applicantUser.email);
        cy.visit(`/app/project/detail/${applicationId}/applicationFormLumpSums`, {failOnStatusCode: false});
        cy.contains('button', 'add').should('be.enabled');
        cy.contains('mat-select', testData.fastTrackLumpSum.name).should('have.class', 'mat-select-disabled');
        cy.submitProjectApplication(applicationId);

        cy.loginByRequest(user.programmeUser.email);
        cy.rejectModification(applicationId, rejectionInfo);
        cy.visit(`/app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});
        cy.get('.jems-table-config > div').last().find('button').should('be.enabled');
        cy.get('.jems-table-config > div').last().contains('No').click();
        cy.contains('Save changes').should('be.visible').click();
        cy.contains('Contract monitoring form saved successfully.').should('be.visible');
        cy.startModification(applicationId);
        
        cy.loginByRequest(user.applicantUser.email);
        cy.visit(`/app/project/detail/${applicationId}/applicationFormLumpSums`, {failOnStatusCode: false});
        cy.contains('mat-select', testData.fastTrackLumpSum.name).should('not.have.class', 'mat-select-disabled');
      });
    });
  });

  it('TB-796 Contract monitoring behavior when both LS and FTLS are used in AF', function () {
    cy.fixture('project/contracting/TB-796.json').then(testData => {
      cy.fixture('api/call/1.step.call.json').then(call => {
        cy.fixture('api/application/application.json').then(application => {

          // customize the call
          cy.loginByRequest(user.programmeUser.email);
          cy.createCall(call).then(callId => {
            application.details.projectCallId = callId;
            cy.publishCall(callId);

            application.details.projectCallId = callId;
            application.partners[0].cofinancing.partnerContributions[0].amount = 5698.97;
            application.partners[1].cofinancing.partnerContributions[2].amount = 6107.96;

            // since default application has FTLS in first position, switch them
            const fastTrackLumpSum = application.lumpSums[0];
            application.lumpSums[0] = application.lumpSums[1];
            application.lumpSums[1] = fastTrackLumpSum;
            application.lumpSums.pop();

            cy.loginByRequest(user.applicantUser.email);
            cy.createContractedApplication(application, user.programmeUser.email).then(function (applicationId) {

              cy.loginByRequest(user.programmeUser.email);
              cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});

              cy.contains(testData.fastTrackLumpSumName).scrollIntoView().should('be.visible');
              cy.contains(testData.regularLumpSumName).should('not.exist');

              cy.get('div.jems-table-config').eq(1).children().eq(1).contains('Yes').click();
              cy.contains('Save changes').should('be.visible').click();
              cy.contains('Contract monitoring form saved successfully.').should('be.visible');

              // remove regular lump sum
              cy.startModification(applicationId, user.programmeUser.email);
              cy.loginByRequest(user.applicantUser.email);
              cy.visit(`app/project/detail/${applicationId}/applicationFormLumpSums`, {failOnStatusCode: false});
              cy.contains('mat-row', testData.regularLumpSumName).contains('delete').click();
              cy.contains('Save changes').click();
              cy.contains('Project\'s LumpSums saved successfully').should('be.visible');
              cy.contains('mat-row', testData.fastTrackLumpSumName).contains('delete').should('not.exist');

              application.partners[0].cofinancing.partnerContributions[0].amount = 5192.17;
              application.partners[1].cofinancing.partnerContributions[2].amount = 3974.63;
              cy.updatePartnerCofinancing(this[application.partners[0].details.abbreviation], application.partners[0].cofinancing);
              cy.updatePartnerCofinancing(this[application.partners[1].details.abbreviation], application.partners[1].cofinancing);
              cy.runPreSubmissionCheck(applicationId);
              cy.submitProjectApplication(applicationId);

              cy.loginByRequest(user.programmeUser.email);
              cy.approveModification(applicationId, approvalInfo);

              cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});

              cy.contains(testData.fastTrackLumpSumName).scrollIntoView().should('be.visible');
              cy.contains(testData.regularLumpSumName).should('not.exist');

              cy.contains('div.jems-table-config', testData.fastTrackLumpSumName).children().should('have.length', 2);

              // add regular lump sum back again
              cy.startModification(applicationId);
              cy.loginByRequest(user.applicantUser.email);
              cy.updateLumpSums(applicationId, application.lumpSums);
              cy.then(() => {
                application.partners[0].cofinancing.partnerContributions[0].amount = 5698.97;
                application.partners[1].cofinancing.partnerContributions[2].amount = 6107.96;
                cy.updatePartnerCofinancing(this[application.partners[0].details.abbreviation], application.partners[0].cofinancing);
                cy.updatePartnerCofinancing(this[application.partners[1].details.abbreviation], application.partners[1].cofinancing);
              });
              cy.runPreSubmissionCheck(applicationId);
              cy.submitProjectApplication(applicationId);

              cy.loginByRequest(user.programmeUser.email);
              cy.approveModification(applicationId, approvalInfo);

              cy.visit(`app/project/detail/${applicationId}/contractMonitoring`, {failOnStatusCode: false});

              cy.contains(testData.fastTrackLumpSumName).scrollIntoView().should('be.visible');
              cy.contains(testData.regularLumpSumName).should('not.exist');

              cy.contains('div.jems-table-config', testData.fastTrackLumpSumName).children().should('have.length', 2);
            });
          });
        });
      });
    });
  });
});
