import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProjectApplicationComponent} from './components/project-application/project-application.component';
import {ProjectApplicationDetailComponent} from './components/project-application/project-application-detail/project-application-detail.component';


const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: ProjectApplicationComponent,
    // canActivate: [AuthenticationGuard]
  },
  {
    path: 'project/:projectId',
    component: ProjectApplicationDetailComponent,
  }
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
