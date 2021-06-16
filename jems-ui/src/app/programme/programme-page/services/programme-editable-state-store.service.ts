import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ProgrammeDataService, UserRoleCreateDTO} from '@cat/api';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable()
export class ProgrammeEditableStateStore {
  isProgrammeEditableDependingOnCall$: Observable<boolean>;
  hasOnlyViewPermission$: Observable<boolean>;
  hasEditPermission$: Observable<boolean>;

  firstCallPublished$ = new Subject<void>();

  constructor(
    private programmeDataService: ProgrammeDataService,
    private permissionService: PermissionService,
  ) {
    this.isProgrammeEditableDependingOnCall$ = this.isProgrammeEditable();
    this.hasOnlyViewPermission$ = this.hasUserOnlyViewPermission();
    this.hasEditPermission$ = this.hasUserEditPermission();
  }

  private isProgrammeEditable(): Observable<boolean> {
    return this.firstCallPublished$
      .pipe(
        startWith(null),
        switchMap(() => this.programmeDataService.isProgrammeSetupLocked()),
        tap(flag => Log.info('Fetched programme is locked:', flag)),
        shareReplay(1),
      );
  }

  private hasUserOnlyViewPermission(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.ProgrammeSetupUpdate)
      .pipe(
        map(hasUpdate => !hasUpdate),
        shareReplay(1),
      );
  }

  private hasUserEditPermission(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.ProgrammeSetupUpdate)
      .pipe(
        shareReplay(1),
      );
  }
}
