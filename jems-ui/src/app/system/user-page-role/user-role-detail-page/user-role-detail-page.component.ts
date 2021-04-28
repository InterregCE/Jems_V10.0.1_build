import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {Log} from '../../../common/utils/log';
import {take} from 'rxjs/internal/operators';
import {UserRoleCreateDTO, UserRoleDTO} from '@cat/api';
import {Observable, of} from 'rxjs';
import {tap} from 'rxjs/operators';
import {SystemPageSidenavService} from '../../services/system-page-sidenav.service';
import {FormState} from '@common/components/forms/form-state';
import {RoutingService} from '../../../common/services/routing.service';
import {UserRoleDetailPageStore} from './user-role-detail-page-store.service';
import {ActivatedRoute} from '@angular/router';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-user-role-detail-page',
  templateUrl: './user-role-detail-page.component.html',
  styleUrls: ['./user-role-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserRoleDetailPageComponent extends ViewEditForm {

  PERMISSIONS = UserRoleDTO.PermissionsEnum;
  roleId = this.activatedRoute?.snapshot?.params?.roleId;

  details$: Observable<UserRoleDTO> = (this.roleId ? this.roleStore.userRole$ : of({} as UserRoleDTO))
    .pipe(
      tap(role => this.resetUserRole(role)),
      tap(role => {
        if (!role.id) {
          this.changeFormState$.next(FormState.EDIT);
        }
      })
    );

  userRoleForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(50),
      Validators.minLength(1),
    ])],
    permissions: this.formBuilder.group(this.generateCleanPermissions()),
  });

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private activatedRoute: ActivatedRoute,
              private router: RoutingService,
              private sidenavService: SystemPageSidenavService,
              public roleStore: UserRoleDetailPageStore,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService) {
    super(changeDetectorRef, translationService);

    this.error$ = this.roleStore.userRoleSaveError$;
    this.success$ = this.roleStore.userRoleSaveSuccess$;
  }

  getForm(): FormGroup | null {
    return this.userRoleForm;
  }

  onSubmit(role: UserRoleDTO): void {
    if (!role?.id) {
      const redirectPayload = {
        state: {
          success: {
            i18nKey: 'userRole.detail.save.success',
          },
        }
      };
      this.roleStore.createUserRole(this.extractPayloadFromForm())
        .pipe(
          take(1),
          tap(() => this.router.navigate(['/app/system/userRole/'], redirectPayload)),
        ).subscribe();
      return;
    }

    Log.debug('Saving user role', this);
    this.saveUserRole(role.id);
  }

  discard(role: UserRoleDTO): void {
    if (role.id) {
      this.changeFormState$.next(FormState.VIEW);
      this.resetUserRole(role);
    } else {
      this.router.navigate(['/app/system/userRole']);
    }
  }

  resetUserRole(role: UserRoleDTO): void {
    this.formName?.patchValue(role?.name);
    this.formPermissions.patchValue(this.generateCleanPermissions());
    (role?.permissions || []).forEach(perm => {
      this.formPermissions?.get(perm)?.patchValue(true);
    });
  }

  private saveUserRole(roleId: number): void {
    this.roleStore.saveUserRole$.next({
      id: roleId,
      ...this.extractPayloadFromForm(),
    });
  }

  private extractPayloadFromForm(): UserRoleCreateDTO {
    const permissionsObject: any = this.formPermissions?.value;
    Object.keys(permissionsObject).filter(perm => permissionsObject[perm]);
    return {
      name: this.formName?.value,
      permissions: Object.keys(permissionsObject).filter(perm => permissionsObject[perm]) as PermissionsEnum[],
    };
  }

  get formName(): FormControl {
    return this.userRoleForm.get('name') as FormControl;
  }

  get formPermissions(): FormGroup {
    return this.userRoleForm.get('permissions') as FormGroup;
  }

  private generateCleanPermissions(): any {
    return Object.keys(UserRoleDTO.PermissionsEnum)
      .reduce((ac: any, a: string) => ({...ac, [a]: false}), {});
  }

}
