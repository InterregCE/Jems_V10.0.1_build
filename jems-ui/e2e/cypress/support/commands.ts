import {faker} from '@faker-js/faker';

declare global {

  namespace Cypress {
    interface Chainable {
      parseXLSX();

      comparePdf(templatePdf: string, actualPdf: string, masks: any, baselinePath: string);

      clickToDownload(requestToIntercept: string, fileExtension: string);
    }
  }
}

Cypress.Commands.add('parseXLSX', {prevSubject: true}, (subject) => {
  cy.task('parseXLSX', subject);
});

Cypress.Commands.add('comparePdf', {prevSubject: false}, (templatePdf, actualPdf, masks, baselinePath) => {
  cy.task('comparePdf', {templatePdf, actualPdf, masks, baselinePath}).then((result: any) => {
    result.details?.forEach(detail => {
      cy.log(detail.diffPng);
    });
    cy.wrap(result);
  });
})

Cypress.Commands.add('clickToDownload', {prevSubject: true}, (subject, requestToIntercept, fileExtension) => {
  const randomizeDownload = `downloadRequest_${faker.string.alphanumeric(5)}`;
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
      cy.wait(1000);
      cy.readFile('./cypress/downloads/' + fileName, null).parseXLSX().then(content => {
        const file = {fileName: fileName, content: content};
        cy.wrap(file);
      });
    } else if (fileExtension === 'txt') {
      const returnValue = {fileName: fileName}
      cy.wrap(returnValue);
    } else {
      throw new Error('No implementation for: ' + fileExtension);
    }
  });
});

// https://stackoverflow.com/a/63519375/4876320
Cypress.on('uncaught:exception', (err) => {
  /* returning false here prevents Cypress from failing the test */
  if (err.message.includes('ResizeObserver loop completed with undelivered notifications')) {
    return false
  }
})

export {}
