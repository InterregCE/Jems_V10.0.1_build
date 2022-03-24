import user from '../../../fixtures/users.json';
import testData from '../../../fixtures/application/project-privileges/TB-379.json';

context('Project privileges tests', () => {

  beforeEach(() => {
    cy.viewport(1920, 1080);
  });

  it('TB-379 Automatically assign users to projects', function () {
    cy.loginByRequest(user.admin);
    cy.createUser(testData.privilegedUser);
    cy.visit('app/project', {failOnStatusCode: false});

    cy.contains('Assignment').click();
    cy.get('.mat-paginator-range-label').then(paginatorRange => {
      const numberOfPages = +paginatorRange.text().match(/\d - \d+ of (\d+)/)[1];

      cy.get(`mat-chip:contains('${testData.privilegedUser.email}')`).should(chipElements => {
        expect(chipElements).to.have.length(numberOfPages);
        expect(chipElements).not.to.have.class('mat-chip-selected-user');
      });

    });
  });
})
