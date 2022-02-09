context('Project moderation tests', () => {
    beforeEach(() => {
        cy.viewport(1920, 1080);
    });

    it('Project can be created', () => {
        cy.fixture('users.json').then((user) => {
            cy.visit('/');

            cy.get('#email').type(user.applicationUser.username);
            cy.get('#password').type(Cypress.env('defaultPassword') + '{enter}');

            cy.get('app-call-list').should('exist');

            cy.contains('Apply').click();
            cy.contains('Apply').click();

            const uuid = () => Cypress._.random(0, 1e6)
            const id = `${uuid()}`;
            cy.get('input[name="acronym"]').type(`Automation Project ${id}`);

            cy.contains('Create project application').click();

            cy.get('app-project-application-list').find('mat-cell').contains(`Automation Project ${id}`)
        })
    });
})
