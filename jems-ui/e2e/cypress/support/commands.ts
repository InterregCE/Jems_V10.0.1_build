import faker from '@faker-js/faker';

declare global {

  namespace Cypress {
    interface Chainable {
      parsePDF();

      parseXLSX();

      clickToDownload(requestToIntercept: string, fileExtension: string);
    }
  }
}

Cypress.Commands.add('parsePDF', {prevSubject: true}, (subject) => {
  cy.task('parsePDF', subject);
});

Cypress.Commands.add('parseXLSX', {prevSubject: true}, (subject) => {
  cy.task('parseXLSX', subject);
});

Cypress.Commands.add('clickToDownload', {prevSubject: true}, (subject, requestToIntercept, fileExtension) => {
  const randomizeDownload = `downloadRequest ${faker.random.alphaNumeric(5)}`;
  cy.intercept(requestToIntercept).as(randomizeDownload);
  cy.wrap(subject).click();
  cy.wait(`@${randomizeDownload}`).then(result => {
    const regex = new RegExp(`filename="(.*\.${fileExtension})"`);
    const fileNameMatch = regex.exec(result.response.headers['content-disposition'].toString());
    if (!fileNameMatch) {
      throw new Error(`Downloaded file does not have ${fileExtension} extension`);
    }
    const fileName = fileNameMatch[1];
    if (fileExtension === 'pdf') {
      cy.readFile('./cypress/downloads/' + fileName, null).parsePDF().then(file => {
        file.fileName = fileName;
        cy.wrap(file);
      });
    } else if (fileExtension === 'xlsx') {
      cy.readFile('./cypress/downloads/' + fileName, null).parseXLSX().then(content => {
        const file = {fileName: fileName, content: content};
        cy.wrap(file);
      });
    } else {
      throw new Error('No implementation for: ' + fileExtension);
    }
  });
});

export {}
