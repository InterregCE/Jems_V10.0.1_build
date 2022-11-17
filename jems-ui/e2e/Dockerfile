FROM cypress/included:11.1.0

WORKDIR e2e

# copy test files
COPY .git .git
COPY jems-ui/e2e/cypress cypress
COPY jems-ui/e2e/package-lock.json .
COPY jems-ui/e2e/package.json .
COPY jems-ui/e2e/tsconfig.json .
COPY jems-ui/e2e/cypress.config.ts .

ENV NO_COLOR=1
ENV CYPRESS_BASE_URL=$CYPRESS_BASE_URL
ENV CYPRESS_defaultPassword=$CYPRESS_defaultPassword

RUN npm i

ENTRYPOINT ["npm", "run", "test"]
