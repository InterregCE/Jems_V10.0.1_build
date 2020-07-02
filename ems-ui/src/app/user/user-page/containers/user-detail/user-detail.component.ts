import {Component} from '@angular/core';
import {Observable} from 'rxjs';
import {InputUserUpdate, OutputUser, OutputUserRole} from '@cat/api';
import {UserDetailService} from '../../services/user-detail/user-detail.service';
import {ActivatedRoute} from '@angular/router';
import {RolePageService} from '../../../user-role/services/role-page/role-page.service';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.scss']
})
export class UserDetailComponent {

  userRoles$: Observable<OutputUserRole[]> = this.rolePageService.userRoles();
  id = this.activatedRoute?.snapshot?.params?.userId;
  user$: Observable<OutputUser> = this.userDetailService.getById(this.id);
  saveSuccess$ = this.userDetailService.saveSuccess();
  saveError$ = this.userDetailService.saveError();

  constructor(private userDetailService: UserDetailService,
              private rolePageService: RolePageService,
              private activatedRoute: ActivatedRoute) {
  }

  updateUser(user: InputUserUpdate): void {
    this.userDetailService.updateUser(user);
  }

}
