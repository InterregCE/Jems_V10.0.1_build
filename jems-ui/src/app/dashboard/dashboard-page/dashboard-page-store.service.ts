import {Injectable} from '@angular/core';
import {UserDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {SecurityService} from '../../security/security.service';

@Injectable()
export class DashboardPageStore {

  currentUser$: Observable<UserDTO | null>;

  constructor(private securityService: SecurityService) {
    this.currentUser$ = this.securityService.currentUserDetails;
  }

}
