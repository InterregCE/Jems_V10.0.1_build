FROM cypress/browsers

WORKDIR e2e
ENV CYPRESS_DOWNLOAD_MIRROR=https://cy-cdn.currents.dev

# copy test files
COPY .git .git
COPY jems-ui/e2e/cypress cypress
COPY jems-ui/e2e/package-lock.json .
COPY jems-ui/e2e/package.json .
COPY jems-ui/e2e/tsconfig.json .
COPY jems-ui/e2e/cypress.config.ts .
COPY jems-ui/e2e/currents.config.js .

ENV NO_COLOR=1

RUN npm i

ENTRYPOINT ["npm", "run", "test"]
