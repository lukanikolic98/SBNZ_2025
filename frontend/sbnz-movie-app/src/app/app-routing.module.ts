import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { MoviePageComponent } from './pages/movie-page/movie-page.component';

const routes: Routes = [
  { path: '', component: HomePageComponent }, // home page
  { path: 'login', component: LoginPageComponent }, // login page
  { path: 'movie/:id', component: MoviePageComponent }, // movie detail with id param
  { path: '**', redirectTo: '', pathMatch: 'full' }, // fallback -> home
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
