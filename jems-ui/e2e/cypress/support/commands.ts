import {faker} from '@faker-js/faker';

declare global {

  namespace Cypress {
    interface Chainable {
      parseXLSX();

      comparePdf(templatePdf: string, actualPdf: string, masks: any);

      clickToDownload(requestToIntercept: string, fileExtension: string);
    }
  }
}

Cypress.Commands.add('parseXLSX', {prevSubject: true}, (subject) => {
  cy.task('parseXLSX', subject);
});

Cypress.Commands.add('comparePdf', {prevSubject: false}, (templatePdf, actualPdf, masks) => {
  cy.task('comparePdf', {templatePdf, actualPdf, masks});
})

Cypress.Commands.add('clickToDownload', {prevSubject: true}, (subject, requestToIntercept, fileExtension) => {
  const randomizeDownload = `downloadRequest ${faker.random.alphaNumeric(5)}`;
  cy.intercept(requestToIntercept).as(randomizeDownload);
  cy.wrap(subject).click();
  cy.wait(`@${randomizeDownload}`).then(result => {
    const regex = new RegExp(`filename="(.*\.${fileExtension})"`);
    const localDateTime = new URLSearchParams(result.request.url).get('localDateTime');
    const fileNameMatch = regex.exec(result.response.headers['content-disposition'].toString());
    if (!fileNameMatch) {
      throw new Error(`Downloaded file does not have ${fileExtension} extension`);
    }
    const fileName = fileNameMatch[1];
    if (fileExtension === 'pdf') {
      cy.readFile('./cypress/downloads/' + fileName, null).then(file => {
        file.fileName = fileName;
        file.localDateTime = localDateTime;
        cy.wrap(fileName);
      })
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
