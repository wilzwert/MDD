describe("Post creation", () => {
    let mockTopics: Array<Object>;
    
    before(() => {
        cy.fixture('topics.json').as('topicsJson').then(json => {mockTopics = json;});
    })
    
    it('should redirect to login if not logged in', () => {
        cy.visit('/posts/create');
        // precisely match homepage url
        cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
    })

    it('Should redirect to posts page when no topic found', () => {
      cy.login();
      cy.intercept(
        {
          method: 'GET',
          url: '/api/topics',
          times: 1
        },
        (request => {request.reply([]);})
      ).as('getTopics');

      cy.visit('/posts/create');
      cy.wait('@getTopics');
      cy.contains("Aucun thème disponible, impossible de créer un article");
      cy.url().should('to.match', /posts$/);
  });

    it('Should go back to posts page', () => {
      cy.login();
      cy.intercept(
        {
          method: 'GET',
          url: '/api/topics',
          times: 1
        },
        (request => {request.reply(mockTopics);})
      ).as('getTopics');

      cy.visit('/posts/create');
      cy.wait('@getTopics');
      cy.get('app-back-button mat-icon').click();
      // we use a regexp because in some circumstances it seems a trailing slash may be added
      cy.url().should('to.match', /posts$/);
  });

    it('should create post after failed attempt', () => {
      cy.intercept(
        {
          method: 'GET',
          url: '/api/posts',
          // force this interception to be called first to "override" interception made in cy.login()
          middleware: true
        },
        (request => {request.reply([]);})
      );

      cy.intercept(
        {
          method: 'GET',
          url: '/api/user/me/subscriptions',
          times: 1
        },
        (request => {request.reply([]);})
      );

      cy.intercept(
        {
          method: 'GET',
          url: '/api/topics',
          times: 1
        },
        (request => {request.reply(mockTopics);})
      ).as('getTopics');

      cy.login();

      cy.contains('Créer un article').click();
      cy.wait('@getTopics');

      // by default form should be invalid
      cy.get('[formControlName="topicId"]').should('have.class', 'ng-invalid');
      cy.get('[formControlName="title"]').should('have.class', 'ng-invalid');
      cy.get('[formControlName="content"]').should('have.class', 'ng-invalid');
      cy.get('[formControlName="title"]').should('have.class', 'ng-invalid');
      cy.get('mat-icon.input-invalid').should('have.lengthOf', 3);
      cy.contains('button[type=submit]', 'Créer').should('be.disabled');

      cy.get('form [formControlName=topicId]').click().get('mat-option').contains('Test topic').click();
      cy.get('.topic-hint mat-icon').should('have.class', 'input-valid');

      // enter should do nothing
      cy.get('form [formControlName=title]').type('Post title{enter}');
      cy.get('.title-hint mat-icon').should('have.class', 'input-valid');

      cy.get('form [formControlName=content]').type('Post content{enter}');
      cy.get('.content-hint mat-icon').should('have.class', 'input-valid');
      cy.contains('button[type=submit]', 'Créer').should('not.be.disabled');

      // API error
      cy.intercept(
        {
          method: 'POST',
          url: '/api/posts',
          times: 1
        }, 
        {statusCode: 500, body: {message: "Internal server error"}}
      ).as('creationError');
      cy.contains('button[type=submit]', 'Créer').click();
      cy.wait('@creationError');
      cy.contains("Internal server error");

      cy.intercept(
        {
          method: 'POST',
          url: '/api/posts',
          times: 1
        }, 
        (request) => request.reply(
          {
            "id": 1,
            "title": "Post title",
            "content": "Post content",
            "createdAt": "2024-11-29T08:45:12",
            "updatedAt": "2024-11-29T08:45:12" ,
            "author": {
                "id": 1,
                "username": "testuser",
                "email": "test@example.com"
            },
            "topic": {
                "id": 1,
                "title": "Test topic",
                "description": "Test topic description"
            }
        })
      ).as('creationSuccess');
      cy.contains('button[type=submit]', 'Créer').click();
      cy.wait('@creationSuccess');
      cy.contains("Votre article a bien été créé");
      cy.url().should('to.match', /posts$/);
    })
})