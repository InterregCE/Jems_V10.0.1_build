# API integration tests

This module is used for integration testing without FE - testing API.

For this purpose we use [Postman Collections](https://www.postman.com/collection/) stored in the
[collections](collections) folder.

### Running through CLI
Postman Collections can be run in cmd with a tool called `newman`. It is a CLI interface for running postman
collections. Inside the [environments](environments) folder, there are configs for multiple environments.

To execute those tests, run:
- `npm run test-local` - to use [local.postman_environment.json](environments/local.postman_environment.json) config
- `npm run test-663-ems-staging` (or similar) - to use other configs (see [package.json](package.json)/scripts)
##### Reporting
Running newman in CI/CD pipelines can also extract results for further processing. This feature is part of newman and
is called Reporting, for detailed options see [newman-reporters](https://github.com/postmanlabs/newman#using-reporters-with-newman).

### Running locally with Postman
To implement new tests or run existing ones locally, there is a possibility to use Postman directly. Just use the _Import_ button
in Postman and import _collection file_ & _environment files_ into Postman.

In Postman Settings (_File > Settings > General > Working Directory_), adjust your working directory to the
[jems-rest-test](../jems-rest-test) folder. You can run the whole collection or just one specific folder. If you want to
run a specific folder, you might need to run the **setup** folder once, **first before the others**. This one will create basic
users and programme data, which are needed for every followup test.