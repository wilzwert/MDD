import { request } from "http";

describe('User profile page', () => {

    let mockSubscriptions: Array<Object>;
    
    before(() => {
        cy.fixture('subscriptions.json').as('subscriptionsJson').then(json => {mockSubscriptions = json;});
    })
    /*
    it('should redirect to login if not logged in', () => {
        cy.visit('/me');
        // precisely match homepage url
        cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
    })*/

    it('should display user update form and allow update', () => {
        cy.login();
        cy.get('.account[href="/me"]').click();
        cy.url().should('to.match', /me$/);
        cy.get('app-back-button mat-icon').should('not.exist');

        cy.contains('h1', "Profil utilisateur");
        cy.get('[formControlName="username"]').should('have.value', 'username');
        cy.get('[formControlName="email"]').should('have.value', 'test@example.com');
        cy.contains('Sauvegarder').should('not.be.disabled');

        cy.get('[formControlName="username"]').clear().should('have.class', 'ng-invalid');
        cy.contains('Sauvegarder').should('be.disabled');
        cy.get('[formControlName="username"]').type('newuser').should('have.class', 'ng-valid');
        cy.contains('Sauvegarder').should('not.be.disabled');
        cy.get('[formControlName="email"]').clear().should('have.class', 'ng-invalid');
        cy.contains('Sauvegarder').should('be.disabled');
        cy.get('[formControlName="email"]').type('newemail').should('have.class', 'ng-invalid');
        cy.contains('Sauvegarder').should('be.disabled');
        cy.get('[formControlName="email"]').type('@example.com').should('have.class', 'ng-valid');
        cy.contains('Sauvegarder').should('not.be.disabled');

        cy.intercept(
            {
                method: 'PUT',
                url: '/api/user/me'
            },
            {statusCode: 409, body: { message: 'Username or email already exists'}}
        ).as('conflictFailure');
        cy.contains('Sauvegarder').click();
        cy.wait('@conflictFailure');
        cy.contains("Email ou nom d'utilisateur déjà utilisé");

        cy.intercept(
            {
                method: 'PUT',
                url: '/api/user/me'
            },
            {statusCode: 500, body: { message: 'Internal server error'}}
        ).as('internalFailure');
        cy.contains('Sauvegarder').click();
        cy.wait('@internalFailure');
        cy.contains('Une erreur est survenue');

        cy.intercept(
            {
                method: 'PUT',
                url: '/api/user/me'
            },
            (request) => request.reply({
                id: 1,
                username: "newuser",
                email: "newemail@example.com"
            })
        ).as('updateSuccess');
        cy.contains('Sauvegarder').click();
        cy.wait('@updateSuccess');
        cy.contains('Modifications enregistrées');
    });

    it('should display link to topics when user has no subscriptions', () => {
        cy.login();

        cy.get('.account[href="/me"]').click();
        cy.url().should('to.match', /me$/);
        cy.get('app-back-button mat-icon').should('not.exist');

        cy.contains('h2', "Abonnements");
        cy.get('app-topic').should('have.lengthOf', 0);
        cy.contains('Aucun abonnement pour le moment.');
        cy.contains('a.topics', 'Cliquez ici');
    });
    
    it('should display user subscriptions and allow unsubscription', () => {
        cy.intercept(
            {
              method: 'GET',
              url: '/api/user/me/subscriptions',
              middleware: true
            },
            (req) => {req.reply(mockSubscriptions);}
        ).as('subsriptions');
        
        cy.login();

        cy.get('.account[href="/me"]').click();
        cy.url().should('to.match', /me$/);
        cy.get('app-back-button mat-icon').should('not.exist');

        cy.contains('h2', "Abonnements");
        cy.get('app-topic').should('have.lengthOf', 2);
        cy.contains('Aucun abonnement pour le moment.').should('not.exist');
        cy.contains('a.topics', 'Cliquez ici').should('not.exist');

        cy.intercept(
            {
              method: "DELETE",
              url: "/api/topics/1/subscription"
            },
            (request => {request.reply('');})
          ).as('deleteSubscription');
    
          cy.contains("Se désabonner").click();
          cy.wait('@deleteSubscription');
          cy.contains('Désabonnement pris en compte');
          cy.get('app-topic').should('have.lengthOf', 1);
    });
});