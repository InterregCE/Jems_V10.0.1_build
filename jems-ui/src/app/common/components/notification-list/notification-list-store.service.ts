import {Injectable} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {CallService, NotificationService, PageCallDTO, PageNotificationDTO, UserRoleDTO} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {PermissionService} from '../../../security/permissions/permission.service';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable()
export class NotificationListStoreService {

  notificationPage$: Observable<PageNotificationDTO>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private notificationService: NotificationService,
              private permissionService: PermissionService) {
    this.notificationPage$ = this.notificationPage();
  }

  private notificationPage(): Observable<PageNotificationDTO> {
    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.notificationService.getMyNotifications(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched notifications:', this, page.content)),
      );
  }
}
