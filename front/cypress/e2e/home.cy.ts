describe('Home page', () => {
  it('Visits the home page', () => {
    cy.visit('/')
    cy.contains('Se connecter')
  })
})
