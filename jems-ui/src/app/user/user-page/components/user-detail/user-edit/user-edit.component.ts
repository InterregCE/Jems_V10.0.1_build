import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {InputUserUpdate, OutputUserRole, OutputUserWithRole} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Permission} from 'src/app/security/permissions/permission';
import {Forms} from '../../../../../common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {take, takeUntil} from 'rxjs/operators';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {Log} from '../../../../../common/utils/log';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserEditComponent extends ViewEditForm {
  Permission = Permission;

  @Input()
  userRoles: OutputUserRole[];
  @Input()
  user: OutputUserWithRole;
  @Input()
  disabled = false;
  @Output()
  submitUser: EventEmitter<InputUserUpdate> = new EventEmitter<InputUserUpdate>();

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
    role: ['']
  });

  nameErrors = {
    maxlength: 'user.name.wrong.size',
    minlength: 'user.name.wrong.size',
    required: 'user.name.wrong.size',
  };

  surnameErrors = {
    maxlength: 'user.surname.wrong.size',
    minlength: 'user.surname.wrong.size',
    required: 'user.surname.wrong.size'
  };

  emailErrors = {
    required: 'user.email.should.not.be.empty',
    maxlength: 'user.email.wrong.size',
    email: 'user.email.wrong.format'
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  getForm(): FormGroup | null {
    return this.userForm;
  }

  onSubmit(): void {
    if (!this.userForm?.controls?.role.value.id
      || this.user?.userRole?.id === this.userForm.controls.role.value.id) {
      /**
       * Proceed to submit the form without the role change confirm if:
       * - the role did not change
       * - the form does not have a role => own user editing
       */
      Log.debug('Saving user without role confirmation', this);
      this.saveUser();
      return;
    }
    this.confirmRoleChange();
  }

  enterViewMode(): void {
    this.userForm.controls.name.setValue(this.user.name);
    this.userForm.controls.surname.setValue(this.user.surname);
    this.userForm.controls.email.setValue(this.user.email);
    this.userForm.controls.role.setValue(this.getUserRole());
  }

  private saveUser(): void {
    this.submitted = true;

    this.submitUser.emit({
      id: this.user?.id,
      name: this.userForm?.controls?.name?.value,
      surname: this.userForm?.controls?.surname?.value,
      email: this.userForm?.controls?.email?.value,
      userRoleId: this.userForm?.controls?.role?.value?.id || this.user?.userRole?.id
    });
  }

  private confirmRoleChange(): void {
    Forms.confirmDialog(
      this.dialog,
      'user.detail.changeRole.dialog.title',
      'user.detail.changeRole.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$)
    ).subscribe(changeRole => {
      const selectedRole = changeRole
        ? this.userForm?.controls?.role.value
        : this.getUserRole();
      this.userForm?.controls?.role.setValue(selectedRole);
      if (changeRole) {
        this.saveUser();
      }
    });
  }

  getUserRole(): OutputUserRole | undefined {
    return this.userRoles.find(role => role.id === this.user.userRole.id);
  }
}
