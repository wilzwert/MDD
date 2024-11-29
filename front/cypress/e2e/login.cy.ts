describe('Login', () => {
    it('Should redirect to posts if logged in', () => {
        // see command.ts
        cy.login();
        cy.visit('/login');
        cy.url().should('to.match', /posts$/);
      })

    it('Should go back to home page', () => {
        cy.visit("/");
        cy.contains('Se connecter').click();
        cy.url().should('to.match', /login$/);
        cy.get('app-back-button mat-icon').click();
        // we use a regexp because in some circumstances it seems a trailing slash may be added
        cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
    });

    it('Login successfull', () => {
      // see command.ts
      cy.login();
    })
    
    it('Login failed', () => {
      cy.visit('/login')
  
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 401
      })
     
      cy.get('input[formControlName=email]').type("test@example.com")
      cy.get('input[formControlName=password]').type("password{enter}{enter}")
      cy.contains('La connexion a échoué.');
  
    })
    
    it('Login successful after failed attempts', () => {
      cy.visit('/login')
  
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 401
      }).as('loginFailure')
  
      cy.get('input[formControlName=email]').type("test");
      cy.get('input[formControlName=password]').type("password");
      cy.get('input[formControlName=email]').should('have.class', 'ng-invalid');
      cy.get('input[formControlName=password]').should('have.class', 'ng-valid');
      cy.get('button[type=submit]').should('be.disabled');
  
      cy.get('input[formControlName=password]').clear();
      // try to input enter key to trigger submit, which should do nothing
      cy.get('input[formControlName=email]').type("test@example.com{enter}{enter}");
      cy.get('input[formControlName=email]').should('have.class', 'ng-valid');
      cy.get('input[formControlName=password]').should('have.class', 'ng-invalid');
      cy.get('button[type=submit]').should('be.disabled');

      cy.get('input[formControlName=password]').type('password{enter}');
      cy.get('button[type=submit]').should('not.be.disabled');

      cy.wait('@loginFailure');

      cy.contains('La connexion a échoué.').should('be.visible');
  
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'username',
          token: 'access_token',
          type: 'Bearer',
          refreshToken: 'refresh_token'
        },
      }).as('loginSuccess')
  
      cy.intercept(
        {
          method: 'GET',
          url: '/api/posts',
        },
        []).as('posts')

        cy.intercept(
            {
              method: 'GET',
              url: '/api/user/me/subscriptions',
            },
            []).as('subscriptions')
  
      
      cy.get('input[formControlName=email]').clear();
      cy.get('input[formControlName=email]').type("test@example.com");
      cy.get('input[formControlName=email]').should('have.class', 'ng-valid');
      cy.get('input[formControlName=password]').type("Test!1234");
      cy.get('input[formControlName=password]').should('have.class', 'ng-valid');
      cy.get('button[type=submit]').should('be.enabled');
      cy.get('input[formControlName=password]').type("{enter}{enter}");
  
      cy.wait('@loginSuccess');
      
      cy.url().should('to.match', /\/posts$/)
    })
  });