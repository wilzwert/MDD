// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
// declare namespace Cypress {
//   interface Chainable<Subject = any> {
//     customCommand(param: any): typeof customCommand;
//   }
// }
//
// function customCommand(param: any): void {
//   console.warn(param);
// }
//
// NOTE: You can use it like so:
// Cypress.Commands.add('customCommand', customCommand);
//
// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
Cypress.Commands.add("login", () => {
    cy.visit('/login');
  
    cy.intercept(
        {
            method: 'POST',
            url:  '/api/auth/login'
        }, 
        req => {
            req.reply({
                type: 'Bearer',
                token: 'access_token',
                refreshTokn: 'refresh_token',
                id: 1,
                username: 'username'
              })
        }
    ).as('login');

    cy.intercept(
        {
          method: 'GET',
          url: '/api/posts',
          times: 1
        },
        (req) => {req.reply([]);}
    ).as('posts')

    cy.intercept(
        {
          method: 'GET',
          url: '/api/user/me/subscriptions',
          times: 1
        },
        (req) => {req.reply([]);}
    ).as('subsriptions')

    cy.intercept(
        {
            method: 'GET',
            url:  '/api/user/me'
        }, 
        req => {
            req.reply({
                id: 1,
                username: 'username',
                email: 'test@example.com'
              })
        }
    ).as('userMe');
  
    cy.get('input[formControlName=email]').type("user@example.com");
    cy.get('input[formControlName=password]').type("password{enter}{enter}");
    cy.url().should('include', '/posts');
  });
