# E2E Cypress tests

In this directory there are files, which are used to run Cypress continuous integration tests.

### Development

In most cases you don't need to set up the local environment to develop, troubleshoot or simply run cypress tests.
Just switch to our cypress environment (https://amsterdam.interact-eu.net). In most cases this environment will be preconfigured by
the last nights run, so it will be ready to go.
The only thing to have in mind is that you won't be able to rerun tests from `login.spec.ts` and `programme.spec.ts` as those can be ran only once.

If you want to run cypress from your machine, do the following:
- run `npm i` in this directory, which will download and install Cypress and all its dependencies
- set the following (env or e2e) variables in `cypress.config.ts`:
    - `defaultPassword` to our default password
    - `baseUrl` to remote or local url
- run `npm run open` to start Cypress TestRunner or `npm run run` to start it in headless mode
    - if that will fail because `Cypress verification timed out`, run the command again or run `npx cypress verify`

### Environment

To run any cypress tests on your local Jems environment, you have to start from a clean database and run both `login`
and `programme` tests one time. This will properly set users and programme configurations.
All other tests can be run independently, multiple times and in any order. It's just a thing that they expect a certain programme configuration
that is configured once you run tests from `programme.spec.ts`