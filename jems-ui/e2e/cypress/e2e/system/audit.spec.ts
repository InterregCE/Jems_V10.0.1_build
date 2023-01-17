import user from '../../fixtures/users.json';
import {faker} from "@faker-js/faker";

context('Application Audit log tests', () => {

  it('TB-926 User can access audit log page and see logs', () => {
    cy.fixture('system/audit/TB-926.json').then(testData => {

      // login with admin
      cy.loginByRequest(user.admin.email);

      // create audit role
      testData.auditRole.name = `${testData.auditRole.name}_${faker.random.numeric(5)}`;
      cy.createRole(testData.auditRole).then(roleId => {

        // create audit user
        testData.auditUser.userRoleId = roleId;
        testData.auditUser.email = faker.internet.email();
        testData.auditUser.name = faker.name.fullName();
        cy.createUser(testData.auditUser).then(userId => {
          // login with audit user
          cy.loginByRequest(testData.auditUser.email);

          // go to AuditLog page
          cy.visit('/', {failOnStatusCode: false});
          cy.contains('System').click();
          cy.contains('Audit log').should('be.visible').click();

          // assert audit user logged in
          cy.get('table mat-row').then(row => {
            expect(row).has.length.of.at.least(1);
            expect(row.get(0).childNodes[1]).to.contain(userId);
            expect(row.get(0).childNodes[2]).to.contain(testData.auditUser.email);
            expect(row.get(0).childNodes[3]).to.contain('USER_LOGGED_IN');
            expect(row.get(0).childNodes[4]).to.contain('');
            expect(row.get(0).childNodes[5]).to.contain(`user with email ${testData.auditUser.email} logged in`);
          });
        });
      });
    });
  });
});
