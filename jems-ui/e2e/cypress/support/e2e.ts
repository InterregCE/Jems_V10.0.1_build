import './commands';
import './login.commands'
import './call.commands';
import './application.commands';
import './programme.commands';
import './system.commands';
import './partner-report.commands';
import './controller.commands';

// update test case execution results in Jira
Cypress.on('test:after:run', async function(test) {
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
      await fetch(`https://rtm-api.hexygen.com/api/v2/test-case-execution/${Cypress.env('executionKey')}-${testKey}`, requestDetails);
    }
  }
});
