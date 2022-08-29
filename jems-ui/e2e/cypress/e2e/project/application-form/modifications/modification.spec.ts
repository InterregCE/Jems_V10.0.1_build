import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import call2step from '../../../../fixtures/api/call/2.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import application2step from '../../../../fixtures/api/application/2.step.application.json';

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

        cy.wait(1000);

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
      cy.wait(1000);
      cy.get('textarea').should('have.value', 'New title');

      cy.contains('(current) V. 2.0').click();
      cy.wait(1000);
      cy.contains('V. 1.0').should('be.visible').click();
      cy.contains('You are currently viewing an old version of this application').should('be.visible');
      cy.get('textarea').should('have.value', 'API generated application title DE');
    });
  });
  it('TB-358 Deactivate Partner', () => {
    cy.fixture('project/application-form/modifications/TB-358.json').then(testData => {
      //pre-condition: create a new application based on a two step call
      cy.loginByRequest(user.programmeUser.email);
      call2step.preSubmissionCheckSettings.pluginKey = "jems-pre-condition-check-off";
      cy.create2StepCall(call2step, user.programmeUser.email).then(callId => {
        cy.publishCall(callId, user.programmeUser.email);
        cy.loginByRequest(user.applicantUser.email);
        application2step.details.projectCallId = callId;
        cy.createApplication(application2step).then(applicationId => {
          //Action: Create at least two partners, one of them must be a "Lead Partner"
          cy.visit('/', {failOnStatusCode: false});
          cy.get('jems-project-application-list mat-row:nth-of-type(1) mat-cell:nth-of-type(1)').should('contain.text', applicationId.toString().padStart(5, '0')).click();
          cy.contains('Partners overview').click();
          cy.contains('Add new partner').click();
          cy.contains('Lead partner').click();
          cy.contains('div', 'Abbreviated name of the organisation').find('input').type(testData.partner1.abbreviation);
          cy.contains('div', 'Legal status').find('mat-select').click();
          cy.get('mat-option:first-of-type').click();
          cy.contains('button', 'Create').click();
          cy.contains('Partners overview').click({force:true});
          cy.contains('Add new partner').click();
          cy.contains('button', 'Partner').click();
          cy.contains('div', 'Abbreviated name of the organisation').find('input').type(testData.partner2.abbreviation);
          cy.contains('div', 'Legal status').find('mat-select').click();
          cy.get('mat-option:first-of-type').click();
          cy.contains('button', 'Create').click();
          //Result: Partners are visible in the side bar
          cy.contains('li', testData.partner1.abbreviation).should('be.visible');
          cy.contains('li', testData.partner2.abbreviation).should('be.visible');
          //Result: Partners are created and visible in the overview page
          cy.contains('Partners overview').click({force:true});
          cy.get('jems-table tbody mat-row:nth-of-type(1) mat-cell:nth-of-type(3)').should('contain.text', testData.partner1.abbreviation);
          cy.get('jems-table tbody mat-row:nth-of-type(2) mat-cell:nth-of-type(3)').should('contain.text', testData.partner2.abbreviation);
          //Result: Partner Status is Active
          cy.get('jems-table tbody mat-row:nth-of-type(1) mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          cy.get('jems-table tbody mat-row:nth-of-type(2) mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          //Result: Delete Button is visible
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell','delete').should('be.visible');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell','delete').should('be.visible');
          //Result: Deactivate Button is not available
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell','Deactivate partner').should('not.exist');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell','Deactivate partner').should('not.exist');
          //Action: Submit application
          cy.contains('Check & Submit').click();
          cy.contains('Run pre-submission check').click();
          cy.contains('Submit project application').click();
          cy.contains('Confirm').click();
          //Result: Status is changed to Step 1 Submitted
          cy.contains('mat-chip', ' Step 1 Submitted ').should('be.visible');
          //Action: Login as programme user
          cy.loginByRequest(user.programmeUser.email);
          //Result: The newly submitted application is available in the application list
          cy.visit('/', {failOnStatusCode: false});
          cy.contains('li', 'Applications').click();
          cy.get('jems-table tbody mat-row:nth-of-type(1) mat-cell:nth-of-type(1)').should('contain.text', applicationId.toString().padStart(5, '0')).click();
          //Action: Approve the submitted application, part 1
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
          cy.intercept('PUT','api/project/*/set-as-eligible').as('setEligible');
          cy.contains('Confirm').click();
          cy.wait('@setEligible');
          //Result: Application Status is changed to Step 1 Eligible
          cy.contains('Project overview').click({force:true});
          cy.contains('mat-chip', ' Step 1 Eligible ').should('be.visible');
          //Action: Approve the submitted appication, part 2
          cy.contains('Assessment & Decision').click({force:true});
          cy.contains('Enter funding decision').click();
          cy.contains('Project is approved').click();
          cy.get('mat-datepicker-toggle').click();
          cy.get('mat-calendar').type('Cypress.io{enter}');
          cy.contains('Submit funding decision').click();
          cy.intercept('PUT','api/project/*/approve').as('approve');
          cy.contains('Confirm').click();
          cy.wait('@approve');
          //Result: Application Status is changed to Step 1 Approved
          cy.contains('Project overview').click({force:true});
          cy.contains('mat-chip', ' Step 1 Approved ').should('be.visible');
          //Result: Partner status is active
          cy.contains('Partners overview').click({force:true});
          cy.get('jems-table tbody mat-row:nth-of-type(1) mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          cy.get('jems-table tbody mat-row:nth-of-type(2) mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          //Result: Delete button is not available
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell','delete').should('not.exist');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell','delete').should('not.exist');
          //Result: Deactivate button is not available
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell','Deactivate partner').should('not.exist');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell','Deactivate partner').should('not.exist');
          //Action: Start Step 2
          cy.contains('Assessment & Decision').click({force:true});
          cy.contains('Start step two').click();
          //Result: Project Status is changed to Draft
          cy.contains('Project overview').click();
          cy.contains('mat-chip', ' Draft ').should('be.visible');
          //Result: Partner Status is active
          cy.contains('Partners overview').click({force:true});
          cy.get('jems-table tbody mat-row:nth-of-type(1) mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          cy.get('jems-table tbody mat-row:nth-of-type(2) mat-cell:nth-of-type(2)').should('have.text', 'personActive');
          //Action: Login as applicant user, navigate to Partners overview
          cy.loginByRequest(user.applicantUser.email);
          cy.visit('/', {failOnStatusCode: false});
          cy.get('jems-project-application-list tbody mat-row:nth-of-type(1) mat-cell:nth-of-type(1)').should('contain.text', applicationId.toString().padStart(5, '0')).click();
          cy.contains('Partners overview').click({force: true});
          //Result: Delete button is visible
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell','delete').should('be.visible');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell','delete').should('be.visible');
          //Result: Deactivate button is not available
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell','Deactivate partner').should('not.exist');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell','Deactivate partner').should('not.exist');
          //Action: Submit the application
          cy.contains('Check & Submit').click();
          cy.contains('Run pre-submission check').click();
          cy.contains('Submit project application').click({force:true});
          cy.contains('Confirm').click();
          //Result: Application Status is changed to "Submitted"
          cy.contains('mat-chip', ' Submitted ').should('be.visible');
          //Action: Login as programme user
          cy.loginByRequest(user.programmeUser.email);
          //Result: The newly submitted application is available in the application list
          cy.visit('/', {failOnStatusCode: false});
          cy.contains('li', 'Applications').click();
          cy.get('jems-table tbody mat-row:nth-of-type(1) mat-cell:nth-of-type(1)').should('contain.text', applicationId.toString().padStart(5, '0')).click();
          //Action: Approve the submitted application, part 1
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
          cy.intercept('PUT','api/project/*/set-as-eligible').as('setEligible');
          cy.contains('Confirm').click();
          cy.wait('@setEligible');
          //Result: Status is changed to "Eligible"
          cy.contains('Project overview').click({force:true});
          cy.contains('mat-chip', ' Eligible ').should('be.visible');
          //Action: Approve the submitted application, part 2
          cy.contains('Assessment & Decision').click({force:true});
          cy.contains('Enter funding decision').click();
          cy.contains('Project is approved').click();
          cy.get('mat-datepicker-toggle').click();
          cy.get('mat-calendar').type('Cypress.io{enter}');
          cy.contains('Submit funding decision').click();
          cy.intercept('PUT','api/project/*/approve').as('approve');
          cy.contains('Confirm').click();
          cy.wait('@approve')
          //Result: Status is changed to "Approved"
          cy.contains('Project overview').click({force:true});
          cy.contains('mat-chip', ' Approved ').should('be.visible');
          //Result: Delete partner button is not available
          cy.contains('Partners overview').click({force:true});
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell', 'delete').should('not.exist');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell', 'delete').should('not.exist');
          //Result: Deactivate partner button is not available
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell', 'Deactivate partner').should('not.exist');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell', 'Deactivate partner').should('not.exist');
          //Action: Open a new modification
          cy.contains('Modification').click({force:true});
          cy.contains('Open new modification').click();
          cy.contains('Confirm').click();
          //Result: Status is changed to "In modification precontracted"
          cy.contains('Project overview').click({force: true});
          cy.contains('mat-chip', ' In modification precontracted ').should('be.visible');
          //Action: Login as applicant user, navigate to partner overview page
          cy.loginByRequest(user.applicantUser.email);
          cy.visit('/', {failOnStatusCode: false});
          cy.get('jems-project-application-list tbody mat-row:nth-of-type(1) mat-cell:nth-of-type(1)').should('contain.text', applicationId.toString().padStart(5, '0')).click();
          //Result: Delete partner button is not available
          cy.contains('Partners overview').click({force: true});
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell', 'delete').should('not.exist');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell', 'delete').should('not.exist');
          //Result: Deactivate partner button is visible
          cy.contains('jems-table tbody mat-row:nth-of-type(1) mat-cell', 'Deactivate partner').should('be.visible');
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell', 'Deactivate partner').should('be.visible');
          //Action: Deactivate Partner 2
          cy.contains('jems-table tbody mat-row:nth-of-type(2) mat-cell', 'Deactivate partner').click();
          //Result: Confirm deactivation popup is displayed
          cy.contains('button', 'Confirm').click();
          //Result: Green confirmation message is displayed
          cy.contains('div', `Partner "${testData.partner2.abbreviation}" deactivated successfully`).should('have.css', 'background-color', 'rgb(212, 237, 218)').and('be.visible');
          //Result: Deactivated partner stays in the partners overview list, with status inactive
          cy.get('jems-table tbody mat-row:nth-of-type(2) mat-cell:nth-of-type(2)').should('have.text', 'person_offInactive')
          //Result: Deactivated partner is marked in the sidenav (person_off icon)
          cy.contains('li', testData.partner2.abbreviation).contains('person_off').should('be.visible');
          //Action: Navigate to the partner detail page of the deactivate partner
          cy.contains('li', testData.partner2.abbreviation).click({force:true});
          //Result: A warning message is visible: “You are currently viewing a deactivated partner.“
          cy.contains('You are currently viewing a deactivated partner.').should('be.visible');
          //Action: Change the application version to an older version
          cy.contains('mat-select', '(current)').click({force:true});
          cy.contains('mat-select', '(current)').click({force:true});
          cy.get('mat-optgroup:nth-of-type(2) mat-option').click();
          //Result:  The warning message “You are currently viewing a deactivated partner.“ is not visible
          cy.contains('You are currently viewing a deactivated partner.').should('not.exist');
        });
      });
    });
  });
});
