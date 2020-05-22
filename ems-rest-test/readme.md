# API integration tests

This module is used for integration testing without FE - testing API.

For this purpose we use [Postman Collections](https://www.postman.com/collection/) stored in
[collections](collections) folder.

### Running through CLI
Postman Collections can be run in cmd with a tool called `newman`. It is a CLI interface for running postman
collections. Inside [environments](environments) folder there are configs for multiple environments.

To execute those tests run
- `npm run test-local` to use [env.local.json](environments/env.local.json) config
- `npm run test-dev` or similar to use other configs, see [package.json](package.json)/scripts
##### Reporting
Running newman in CI/CD pipelines can also extract results for further processing. This feature is part of newman and
is called Reporting, for detail options see [newman-reporters](https://github.com/postmanlabs/newman#using-reporters-with-newman).

### Running locally with Postman
To implement new tests or run existing locally, there is possibility to use Postman directly. Just use _Import_ button
in Postman and import collection file into Postman.
