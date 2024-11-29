describe("Logout", () => {
        
    it('should logout user ', () => {
        cy.login();

        cy.visit('/me')

        cy.contains('a.logout', 'Se déconnecter').click();

        // we use a regexp because in some circumstances it seems a trailing slash may be added
        cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
        // cy.url().should('eq', Cypress.config().baseUrl +'/');
        cy.contains('a.link', 'Articles').should('not.exist');
        cy.contains('a.link', 'Thèmes').should('not.exist');
        cy.get('a.account').should('not.exist');
        cy.contains('Se connecter');
        cy.contains("S'inscrire");
    })
})