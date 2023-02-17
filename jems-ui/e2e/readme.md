# E2E Cypress tests

In this directory there are files, which are used to run Cypress continuous integration tests.

### Development setup
If you want to run cypress locally, do the following:
- run `npm i` in this directory, which will download and install Cypress and all its dependencies
- set the following environment variables:
  - `CYPRESS_defaultPassword` to our default password
  (The password can be set by updating the corresponding field in `cypress.config.ts`)
- run `npm run open` to start Cypress TestRunner pr `npm run run` to start it in headless mode
  - if that will fail because `Cypress verification timed out`, run the command again or run `npx cypress verify`

### Environment setup
To run any cypress tests, you have to start from a clean environment and run both `login`
and `programme` tests one time. This will properly set users and programme configurations.
All other tests can be run independently, multiple times and in any order.

You can skip this step and just run tests on Cypress interact environment. To do that set:
- `CYPRESS_BASE_URL` to `https://cypress.interact-eu.net`

Bear in mind that cypress tests here are executed each night and the environment is cleaned each time.
You won't be able to run login or programme tests here, but all the others will work just fine, without any prior setup.

### Jira integration
To check/develop local test reporting to Jira, set the following environment variable:

- `CYPRESS_jiraApiToken` to `eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxNjU5MzU3In0.OxRpcFzbw1-HGeUZJ3Cl22RUtGHwwTKquy8R1Z9s-SgP6tGSiUtv2d2Wbr0PUWXp`

Once you run the tests, a new execution will be created in `Jira Test Board` and test results will be reported there.