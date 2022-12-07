import './commands';
import './login.commands'
import './call.commands';
import './application.commands';
import './programme.commands';
import './system.commands';
import './partner-report.commands';
import './controller.commands';

// update test case execution results in Jira
afterEach(function () {
  if (Cypress.env('executionKey')) {
    cy.task('sendTestResults', {state: this.currentTest.state, title: this.currentTest.title});
  }
});
