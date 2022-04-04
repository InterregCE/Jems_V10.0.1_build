FROM cypress/included:9.4.1

WORKDIR e2e

# copy test files
COPY .git .git
COPY jems-ui/e2e/cypress cypress
COPY jems-ui/e2e/package-lock.json .
COPY jems-ui/e2e/package.json .
COPY jems-ui/e2e/tsconfig.json .
COPY jems-ui/e2e/cypress.json .

ENV NO_COLOR=1

RUN npm ci

ENTRYPOINT ["npm", "run", "test"]
