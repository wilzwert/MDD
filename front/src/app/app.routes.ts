import { Routes } from '@angular/router';
import { NotFoundComponent } from './layout/not-found/not-found.component';
import { HomeComponent } from './layout/home/home.component';
import { LoginComponent } from './features/login/login.component';
import { MeComponent } from './features/me/me.component';
import { RegisterComponent } from './features/register/register.component';
import { TopicsListComponent } from './features/topics/list/topics-list/topics-list.component';

export const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'me', component: MeComponent},
    {path: 'topics', component: TopicsListComponent},
    {path: '404', component: NotFoundComponent},
    {path: '**', redirectTo: '404'}
];
