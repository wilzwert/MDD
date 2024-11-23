import { Routes } from '@angular/router';
import { NotFoundComponent } from './layout/not-found/not-found.component';
import { HomeComponent } from './layout/home/home.component';
import { LoginComponent } from './features/login/login.component';
import { MeComponent } from './features/me/me.component';
import { RegisterComponent } from './features/register/register.component';
import { TopicsListComponent } from './features/topics/list/topics-list/topics-list.component';
import { PostsListComponent } from './features/posts/list/posts-list/posts-list.component';
import { PostDetailComponent } from './features/posts/post-detail/post-detail.component';
import { PostFormComponent } from './features/posts/post-form/post-form.component';
import { UnauthGuard } from './core/guards/unauth.guard';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { 
        path: '', 
        canActivate: [UnauthGuard],
        component: HomeComponent,
        title: 'MDD - home' 
    },
    { 
        path: 'login',
        canActivate: [UnauthGuard], 
        component: LoginComponent,
        title: 'Connextion',
        data: {goBackToRoute: ""} 
    },
    { 
        path: 'register',
        canActivate: [UnauthGuard],
        component: RegisterComponent, 
        title: 'Inscription', 
        data: {goBackToRoute: ""} 
    },
    { 
        path: 'me', 
        canActivate: [AuthGuard],
        component: MeComponent, 
        title: 'Profil utilisateur',
        data: {goBackToRoute: "posts"} 
    },
    { 
        path: 'topics', 
        canActivate: [AuthGuard],
        component: TopicsListComponent,
        title: 'Liste des thèmes'
    },
    { 
        path: 'posts', 
        canActivate: [AuthGuard],
        component: PostsListComponent,
        title: 'Liste des articles'
    },
    { 
        path: 'posts/create', 
        canActivate: [AuthGuard],
        component: PostFormComponent, 
        title: 'Créer un nouvel article',
        data: {goBackToRoute: "posts"} 
    },
    { 
        path: 'posts/:id', 
        canActivate: [AuthGuard],
        component: PostDetailComponent, 
        title: 'Détail de l\'article',
        data: {goBackToRoute: "posts"} 
    },
    { 
        path: '404', 
        component: NotFoundComponent, 
        data: {goBackToRoute: ""} 
    },
    { 
        path: '**', 
        title: 'Page introuvable', 
        redirectTo: '404'
    }
];
