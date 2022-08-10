import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import application2step from '../../../../fixtures/api/application/2.step.application.json';
import call2stepNoCheck from '../../../../fixtures/api/call/2.step.no-check.call.json';

context('Application modification tests', () => {

  before(() => {
    cy.loginByRequest(user.programmeUser.email);
    cy.createCall(call).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId);
    });
  });

  it('TB-359 Open new modification and approve it', () => {
    cy.fixture('project/application-form/modifications/TB-359.json').then(testData => {
      cy.loginByRequest(user.applicantUser.email);
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false});
        cy.contains('Open new modification').click();
        cy.get('jems-confirm-dialog').should('be.visible');
        cy.get('jems-confirm-dialog').find('.mat-dialog-actions').contains('Confirm').click();
        cy.contains('You have successfully opened a modification').should('be.visible');

        cy.loginByRequest(user.applicantUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.get('jems-project-application-information').find('div').should('contain.text', 'In modification precontracted');
        cy.get('jems-side-nav').find('mat-select-trigger').find('span').should('contain.text', ' (current) V. 2.0');
        cy.runPreSubmissionCheck(applicationId);
        cy.submitProjectApplication(applicationId);

        cy.loginByRequest(user.programmeUser.email);
        cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
        cy.get('jems-project-application-information').find('div').should('contain.text', 'Modification precontracted submitted');
        cy.contains('span', 'Modification').click();
        cy.contains('Approve modification').click();
        cy.contains('mat-form-field', 'Decision date').find('button').click();
        cy.get('.mat-calendar-body-today').click();
        cy.contains('mat-form-field', 'Entry into force').find('button').click();
        cy.get('.mat-calendar-body-today').click();
        cy.get('jems-modification-confirmation').contains('div', 'Explanatory notes').find('textarea').type(testData.approved.note);
        cy.get('jems-modification-confirmation').contains('Save changes').click();

        cy.wait(1000); // TODO remove after MP2-2391 is fixed

        cy.contains('Past modifications').next().should(pastModificationsSection => {
          expect(pastModificationsSection).to.contain('Approved');
          expect(pastModificationsSection).to.contain('Modification 1');
          expect(pastModificationsSection).to.contain('Project Version 2.0');
        });

        cy.contains('Project overview').click();
        cy.get('jems-project-application-information').find('div').should('contain.text', 'Approved');
      });
    });
  });

  it('TB-357 Reject modification', () => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.startModification(applicationId, user.programmeUser.email);
      cy.runPreSubmissionCheck(applicationId);
      cy.submitProjectApplication(applicationId);

      cy.loginByRequest(user.programmeUser.email);
      cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false});

      cy.contains('Reject modification').click();
      cy.contains('mat-form-field', 'Decision date').find('button').click();
      cy.get('.mat-calendar-body-today').click();
      cy.contains('mat-form-field', 'Entry into force').find('button').click();
      cy.get('.mat-calendar-body-today').click();
      cy.contains('div', 'Explanatory notes').find('textarea').type('Random note text');

      cy.contains('Save changes').click();

      cy.wait(1000); // TODO remove after MP2-2391 is fixed

      cy.contains('Past modifications').next().should(pastModificationsSection => {
        expect(pastModificationsSection).to.contain('Rejected');
        expect(pastModificationsSection).to.contain('Modification 1');
        expect(pastModificationsSection).to.contain('Project Version 2.0');
      });

      cy.contains('div', 'Project version').should('contain', '(current) V. 1.0');
    });
  });

  it('TB-356 Edit project in modification and resubmit', () => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
      cy.startModification(applicationId, user.programmeUser.email);

      cy.loginByRequest(user.applicantUser.email);
      cy.visit(`app/project/detail/${applicationId}/applicationFormIdentification`, {failOnStatusCode: false});
      cy.contains('div', 'Project title').find('textarea').clear().type('New title');
      cy.contains('Save changes').click();
      cy.contains('Project identification saved successfully.').should('be.visible');

      cy.runPreSubmissionCheck(applicationId);
      cy.submitProjectApplication(applicationId);
      cy.reload();

      cy.contains('Project overview').click();
      cy.get('jems-project-application-information').find('div').should('contain.text', 'Modification precontracted submitted');

      cy.contains('.link', 'A - Project identification').click();
      cy.wait(1000); // TODO remove after MP2-2391 is fixed
      cy.get('textarea').should('have.value', 'New title');

      cy.contains('(current) V. 2.0').click();
      cy.wait(1000); // TODO remove after MP2-2391 is fixed
      cy.contains('V. 1.0').should('be.visible').click();
      cy.contains('You are currently viewing an old version of this application').should('be.visible');
      cy.get('textarea').should('have.value', 'API generated application title DE');
    });
  });
  it('TB-358 Deactivate Partner', () => {
    cy.fixture('project/application-form/modifications/TB-358.json').then(testData => {
      //pre-condition: create a new application based on a two step call
      cy.loginByRequest(user.programmeUser.email);
      cy.create2StepCall(call2stepNoCheck, user.programmeUser.email).then(callId => {
        cy.publishCall(callId, user.programmeUser.email);
        cy.loginByRequest(user.applicantUser.email);
        application2step.details.projectCallId = callId;
        cy.createApplication(application2step).then(applicationId => {
          //Action: Create at least two partners, one of them must be a "Lead Partner"
          cy.visit('/', {failOnStatusCode: false});
          cy.wait(1000); //If I remove this, it fails in 17 out of 100 cases
          cy.get('jems-table').get('mat-row').first().get('mat-cell').first().click();
          cy.contains('Partners overview').click({force:true});
          cy.contains('Add new partner').click();
          cy.contains('Lead partner').click();
          cy.contains('div', 'Abbreviated name of the organisation').find('input').type(testData.partner1.abbreviation);
          cy.contains('div', 'Legal status').find('mat-select').click();
          cy.get('mat-option:first-of-type').click();
          cy.contains('button', 'Create').click();
          cy.wait(100); //If I remove this, it fails in 1 out of 100 cases
          cy.contains('Partners overview').click({force:true});
          cy.contains('Add new partner').click();
          cy.contains('button', 'Partner').click();
          cy.contains('div', 'Abbreviated name of the organisation').find('input').type(testData.partner2.abbreviation);
          cy.contains('div', 'Legal status').find('mat-select').click();
          cy.get('mat-option:first-of-type').click();
          cy.contains('button', 'Create').click();
          cy.wait(1000); //If I remove this, it fails in 32 out of 100 cases
          //Result: Partners are created and visible in the overview page
          cy.contains('Partners overview').click();
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(3)').should('have.text', ' ' + testData.partner1.abbreviation + ' ');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').find('mat-cell:nth-of-type(3)').should('have.text', ' ' + testData.partner2.abbreviation + ' ');
          //Result: Partner Status is Active
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').find('mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          //Result: Delete Button is visible
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell','delete').should('be.visible');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell','delete').should('be.visible');
          //Result: Deactivate Button is not available
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell', 'Deactivate partner').should('not.exist');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell', 'Deactivate partner').should('not.exist');
          //Result: Partners are visible in the side bar
          cy.contains('li', testData.partner1.abbreviation)
          cy.contains('li', testData.partner2.abbreviation)
          //Action: Submit application
          cy.contains('Check & Submit').click();
          cy.contains('Run pre-submission check').click();
          cy.wait(1000);
          cy.contains('Submit project application').click();
          cy.contains('Confirm').click();
          //Result: Status is changed to Step 1 Submitted
          cy.contains('mat-chip', ' Step 1 Submitted ');
          //Action: Login as programme user
          cy.loginByRequest(user.programmeUser.email);
          //Result: The newly submitted application is available in the application list
          cy.visit('/', {failOnStatusCode: false});
          cy.contains('li', 'Applications').click();
          cy.wait(100);
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(1)').should('have.text', ' ' + applicationId.toString().padStart(5, '0') + ' ');
          //Action: Approve the submitted application, part 1
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(1)').click();
          cy.contains('Assessment & Decision').click();
          cy.contains('Enter eligibility assessment').click();
          cy.contains('Project has passed eligibility assessment.').click();
          cy.contains('Submit eligibility assessment').click();
          cy.contains('Confirm').click();
          cy.contains('Enter quality assessment').click();
          cy.contains('Project is recommended for funding.').click();
          cy.contains('Submit quality assessment').click();
          cy.contains('Confirm').click();
          cy.contains('Enter eligibility decision').click();
          cy.contains('Project is eligible').click();
          cy.get('mat-datepicker-toggle').click();
          cy.get('mat-calendar').type('Cypress.io{enter}');
          cy.contains('Submit eligibility decision').click();
          cy.contains('Confirm').click();
          cy.wait(1000);
          //Result: Application Status is changed to Step 1 Eligible
          cy.contains('Project overview').click({force: true});
          cy.wait(100);
          cy.contains('mat-chip', ' Step 1 Eligible ');
          cy.wait(100);
          //Action: Approve the submitted appication, part 2
          cy.contains('Assessment & Decision').click({force:true});
          cy.contains('Enter funding decision').click();
          cy.contains('Project is approved').click();
          cy.get('mat-datepicker-toggle').click();
          cy.wait(100);
          cy.get('mat-calendar').type('Cypress.io{enter}');
          cy.contains('Submit funding decision').click();
          cy.contains('Confirm').click();
          cy.wait(1000);
          //Result: Application Status is changed to Step 1 Approved
          cy.contains('Project overview').click({force: true});
          cy.wait(100);
          cy.contains('mat-chip', ' Step 1 Approved ');
          cy.wait(100);
          //Result: Partner status is active
          cy.contains('Partners overview').click({force:true});
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').find('mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          //Result: Delete button is not available
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell','delete').should('not.exist');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell','delete').should('not.exist');
          //Result: Deactivate button is not available
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell', 'Deactivate partner').should('not.exist');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell', 'Deactivate partner').should('not.exist');
          cy.wait(100);
          //Action: Start Step 2
          cy.contains('Assessment & Decision').click();
          cy.contains('Start step two').click();
          //Result: Project Status is changed to Draft
          cy.contains('Project overview').click();
          cy.wait(100);
          cy.contains('mat-chip', ' Draft ');
          cy.wait(100);
          //Result: Partner Status is active
          cy.contains('Partners overview').click();
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').find('mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          //Action: Login as applicant user, navigate to Partners overview
          cy.loginByRequest(user.applicantUser.email);
          cy.visit('/', {failOnStatusCode: false});
          cy.get('jems-table').eq(0).find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(1)').should('have.text', ' ' + applicationId.toString().padStart(5, '0') + ' ');
          cy.get('jems-table').eq(0).find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(1)').click();
          cy.wait(100);
          cy.contains('Partners overview').click({force: true});
          //Result: Delete button is visible
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell','delete').should('be.visible');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell','delete').should('be.visible');
          //Result: Deactivate button is not available
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell', 'Deactivate partner').should('not.exist');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell', 'Deactivate partner').should('not.exist');
          //Action: Submit the application
          cy.contains('Check & Submit').click();
          cy.contains('Run pre-submission check').click();
          cy.wait(100);
          cy.contains('Submit project application').click({force:true});
          cy.contains('Confirm').click();
          //Result: Application Status is changed to "Submitted"
          cy.contains('mat-chip', ' Submitted ');
          //Action: Login as programme user
          cy.loginByRequest(user.programmeUser.email);
          //Result: The newly submitted application is available in the application list
          cy.visit('/', {failOnStatusCode: false});
          cy.contains('li', 'Applications').click();
          cy.wait(1000);
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(1)').should('have.text', ' ' + applicationId.toString().padStart(5, '0') + ' ');
          //Action: Approve the submitted application, part 1
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(1)').click({force:true});
          cy.wait(100);
          cy.contains('Assessment & Decision').click({force: true});
          cy.contains('Enter eligibility assessment').click();
          cy.contains('Project has passed eligibility assessment.').click();
          cy.contains('Submit eligibility assessment').click();
          cy.contains('Confirm').click();
          cy.contains('Enter quality assessment').click();
          cy.contains('Project is recommended for funding.').click();
          cy.contains('Submit quality assessment').click();
          cy.contains('Confirm').click();
          cy.contains('Enter eligibility decision').click();
          cy.contains('Project is eligible').click();
          cy.get('mat-datepicker-toggle').click();
          cy.get('mat-calendar').type('Cypress.io{enter}');
          cy.contains('Submit eligibility decision').click();
          cy.contains('Confirm').click();
          cy.wait(1000);
          //Result: Status is changed to "Eligible"
          cy.contains('Project overview').click({force: true});
          cy.wait(100);
          cy.contains('mat-chip', ' Eligible ');
          cy.wait(100);
          //Action: Approve the submitted application, part 2
          cy.contains('Assessment & Decision').click();
          cy.contains('Enter funding decision').click();
          cy.contains('Project is approved').click();
          cy.get('mat-datepicker-toggle').click();
          cy.get('mat-calendar').type('Cypress.io{enter}');
          cy.contains('Submit funding decision').click();
          cy.contains('Confirm').click();
          cy.wait(1000);
          //Result: Status is changed to "Approved"
          cy.contains('Project overview').click({force: true});
          cy.wait(100);
          cy.contains('mat-chip', ' Approved ');
          cy.wait(100)
          //Result: Delete partner button is not available
          cy.contains('Partners overview').click({force:true});
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell', 'delete').should('not.exist');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell', 'delete').should('not.exist');
          //Result: Deactivate partner button is not available
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell', 'Deactivate partner').should('not.exist');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell', 'Deactivate partner').should('not.exist');
          //Action: Open a new modification
          cy.contains('Modification').click();
          cy.contains('Open new modification').click();
          cy.contains('Confirm').click();
          cy.wait(1000);
          //Result: Status is changed to "In modification precontracted"
          cy.contains('Project overview').click({force: true});
          cy.wait(100);
          cy.contains('mat-chip', ' In modification precontracted ');
          //Action: Login as applicant user, navigate to partner overview page
          cy.loginByRequest(user.applicantUser.email);
          cy.visit('/', {failOnStatusCode: false});
          cy.get('jems-table').eq(0).find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(1)').should('have.text', ' ' + applicationId.toString().padStart(5, '0') + ' ');
          cy.get('jems-table').eq(0).find('tbody').find('mat-row:nth-of-type(1)').find('mat-cell:nth-of-type(1)').click();
          cy.wait(100);
          //Result: Delete partner button is not available
          cy.contains('Partners overview').click({force: true});
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell', 'delete').should('not.exist');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell', 'delete').should('not.exist');
          //Result: Deactivate partner button is visible
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(1)').contains('mat-cell', 'Deactivate partner').should('be.visible');
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell', 'Deactivate partner').should('be.visible');
          //Action: Deactivate Partner 2
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').contains('mat-cell', 'Deactivate partner').click();
          //Result: Confirm deactivation popup is displayed
          cy.contains('button', 'Confirm').click();
          //Result: Green confirmation message is displayed
          cy.contains('div', `Partner "${testData.partner2.abbreviation}" deactivated successfully`).should('have.css', 'background-color', 'rgb(212, 237, 218)');
          cy.wait(100);
          //Result: Deactivated partner stays in the partners overview list, with status inactive
          cy.get('jems-table').find('tbody').find('mat-row:nth-of-type(2)').find('mat-cell:nth-of-type(2)').should('have.text', 'person_offInactive')
          //Result: Deactivated partner is marked in the sidenav (person_off icon)
          cy.contains('li', testData.partner2.abbreviation).contains('person_off');
          //Action: Navigate to the partner detail page of the deactivate partner
          cy.contains('li', testData.partner2.abbreviation).click({force:true});
          //Result: A warning message is visible for each tab in the specific partner section: “You are currently viewing an deactivated partner.“
          cy.contains('You are currently viewing a deactivated partner.');
          cy.wait(100);
          cy.contains('a', 'Address').click({force: true});
          cy.contains('You are currently viewing a deactivated partner.');
          cy.wait(100);
          cy.contains('a', 'Contact').click({force: true});
          cy.contains('You are currently viewing a deactivated partner.');
          cy.wait(100);
          cy.contains('a', 'Motivation').click({force: true});
          cy.contains('You are currently viewing a deactivated partner.');
          cy.wait(100);
          cy.contains('a', 'Budget').click({force: true});
          cy.contains('You are currently viewing a deactivated partner.');
          cy.wait(100);
          cy.contains('a', 'Co-financing').click({force: true});
          cy.contains('You are currently viewing a deactivated partner.');
          cy.wait(100);
          cy.contains('a', 'State Aid').click({force: true});
          cy.contains('You are currently viewing a deactivated partner.');
          cy.wait(100);
          //Action: Change the application version to an older version
          cy.contains('div', 'Project version').find('mat-select').click();
          cy.get('mat-optgroup:nth-of-type(2)').find('mat-option').click();
          cy.wait(100);
          //Result:  The warning message “You are currently viewing a deactivated partner.“ is not visible
          cy.contains('You are currently viewing a deactivated partner.').should('not.exist');
        });
      });
    });
  });
});
