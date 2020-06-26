import {Component} from '@angular/core';
import {UserPageService} from '../../services/user-page/user-page.service';
import {Observable} from 'rxjs';
import {OutputUser, InputUser, OutputUserRole} from '@cat/api';
import {I18nValidationError} from '@common/validation/i18n-validation-error';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss']
})
export class UserPageComponent {
  filtered$: Observable<OutputUser[]>;
  saveSuccess$: Observable<boolean>;
  saveError$: Observable<I18nValidationError | null>;
  userRoles$: Observable<OutputUserRole[]>

  constructor(private userPageService: UserPageService) {
    this.filtered$ = this.userPageService.filtered();
    this.saveSuccess$ = this.userPageService.saveSuccess();
    this.saveError$ = this.userPageService.saveError();
    this.userRoles$ = this.userPageService.userRoles();
    this.userPageService.newPage(0, 100, 'id,desc');
  }

  saveUser(user:InputUser): void {
    this.userPageService.saveUser(user);
  }
}
