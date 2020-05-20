import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProjectApplicationComponent} from './components/project-application/project-application.component';


const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: ProjectApplicationComponent,
    // canActivate: [AuthenticationGuard]
  },
  // {
  //   path: 'login',
  //   component: LoginComponent,
  // }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
