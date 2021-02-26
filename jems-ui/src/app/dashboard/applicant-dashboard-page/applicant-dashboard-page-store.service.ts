import {Injectable} from '@angular/core';
import {OutputUserWithRole} from '@cat/api';
import {Observable} from 'rxjs';
import {SecurityService} from '../../security/security.service';

@Injectable()
export class ApplicantDashboardPageStore {

  currentUser$: Observable<OutputUserWithRole | null>;

  constructor(private securityService: SecurityService) {
    this.currentUser$ = this.securityService.currentUserDetails;
  }

}


