import { Routes } from '@angular/router';
import { NotFoundComponent } from './layout/not-found/not-found.component';
import { HomeComponent } from './layout/home/home.component';

export const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: '404', component: NotFoundComponent},
    {path: '**', redirectTo: '404'}
];
