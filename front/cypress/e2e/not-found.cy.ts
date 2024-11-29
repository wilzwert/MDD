describe('Page not found', () => {
  
  it('Should redirect to /404 and display 404 message', () => {
    cy.visit('/unknown-url')

    cy.url().should('to.match', /\/404$/)
    cy.contains('h1', 'Page not found !');
  })

  it('Should go back to home when not logged in and back button clicked', () => {
    cy.visit('/unknown-url');
    cy.url().should('to.match', /\/404$/)
    cy.contains('h1', 'Page not found !');
    cy.get('app-back-button mat-icon').click();
    // we use a regexp because in some circumstances it seems a trailing slash may be added
    cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
  })

  it('Should go back to posts when logged in and back button clicked', () => {
    cy.login();
    cy.visit('/unknown-url');
    cy.url().should('to.match', /\/404$/)
    cy.contains('h1', 'Page not found !');
    cy.get('app-back-button mat-icon').click();
    cy.url().should('to.match', /posts$/);
  })
});