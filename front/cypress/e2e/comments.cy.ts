describe("Comments on a post detail page", () => {
    let mockPosts: Array<Object>;
    let mockComments: Array<Object>;
    
    before(() => {
        cy.fixture('posts.json').as('postsJson').then(json => {mockPosts = json;});
        cy.fixture('comments.json').as('commentsJson').then(json => {mockComments = json;});
    })

    it('should display post detail and comments', () => {
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
            (request => {request.reply(mockComments);})
        ).as('getComments');

        cy.visit('/posts/1');
        cy.wait('@getPost');
        cy.wait('@getComments');

        // back button
        cy.get('app-back-button mat-icon[data-mat-icon-name="arrow-left"]').should('exist');
        

        // comments (2 comments expected)
        cy.contains('h2', 'Commentaires');
        cy.get('.element-comment').should('have.lengthOf', 2);
        cy.contains(mockComments[0].content);
        cy.contains(mockComments[0].author.username);
        cy.contains(mockComments[1].content);
        cy.contains(mockComments[1].author.username);

        cy.get('form.comment-form').should('have.lengthOf', 1);
    })

    it('should display post detail and allow commenting', () => {
        cy.login();

        cy.intercept(
            {
                method: 'GET',
                url: '/api/posts/1'
            },
            (request => request.reply(mockPosts[0]))
        ).as('getPost');
        cy.intercept(
            {
                method: 'GET',
                url: '/api/posts/1/comments'
            },
            (request => request.reply(mockComments))
        ).as('getComments');

        cy.visit('/posts/1');
        cy.wait('@getPost');
        cy.wait('@getComments');

        // back button
        cy.get('mat-icon[data-mat-icon-name="arrow-left"]').should('exist');

        // 2 comments
        cy.get('.element-comment').should('have.lengthOf', 2);
        
        cy.get('form.comment-form').should('have.lengthOf', 1);
        // hint
        cy.contains('Votre commentaire doit comporter au moins 5 caractères');
        // by default, textarea is empty and invalid
        cy.get('[formControlName="content"]').should('have.class', 'ng-invalid');
        cy.get('mat-hint mat-icon').should('have.class', 'input-invalid');
        cy.get('button[type=submit]').should('be.disabled');

        // type a valid comment content
        cy.get('[formControlName="content"]').type('Comment');
        cy.get('[formControlName="content"]').should('have.class', 'ng-valid');
        cy.get('mat-hint mat-icon').should('have.class', 'input-valid');

        cy.intercept(
            {
                method: 'POST',
                url: '/api/posts/1/comments'
            },
            (request => request.reply({
                id: 3,
                author: {id: 1, username: 'testuser'},
                postId: 1,
                content: 'Comment',
                createdAt: '2024-11-30T10:00:00',
                updatedAt: '2024-11-30T10:00:00'
            }))
        ).as('createComment');

        cy.get('button[type=submit]').should('not.be.disabled').click();
        cy.wait('@createComment');

        cy.contains('Merci pour votre commentaire');
        // we now have 3 comments
        cy.get('.element-comment').should('have.lengthOf', 3);
        // 2 comments from testuser
        cy.get('.comment-author:contains("testuser")').should('have.lengthOf', 2);
        // first comment should be the newly created one
        cy.contains('.comment-author', 'testuser').parent().contains('Comment');

        // textarea should be empty and invalid again
        cy.get('[formControlName="content"]').should('have.class', 'ng-invalid');
        cy.get('mat-hint mat-icon').should('have.class', 'input-invalid');
        cy.get('button[type=submit]').should('be.disabled');
    })
})