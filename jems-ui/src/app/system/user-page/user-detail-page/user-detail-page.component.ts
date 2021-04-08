import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {Log} from '../../../common/utils/log';
import {Forms} from '../../../common/utils/forms';
import {take} from 'rxjs/internal/operators';
import {OutputCurrentUser, OutputUserRole, OutputUserWithRole} from '@cat/api';
import {UserDetailPageStore} from './user-detail-page-store.service';
import {Permission} from '../../../security/permissions/permission';
import {combineLatest, Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {SystemPageSidenavService} from '../../services/system-page-sidenav.service';
import {FormState} from '@common/components/forms/form-state';
import {RoutingService} from '../../../common/services/routing.service';

@Component({
  selector: 'app-user-detail-page',
  templateUrl: './user-detail-page.component.html',
  styleUrls: ['./user-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailPageComponent extends ViewEditForm {
  Permission = Permission;
  passwordIsInEditMode = false;

  details$: Observable<{
    user: OutputUserWithRole,
    currentUser: OutputCurrentUser | null,
    roles: OutputUserRole[]
  }>;

  userForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(50),
      Validators.minLength(1),
    ])],
    surname: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(50),
      Validators.minLength(1),
    ])],
    email: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(255),
      Validators.email,
    ])],
    userRoleId: ['', Validators.required]
  });

  emailErrors = {
    email: 'user.email.wrong.format'
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private router: RoutingService,
              private sidenavService: SystemPageSidenavService,
              public userStore: UserDetailPageStore,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService) {
    super(changeDetectorRef, translationService);

    this.details$ = combineLatest([
      this.userStore.user$,
      this.userStore.currentUser$,
      this.userStore.roles$
    ])
      .pipe(
        map(([user, currentUser, roles]) => ({user, currentUser, roles})),
        // TODO: remove after new edit
        tap(details => this.resetUser(details.user)),
        tap(details => {
          if (!details.user?.id) {
            this.changeFormState$.next(FormState.EDIT);
          }
        })
      );

    // TODO: remove after new edit
    this.error$ = this.userStore.userSaveError$;
    this.success$ = this.userStore.userSaveSuccess$;
  }

  // TODO: remove after new edit
  getForm(): FormGroup | null {
    return this.userForm;
  }

  onSubmit(user: OutputUserWithRole): void {
    if (!user?.id) {
      const redirectPayload = {
        state: {
          success: {
            i18nKey: 'user.detail.save.success',
          },
        }
      };
      this.userStore.createUser(this.userForm.value)
        .pipe(
          take(1),
          tap(() => this.router.navigate(['/app/system/user/'], redirectPayload)),
        ).subscribe();
      return;
    }

    if (!this.userRoleId?.value || user.userRole.id === this.userRoleId.value) {
      /**
       * Proceed to submit the form without the role change confirm if:
       * - the role did not change
       * - the form does not have a role => own user editing
       */
      Log.debug('Saving user without role confirmation', this);
      this.saveUser(user.id);
      return;
    }
    this.confirmRoleChange(user);
  }

  discard(user: OutputUserWithRole): void {
    if (!user.id) {
      this.router.navigate(['/app/system/user']);
    }
    this.changeFormState$.next(FormState.VIEW);
    this.resetUser(user);
  }

  private resetUser(user: OutputUserWithRole): void {
    this.userForm.patchValue(user || {});
    this.userRoleId?.patchValue(user?.userRole?.id);
  }

  private saveUser(userId: number): void {
    this.userStore.saveUser$.next({
      id: userId,
      ...this.userForm.value
    });
  }

  private confirmRoleChange(user: OutputUserWithRole): void {
    Forms.confirmDialog(
      this.dialog,
      'user.detail.changeRole.dialog.title',
      'user.detail.changeRole.dialog.message'
    ).pipe(
      take(1)
    ).subscribe(changeRole => {
      const selectedRole = changeRole ? this.userRoleId.value : user.userRole.id;
      this.userRoleId.patchValue(selectedRole);
      if (changeRole) {
        this.saveUser(user.id);
      }
    });
  }

  get userRoleId(): AbstractControl {
    return this.userForm.get('userRoleId') as AbstractControl;
  }
}
