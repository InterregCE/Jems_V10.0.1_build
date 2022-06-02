# E2E Cypress tests

In this directory there are files, which are used to run continuous integration tests.

### Development setup
If you want to run cypress locally, just:
- run `npm i` in this directory, which will download Cypress
- run `npm run open` to start cypress
  - if that will fail because `Cypress verification timed out`, for me what helped was running `npx cypress verify`
