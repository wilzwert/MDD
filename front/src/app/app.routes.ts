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

export const routes: Routes = [
    { path: '', title: 'MDD - home',    component: HomeComponent },
    { path: 'login', title: 'Connextion', component: LoginComponent, data: {goBackToRoute: ""} },
    { path: 'register', title: 'Inscription', component: RegisterComponent, data: {goBackToRoute: ""} },
    { path: 'me', title: 'Profil utilisateur', component: MeComponent, data: {goBackToRoute: "posts"} },
    { path: 'topics', title: 'Liste des thèmes', component: TopicsListComponent },
    { path: 'posts', title: 'Liste des articles', component: PostsListComponent },
    { path: 'posts/create', title: 'Créer un nouvel article', component: PostFormComponent, data: {goBackToRoute: "posts"} },
    { path: 'posts/:id', title: 'Détail de l\'article', component: PostDetailComponent, data: {goBackToRoute: "posts"} },
    { path: '404', component: NotFoundComponent, data: {goBackToRoute: "posts"} },
    { path: '**', title: 'Page introuvable', redirectTo: '404', data: {goBackToRoute: "topics"} }
];
