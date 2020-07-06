import { Injectable } from '@angular/core';
import {map, shareReplay, tap} from 'rxjs/operators';
import {UserRoleService, OutputUserRole} from '@cat/api';
import {Observable} from 'rxjs';
import {Log} from '../../../../common/utils/log';

@Injectable()
export class RolePageService {

  private userRoles$ = this.userRoleService.list()
    .pipe(
      tap(page => Log.info('Fetched the user roles:', this, page.content)),
      map(page => page.content),
      shareReplay(1)
    );

  constructor(private userRoleService: UserRoleService) {}

  /**
   * Returns a shared list of user roles observable.
   * The last fetched list is emitted to all/late subscribers - shareReplay(1).
   * The list is refreshed when:
   * - TBA
   */
  userRoles(): Observable<OutputUserRole[]> {
    return this.userRoles$;
  }
}
