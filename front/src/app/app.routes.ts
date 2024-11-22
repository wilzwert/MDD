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
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'me', component: MeComponent },
    { path: 'topics', component: TopicsListComponent },
    { path: 'posts', component: PostsListComponent },
    { path: 'posts/create', title: 'Create a post', component: PostFormComponent },
    { path: 'posts/:id', title: 'Posts - detail', component: PostDetailComponent },
    { path: 'posts/:id/update', title: 'Post - update', component: PostFormComponent },
    { path: '404', component: NotFoundComponent },
    { path: '**', redirectTo: '404' }
];
