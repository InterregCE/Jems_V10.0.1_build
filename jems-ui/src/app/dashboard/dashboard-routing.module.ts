import {Routes} from '@angular/router';
import {DashboardPageComponent} from './dashboard-page/dashboard-page.component';
import {ConfirmLeaveGuard} from '../security/confirm-leave.guard';

export const routes: Routes = [
  {
    path: '',
    component: DashboardPageComponent,
    canDeactivate: [ConfirmLeaveGuard],
  }
];
