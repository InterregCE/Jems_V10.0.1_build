// ***********************************************************
// This example support/index.ts is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.ts using ES2015 syntax:
import './commands';
import './login.commands'
import './call.commands';
import './application.commands';

// Alternatively you can use CommonJS syntax:
// require('./commands')

// update test case execution results in Jira
Cypress.on('test:after:run', function (test) {
  if (Cypress.env('executionKey')) {
    const match = /TB-\d+/.exec(test.title);
    if (match) {
      const testKey = match[0];
      const testCaseExecutionDetails = {
        "result": {
          "statusName": "Blocked"
        }
      }
      if (test.state === 'failed')
        testCaseExecutionDetails.result.statusName = "Fail";
      else if (test.state === 'passed')
        testCaseExecutionDetails.result.statusName = "Pass";

      const requestDetails = {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${Cypress.env('jiraApiToken')}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(testCaseExecutionDetails)
      };
      fetch(`https://rtm-api.hexygen.com/api/v2/test-case-execution/${Cypress.env('executionKey')}-${testKey}`, requestDetails);
    }
  }
});
