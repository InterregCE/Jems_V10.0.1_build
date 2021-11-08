import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {SystemPageSidenavService} from '../services/system-page-sidenav.service';
import {UserPageStore} from './user-page-store.service';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Router} from '@angular/router';
import {Alert} from '@common/components/forms/alert';
import {UserRoleDTO, UserSearchRequestDTO} from '@cat/api';
import {FormBuilder, FormControl} from '@angular/forms';
import {map, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Observable} from 'rxjs';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss'],
  providers: [UserPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPageComponent implements OnInit {

  PermissionsEnum = PermissionsEnum;
  success = this.router.getCurrentNavigation()?.extras?.state?.success;
  Alert = Alert;

  filterForm = this.formBuilder.group({
    name: '',
    surname: '',
    email: '',
    roles: [[]],
    userStatuses: [[]]
  });

  data$: Observable<{
    roles: Map<number, string>,
    userStatuses: Map<UserSearchRequestDTO.UserStatusesEnum, string>
  }>;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/app/system/user/detail',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'user.table.column.name.id',
        elementProperty: 'id',
        sortProperty: 'id'
      },
      {
        displayedColumn: 'user.table.column.name.name',
        elementProperty: 'name',
        sortProperty: 'name'
      },
      {
        displayedColumn: 'user.table.column.name.surname',
        elementProperty: 'surname',
        sortProperty: 'surname'
      },
      {
        displayedColumn: 'user.table.column.name.email',
        elementProperty: 'email',
        sortProperty: 'email'
      },
      {
        displayedColumn: 'user.table.column.name.role',
        elementProperty: 'userRole.name',
        sortProperty: 'userRole.name'
      },
      {
        displayedColumn: 'user.detail.field.status',
        elementProperty: 'userStatus',
        elementTranslationKey: 'user.status',
        sortProperty: 'userStatus'
      }
    ]
  });

  constructor(public userPageStore: UserPageStore,
              private router: Router,
              private changeDetectorRef: ChangeDetectorRef,
              private formBuilder: FormBuilder,
              private systemPageSidenavService: SystemPageSidenavService) {
    const userStatuses = new Map<UserSearchRequestDTO.UserStatusesEnum, string>([
      [UserSearchRequestDTO.UserStatusesEnum.ACTIVE, 'user.status.ACTIVE'],
      [UserSearchRequestDTO.UserStatusesEnum.INACTIVE, 'user.status.INACTIVE'],
      [UserSearchRequestDTO.UserStatusesEnum.UNCONFIRMED, 'user.status.UNCONFIRMED']
    ]);

    this.data$ = this.userPageStore.roles$
      .pipe(
        map(roles => ({
          roles: new Map(roles.map(role => [role.id, role.name])),
          userStatuses
        }))
      );

    this.filterForm.valueChanges.pipe(
      tap(filters => this.userPageStore.updateUserList(filters)),
      untilDestroyed(this)
    ).subscribe();
  }

  get roles(): FormControl {
    return this.filterForm.get('roles') as FormControl;
  }

  get userStatuses(): FormControl {
    return this.filterForm.get('userStatuses') as FormControl;
  }

  isThereAnyActiveFilter(): boolean {
    return this.filterForm.value.name?.length > 0 ||
      this.filterForm.value.surname?.length > 0 ||
      this.filterForm.value.email?.length > 0 ||
      this.filterForm.value.userStatuses?.length > 0 ||
      this.filterForm.value.roles?.length;
  }

  ngOnInit(): void {
    if (this.success) {
      setTimeout(
        () => {
          this.success = null;
          this.changeDetectorRef.markForCheck();
        },
        3000);
    }
  }
}
