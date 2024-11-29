describe('Register', () => {
    it('Should redirect to posts if logged in', () => {
      // see command.ts
      cy.login();
      cy.visit('/register');
      cy.url().should('to.match', /posts$/);
    });

    it('Should go back to home page', () => {
      cy.visit("/");
      cy.contains("S'inscrire").click();
      cy.url().should('to.match', /register$/);
      cy.get('app-back-button mat-icon').click();
      // we use a regexp because in some circumstances it seems a trailing slash may be added
      cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
  });

    it('should register after email or username conflict', () => {
      cy.visit('/register');

      cy.contains('h1', 'Inscription');

      // by default form should be invalid
      cy.contains("Min. 5 caractères");
      cy.contains("Min. 8 caractères donc 1 minuscule, 1 majuscule, 1 chiffre et 1 caractère spécial");
      cy.get('[formControlName="email"]').should('have.class', 'ng-invalid');
      cy.get('[formControlName="username"]').should('have.class', 'ng-invalid');
      cy.get('[formControlName="password"]').should('have.class', 'ng-invalid');
      cy.get('mat-icon.input-invalid').should('have.lengthOf', 3);
      cy.contains('button[type=submit]', "S'inscrire").should('be.disabled');

      // form should be valid after user fills it correctly
      cy.get('[formControlName="email"]').type('test').should('have.class', 'ng-invalid');
      cy.get('.email-hint mat-icon').should('have.class', 'input-invalid');
      cy.get('[formControlName="username"]').type('username').should('have.class', 'ng-valid');
      cy.get('.username-hint mat-icon').should('have.class', 'input-valid');
      cy.get('[formControlName="email"]').type('@example.com').should('have.class', 'ng-valid');
      cy.get('.email-hint mat-icon').should('have.class', 'input-valid');
      cy.get('[formControlName="password"]').type('Abcd').should('have.class', 'ng-invalid');
      cy.get('.password-hint mat-icon').should('have.class', 'input-invalid');
      cy.contains('button[type=submit]', "S'inscrire").should('be.disabled');
      cy.get('[formControlName="password"]').type('.1234').should('have.class', 'ng-valid');
      cy.get('.password-hint mat-icon').should('have.class', 'input-valid');

      cy.intercept(
        {
          method: 'POST',
          url: '/api/auth/register',
          times: 1
        },
        {statusCode: 409, body: {message: 'Username already exists'}}
      ).as('conflictError');

      cy.contains('button[type=submit]', "S'inscrire").should('not.be.disabled').click();
      cy.wait('@conflictError');
      cy.contains("Email ou nom d'utilisateur déjà utilisé");

      cy.intercept(
        {
          method: 'POST',
          url: '/api/auth/register',
          times: 1
        },
        {statusCode: 500, body: {message: 'Internal server error'}}
      ).as('internalError');
      cy.contains('button[type=submit]', "S'inscrire").should('not.be.disabled').click();
      cy.wait('@internalError');
      cy.contains("Une erreur est survenue");


      cy.intercept(
        {
          method: 'POST',
          url: '/api/auth/register',
          times: 1
        },
        (request) => request.reply(
          {
            id: 1,
            username: "username",
            createdAt: "2024-11-30T01:00:00"
          }
        )
      ).as('registerSuccess');

      cy.contains('button[type=submit]', "S'inscrire").should('not.be.disabled').click();
      cy.wait('@registerSuccess');
      cy.contains("Votre inscription a bien été enregistrée, vous pouvez maintenant vous connecter");
      cy.url().should('to.match', /login$/);
    });
  });