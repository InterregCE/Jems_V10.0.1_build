import {Routes} from '@angular/router';
import {UserDetailComponent} from '../user/user-page/containers/user-detail/user-detail.component';
import {CurrentUserResolver} from './services/current-user.resolver';

export const routes: Routes = [
  {
    path: '',
    component: UserDetailComponent,
    data: { breadcrumb: 'user.breadcrumb.your.profile' },
    resolve: {userId: CurrentUserResolver}
  }
];
