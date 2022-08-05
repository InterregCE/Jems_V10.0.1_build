import {defineConfig} from 'cypress';
import pdf from 'pdf-parse';
import date from 'date-and-time';
import xlsx from 'node-xlsx';
import fetch from 'node-fetch';
import CypressRunResult = CypressCommandLine.CypressRunResult;

export default defineConfig({
  defaultCommandTimeout: 8000,
  projectId: 'JEMS',
  viewportHeight: 720,
  viewportWidth: 1280,
  watchForFileChanges: false,
  chromeWebSecurity: false,
  retries: {
    runMode: 1,
    openMode: 0,
  },
  env: {
    authenticationUrl: '/api/auth/login',
    defaultPassword: '<change-me>',
  },
  e2e: {
    async setupNodeEvents(on, config) {
      on('task', {
        parsePDF(subject) {
          return pdf(subject);
        },
      });

      on('task', {
        async parseXLSX(subject) {
          return xlsx.parse(Buffer.from(subject.data));
        },
      });

      // setup reporting to JIRA (only in run mode)
      const requestDetails = {
        method: null,
        headers: {
          'Authorization': `Bearer ${config.env.jiraApiToken}`,
          'Content-Type': 'application/json'
        },
        body: null
      };

      if (config.env.jiraApiToken) {
        const apiUrl = 'https://rtm-api.hexygen.com/api/v2/test-execution/execute/TB-400';

        const today = new Date();
        const formattedToday = date.format(today, 'YYYY-MM-DD');

        const executionDetails = {
          parentTestKey: 'F-TB-TE-6',
          startDate: formattedToday,
          endDate: formattedToday
        };

        requestDetails.method = 'post';
        requestDetails.body = JSON.stringify(executionDetails);

        await fetch(apiUrl, requestDetails).then(response => {
          if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
          }

          return response.json();
        }).then((response: any) => {
          config.env.executionKey = response.testKey;
        });
      }

      on('after:run', async (results: CypressRunResult) => {
        if (config.env.jiraApiToken) {
          const executionDetails = {
            "result": {
              "name": "Fail"
            }
          }

          if (results.totalFailed === 0) {
            executionDetails.result.name = 'Pass'
          }

          requestDetails.method = 'put';
          requestDetails.body = JSON.stringify(executionDetails);

          await fetch(`https://rtm-api.hexygen.com/api/v2/test-execution/${config.env.executionKey}`, requestDetails);
        }
      })

      console.log('JIRA executionKey set to: ' + config.env.executionKey);
      return config;
    },
    baseUrl: 'http://localhost:4200',
    specPattern: ['cypress/e2e/login/**', 'cypress/e2e/programme/**', 'cypress/e2e/call/**', 'cypress/e2e/project/**']
  },
})
