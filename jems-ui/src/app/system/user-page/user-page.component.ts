import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {SystemPageSidenavService} from '../services/system-page-sidenav.service';
import {UserPageStore} from './user-page-store.service';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Router} from '@angular/router';
import {Alert} from '@common/components/forms/alert';
import {UserDTO, UserRoleDTO, UserRoleSummaryDTO, UserSearchRequestDTO} from '@cat/api';
import {FormBuilder, FormControl} from '@angular/forms';
import {tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {BehaviorSubject} from 'rxjs';
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
  userStatus = UserSearchRequestDTO.UserStatusesEnum;

  filterForm = this.formBuilder.group({
    name: '',
    surname: '',
    email: '',
    roles: [[]],
    userStatuses: [[]]
  });
  activeFilters$ = new BehaviorSubject<UserSearchRequestDTO>(this.filterForm.value);

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

    this.activeFilters$.pipe(
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

  getRoleName(roles: UserRoleSummaryDTO[], roleId: number): string {
    return roles.find(it => it.id === roleId)?.name || '';
  }

  removeRoleFromFilters(roleId: number): void {
    const currentRoles: number[] = this.roles?.value || [];
    currentRoles.splice(currentRoles.indexOf(roleId), 1);
    this.roles.setValue([...currentRoles]);
    this.activeFilters$.next(
      {...this.activeFilters$.value, roles: this.roles.value} as UserSearchRequestDTO
    );
  }

  removeUserStatusFromFilters(status: UserDTO.UserStatusEnum): void {
    const currentUserStatuses: UserDTO.UserStatusEnum[] = this.userStatuses?.value || [];
    currentUserStatuses.splice(currentUserStatuses.indexOf(status), 1);
    this.userStatuses.setValue([...currentUserStatuses]);
    this.activeFilters$.next(
      {...this.activeFilters$.value, userStatuses: this.userStatuses.value} as UserSearchRequestDTO
    );
  }

  addRoleToFilters(roleId: number): void {
    const currentRoles: number[] = this.roles?.value || [];
    if (currentRoles.indexOf(roleId) < 0) {
      currentRoles.push(roleId);
      this.roles.setValue([...currentRoles]);
    }
    this.activeFilters$.next(
      {...this.activeFilters$.value, roles: this.roles.value} as UserSearchRequestDTO
    );
  }

  addUserStatusToFilters(userStatus: UserSearchRequestDTO.UserStatusesEnum): void {
    const currentUserStatuses: UserSearchRequestDTO.UserStatusesEnum[] = this.userStatuses?.value || [];
    if (currentUserStatuses.indexOf(userStatus) < 0) {
      currentUserStatuses.push(userStatus);
      this.userStatuses.setValue([...currentUserStatuses]);
    }
    this.activeFilters$.next(
      {...this.activeFilters$.value, userStatuses: this.userStatuses.value} as UserSearchRequestDTO
    );
  }

  resetFilter(controlName: string): void {
    this.filterForm.get(controlName)?.setValue((this.activeFilters$.value as any)[controlName]);
    this.activeFilters$.next(
      {...this.activeFilters$.value, [controlName]: undefined} as UserSearchRequestDTO
    );
    this.filterForm.get(controlName)?.enable();
  }

  updateFilter(controlName: string): void {
    const newValue = this.filterForm.get(controlName)?.value;
    if (!newValue || !newValue.trim().length) {
      return;
    }
    this.filterForm.get(controlName)?.disable();
    this.activeFilters$.next(
      {...this.activeFilters$.value, [controlName]: newValue} as UserSearchRequestDTO
    );
    this.filterForm.get(controlName)?.setValue(' ');
  }

  isThereAnyActiveFilter(): boolean {
    return this.activeFilters$.value.name?.length > 0 ||
      this.activeFilters$.value.surname?.length > 0 ||
      this.activeFilters$.value.email?.length > 0 ||
      this.activeFilters$.value.userStatuses?.length > 0 ||
      (this.activeFilters$.value.roles && this.activeFilters$.value.roles?.length > 0);
  }

  ngOnInit(): void {
    if (this.success) {
      setTimeout(() => {
        this.success = null;
        this.changeDetectorRef.markForCheck();
      },         3000);
    }
  }
}
