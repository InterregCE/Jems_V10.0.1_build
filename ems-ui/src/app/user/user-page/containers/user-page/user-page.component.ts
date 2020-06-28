import {Component} from '@angular/core';
import {UserPageService} from '../../services/user-page/user-page.service';
import {InputUserCreate} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {UserDetailService} from '../../services/user-detail/user-detail.service';
import {RolePageService} from '../../../user-role/services/role-page/role-page.service';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss']
})
export class UserPageComponent {
  Permission = Permission;

  userList$ = this.userPageService.userList();
  userRoles$ = this.rolePageService.userRoles();
  saveSuccess$ = this.userDetailService.saveSuccess();
  saveError$ = this.userDetailService.saveError();

  constructor(private userPageService: UserPageService,
              private userDetailService: UserDetailService,
              private rolePageService: RolePageService) {
  }

  createUser(user: InputUserCreate): void {
    this.userDetailService.createUser(user);
  }
}
