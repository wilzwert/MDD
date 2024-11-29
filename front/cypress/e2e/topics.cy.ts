describe("Topics list", () => {
    let mockTopics: Array<Object>;
    let mockSubscriptions: Array<Object>;
    
    before(() => {
        cy.fixture('topics.json').as('topicsJson').then(json => {mockTopics = json;});
        cy.fixture('subscriptions.json').as('subscriptionsJson').then(json => {mockSubscriptions = json;});
    })
    
    it('should redirect to login if not logged in', () => {
        cy.visit('/topics');
        // precisely match homepage url
        cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
    })
    
    it('should display topics list and allow subscription and unsubscription', () => {
      
      cy.intercept(
        {
          method: 'GET',
          url: '/api/user/me/subscriptions',
          middleware: true
        },
        (request => {request.reply(mockSubscriptions);})
      ).as('subscriptions');

      cy.login();

      cy.intercept(
        {
          method: 'GET',
          url: '/api/topics'
        },
        (request => {request.reply(mockTopics);})
      ).as('topics');

      cy.contains('a.link', 'Thèmes').click();
      cy.wait('@topics');
      cy.wait('@subscriptions');
      
      cy.get('app-topic').should('have.lengthOf', 3);
      cy.get('span:contains("S\'abonner")').should('have.length', 1);
      cy.get('span:contains("Se désabonner")').should('have.length', 2);

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
      cy.get('span:contains("S\'abonner")').should('have.length', 2);
      cy.get('span:contains("Se désabonner")').should('have.length', 1);

      cy.intercept(
        {
            method: 'POST',
            url: '/api/topics/3/subscription'
        },
        (request => {request.reply({
            userId: 1,
            createdAt: "2024-11-29T01:00:00",
            topic: mockTopics[2]
        });})
        ).as('topicSubscription');
      cy.get('span:contains("S\'abonner")').eq(1).click();
      cy.wait('@topicSubscription');
      cy.contains('Abonnement pris en compte');
      cy.get('span:contains("S\'abonner")').should('have.length', 1);
      cy.get('span:contains("Se désabonner")').should('have.length', 2);
    })
    /*
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
        cy.get('app-topic').should('have.lengthOf', 2);
        cy.get('span:contains("S\'abonner")').should('have.length', 2);
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
    })*/
})