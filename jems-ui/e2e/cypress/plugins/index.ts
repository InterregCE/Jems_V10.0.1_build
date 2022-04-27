import pdf from 'pdf-parse';
import {parse as parseCSV} from 'csv-parse';
import {finished} from 'stream/promises';
import date from 'date-and-time';
import fetch from 'node-fetch';
import CypressRunResult = CypressCommandLine.CypressRunResult;

/**
 * @type {Cypress.PluginConfig}
 */
export default async (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions) => {
  on('task', {
    parsePDF(subject) {
      return pdf(subject).catch(function (error) {
        console.log(error);
      });
    },
  });

  on('task', {
    async parseCSV(subject) {
      let result = [];
      let parser = parseCSV(subject, {relax_column_count: true}, function (error, records) {
        if (error) console.log(error);
        result = records;
      });

      await finished(parser);
      return result;
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
}
