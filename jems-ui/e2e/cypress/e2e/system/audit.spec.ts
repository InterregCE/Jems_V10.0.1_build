import user from '../../fixtures/users.json';
import {faker} from "@faker-js/faker";

context('Application Audit log tests', () => {

  it('TB-926 User can access audit log page and see logs', () => {
    cy.fixture('system/audit/TB-926.json').then(testData => {

      // login with admin
      cy.loginByRequest(user.admin.email);

      // create audit role
      testData.auditRole.name = `${testData.auditRole.name}_${faker.string.numeric(5)}`;
      cy.createRole(testData.auditRole).then(roleId => {

        // create audit user
        testData.auditUser.userRoleId = roleId;
        testData.auditUser.email = faker.internet.email();
        testData.auditUser.name = faker.person.fullName();
        cy.createUser(testData.auditUser).then(userId => {
          // login with audit user and wait for audit log sync
          cy.loginByRequest(testData.auditUser.email);
          cy.wait(2000);

          // go to AuditLog page
          cy.visit('/');
          cy.contains('System').click();
          cy.contains('Audit log').should('be.visible').click();

          // assert audit user logged in
          cy.contains('mat-row', testData.auditUser.email).within(() => {
            cy.contains(userId).scrollIntoView().should('be.visible');
            cy.contains('USER_LOGGED_IN').should('be.visible');
            cy.contains(`user with email ${testData.auditUser.email} logged in`).scrollIntoView().should('be.visible');
          });
        });
      });
    });
  });
});
