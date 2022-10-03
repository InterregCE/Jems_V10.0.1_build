import user from "../../fixtures/users.json";
import {faker} from "@faker-js/faker";

context('Controller tests', () => {
  it('TB-810 Controller institutions can be created', () => {
    cy.fixture('controller/TB-810.json').then(testData => {

      cy.loginByRequest(user.admin.email);
      testData.controllerUser1.email = faker.internet.email();
      testData.controllerUser2.email = faker.internet.email();
      cy.createUser(testData.controllerUser1);
      cy.createUser(testData.controllerUser2);
      testData.institution.users[0].email = testData.controllerUser1.email;
      testData.institution.users[1].email = testData.controllerUser2.email;
      testData.controllerCreatorRole.name = `controllerCreator_${faker.random.alphaNumeric(5)}`;
      testData.controllerCreatorUser.email = faker.internet.email();
      cy.createRole(testData.controllerCreatorRole).then(roleId => {
        testData.controllerCreatorUser.userRoleId = roleId;
        cy.createUser(testData.controllerCreatorUser);
      });
      cy.loginByRequest(testData.controllerCreatorUser.email);

      cy.visit('/');
      cy.contains('Controllers').click();

      cy.contains('Add institution').click();
      testData.institution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
      cy.contains('div', 'Name').find('input').type(testData.institution.name);
      cy.contains('div', 'Description').find('textarea').type(testData.institution.description);

      testData.institution.nuts.forEach(nuts => {
        cy.contains('mat-checkbox', nuts).click();
      });

      testData.institution.users.forEach(user => {
        cy.contains('button', 'Add user').click();
        cy.contains('div #institution-collaborators-table-content', 'Cannot be blank').within(() => {
          cy.contains('div', 'Jems username').find('input').type(user.email);
          cy.contains('button', user.accessLevel).click();
        });
      });

      cy.contains('button', 'Create').click();
      cy.get('#institution-collaborators-table-content input').should('be.disabled');

      cy.contains('Add user').click()
      cy.contains('div #institution-collaborators-table-content', 'Cannot be blank').within(() => {
        cy.contains('div', 'Jems username').find('input').type('applicant');
        cy.contains('Input data are not valid').should('be.visible');
        cy.contains('div', 'Jems username').find('input').type('.user@jems.eu');
      });

      cy.contains('Save changes').click();
      cy.contains('Not possible to save: Make sure the username is correctly typed and privileged to monitor projects.').should('be.visible');
      cy.get('button:contains("delete")').last().click();
      cy.contains('Save changes').click();
      cy.contains('Controller institution was updated successfully').should('be.visible');

      cy.contains('Institutions').click();
      cy.contains('mat-row', testData.institution.name).should('be.visible');
      cy.contains(testData.institution.nutsVerification).should('be.visible');
    });
  });
});