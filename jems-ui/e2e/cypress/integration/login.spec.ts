context('Login tests', () => {
    beforeEach(() => {
        cy.viewport(1920, 1080);
    });

    it('Admin can login and logout', () => {
        cy.fixture('users.json').then((user) => {
            cy.visit('/');

            cy.intercept('api/project/mine?*').as('applicationList');

            cy.get('#email').type(user.admin.username);
            cy.get('#password').type(Cypress.env('defaultPassword') + '{enter}');

            cy.wait('@applicationList', { timeout: 10000 });

            cy.get('h2').should('contain', 'My applications');

            cy.intercept('api/auth/logout').as('logout');
            cy.get('button.logout-button').click();
            cy.wait('@logout');

            cy.get('button span').contains('Login').should('exist');
        })
    });

    it('Applicant can register', () => {
        cy.visit('/');

        cy.get('a').contains('Create a new account').click();
        const uuid = () => Cypress._.random(0, 1e6)
        const id = `${uuid()}`;
        const email = `cypress.${id}@Applicant.eu`;

        cy.get('input[name="name"]').type('Cypress');
        cy.get('input[name="surname"]').type(id);
        cy.get('input[name="email"]').type(email);
        cy.get('input[name="password"]').type(email);
        cy.get('mat-checkbox').click("left");

        cy.get('button[color="primary"]').click();

        cy.get('app-alert').find('span').contains('Go to login').should('be.visible');
    });
})
