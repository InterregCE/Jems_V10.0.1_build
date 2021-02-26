import {Routes} from '@angular/router';
import {DefaultPageGuard} from '../common/guards/default-page.guard';
import {ApplicantDashboardPageComponent} from './applicant-dashboard-page/applicant-dashboard-page.component';

export const routes: Routes = [
  {
    path: '',
    component: ApplicantDashboardPageComponent,
    canActivate: [DefaultPageGuard],
    // data: {dynamicBreadcrumb: true},
    // resolve: {breadcrumb$: HomeBreadcrumbResolver},
    // skipBreadcrumb: true,
    // data: {
    //   breadcrumb: 'user.breadcrumb.create',
    //   dynamicBreadcrumb: true,
    //   resolve: {breadcrumb$: HomeBreadcrumbResolver},
    // },
    // breadcrumb: 'user.breadcrumb.create',

    // children: [
    //   {
    //     path: '',
    //     // component: UserPageComponent,
    //   },
    // ]
  }
];
