ARG cypress_version=9.4.1
FROM cypress/included:$cypress_version

WORKDIR e2e

# copy test files
# COPY .git .git
COPY package.json .
COPY package-lock.json .
COPY tsconfig.json .
COPY cypress.json .
COPY cypress cypress

RUN npm ci

ENTRYPOINT ["npm", "run", "test"]
