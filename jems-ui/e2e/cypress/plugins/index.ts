/// <reference types="cypress" />
// ***********************************************************
// This example plugins/index.ts can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************

// This function is called when a project is opened or re-opened (e.g. due to
// the project's config changing)
import pdf from 'pdf-parse';
import {parse as parseCSV} from 'csv-parse';
import {finished} from 'stream/promises';
import date from 'date-and-time';
import fetch from 'node-fetch';

/**
 * @type {Cypress.PluginConfig}
 */
export default async (on, config) => {
  // `on` is used to hook into various events Cypress emits
  // `config` is the resolved Cypress config
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
      let some = parseCSV(subject, {relax_column_count: true}, function (error, records) {
        if (error) console.log(error);
        result = records;
      });

      await finished(some);
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
    }).then(response => {
      config.env.executionKey = response.testKey;
    });
  }

  on('after:run', async (results) => {
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
