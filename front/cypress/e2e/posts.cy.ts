describe("Posts list", () => {
    let mockPosts: Array<Object>;
    let mockTopics: Array<Object>;
    
    before(() => {
        cy.fixture('posts.json').as('postsJson').then(json => {mockPosts = json;});
        cy.fixture('topics.json').as('topicsJson').then(json => {mockTopics = json;});
    })
    
    it('should redirect to login if not logged in', () => {
        cy.visit('/posts');
        // precisely match homepage url
        cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
    })

    it('should redirect to posts when detail post not found', () => {
      cy.intercept(
        {
          method: 'GET',
          url: '/api/posts',
          // force this interception to be called first to "override" interception made in cy.login()
          middleware: true
        },
        (request => {request.reply(mockPosts);})
      );

      cy.intercept(
        {
          method: 'GET',
          url: '/api/user/me/subscriptions',
          times: 1
        },
        (request => {request.reply([]);})
      );


        cy.login();

        cy.intercept('/api/posts/99', {statusCode: 404});

        cy.visit('/posts/99');

        cy.contains("Impossible de charger l'article");
        // precisely match homepage url
        cy.url().should('to.match', /posts$/);
    })

    it('should go back to posts list when back button clicked', () => {
      cy.intercept(
        {
          method: 'GET',
          url: '/api/posts',
          // force this interception to be called first to "override" interception made in cy.login()
          middleware: true
        },
        (request => {request.reply(mockPosts);})
      );
      cy.login();
      cy.intercept(
        {
            method: 'GET',
            url: '/api/posts/1'
        },
        (request => {request.reply(mockPosts[0]);})
      ).as('getPost');
      cy.intercept(
        {
            method: 'GET',
            url: '/api/posts/1/comments'
        },
        (request => {request.reply([]);})
      ).as('getComments');
      cy.contains(mockPosts[0].title).click();
      cy.wait('@getPost');
      cy.wait('@getComments');

      // back button
      cy.get('mat-icon[data-mat-icon-name="arrow-left"]').should('exist').click();
      cy.url().should('to.match', /posts$/);
    });
    
    it('should display posts list and allow article detail page access', () => {
        cy.intercept(
            {
              method: 'GET',
              url: '/api/posts',
              // force this interception to be called first to "override" interception made in cy.login()
              middleware: true
            },
            (request => {request.reply(mockPosts);})
        );
        cy.login();

        cy.contains('Créer un article');
        cy.get('app-post').should('have.lengthOf', 1);

        cy.intercept(
            {
                method: 'GET',
                url: '/api/posts/1'
            },
            (request => {request.reply(mockPosts[0]);})
        ).as('getPost');
        cy.intercept(
          {
              method: 'GET',
              url: '/api/posts/1/comments'
          },
          (request => {request.reply([]);})
        ).as('getComments');
        cy.contains(mockPosts[0].title).click();
        cy.wait('@getPost');
        cy.wait('@getComments');

        // back button
        cy.get('mat-icon[data-mat-icon-name="arrow-left"]').should('exist');
        // check post content
        cy.contains(mockPosts[0].title);
        cy.contains("06/11/2024");
        cy.contains(mockPosts[0].author.username);
        cy.contains(mockPosts[0].topic.title);
        cy.contains(mockPosts[0].content);

        // comments (no comments expected)
        cy.contains('h2', 'Commentaires');
        cy.get('.element-comment').should('have.lengthOf', 0);
        cy.get('form.comment-form').should('have.lengthOf', 1);
    })
    
    it('should display message when no posts found', () => {
        cy.login();

        cy.contains('Créer un article');
        cy.get('app-post').should('have.lengthOf', 0);
        cy.contains('Aucun article pour le moment.');
        cy.contains('a.topics', 'Cliquez ici');
    })

    it('should display topics link when no posts found then display posts when user subscribes to some topics', () => {
        cy.login();
        
        cy.intercept(
          {
            method: 'GET',
            url: '/api/topics',
            middleware: true
          },
          (request => {request.reply(mockTopics);})
        );
        cy.intercept(
            {
              method: 'GET',
              url: '/api/user/me/subscriptions',
              middleware: true
            },
            (request => {request.reply([]);})
          );

        cy.intercept(
        {
            method: 'POST',
            url: '/api/topics/1/subscription'
        },
        (request => {request.reply({
            userId: 1,
            createdAt: "2024-11-29T01:00:00",
            topic: {
                id: 1,
                title: "Test topic",
                description: "Topic description"
            }
            
        });})
        ).as('topicSubscription');
        
        cy.contains('a.topics', 'Cliquez ici').click();
        cy.get('app-topic').should('have.lengthOf', 3);
        cy.get('span:contains("S\'abonner")').should('have.length', 3);
        cy.contains('span', "S'abonner").click();

        cy.wait('@topicSubscription');
        cy.contains("Abonnement pris en compte");

        cy.intercept(
            {
              method: 'GET',
              url: '/api/posts',
              middleware: true
            },
            (request => {request.reply(mockPosts);})
          );

        cy.contains('a.link', 'Articles').click();
        cy.contains('Créer un article');
        cy.get('app-post').should('have.lengthOf', 1);
    })
})