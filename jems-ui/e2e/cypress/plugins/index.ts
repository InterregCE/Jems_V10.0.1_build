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
import { parse as parseCSV } from 'csv-parse';
import { finished } from 'stream/promises';

/**
 * @type {Cypress.PluginConfig}
 */
export default (on, config) => {
  // `on` is used to hook into various events Cypress emits
  // `config` is the resolved Cypress config
  on('task', {
    parsePDF(subject) {
      return pdf(subject).catch(function(error) {
        console.log(error);
      });
    },
  });

  on('task', {
    async parseCSV(subject) {
      let result = [];
      let some = parseCSV(subject, {relax_column_count: true},function(error, records) {
        if (error) console.log(error);
        result = records;
      });

      await finished(some);
      return result;
    },
  });
}
