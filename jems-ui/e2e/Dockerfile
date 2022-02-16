ARG cypress_version=9.4.1
FROM cypress/included:$cypress_version

WORKDIR e2e

# copy test files
COPY .git .git
COPY jems-ui/e2e/package.json .
COPY jems-ui/e2e/package-lock.json .
COPY jems-ui/e2e/tsconfig.json .
COPY jems-ui/e2e/cypress.json .
COPY jems-ui/e2e/cypress cypress

RUN npm ci

ENTRYPOINT ["npm", "run", "test"]
