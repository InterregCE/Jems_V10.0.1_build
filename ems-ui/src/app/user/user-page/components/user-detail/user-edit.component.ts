import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InputUserUpdate, OutputUser, OutputUserRole} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {take, takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent extends AbstractForm implements OnInit {

  @Input()
  success: boolean;
  @Input()
  userRoles: OutputUserRole[];
  @Input()
  user: OutputUser;
  @Output()
  submitUser: EventEmitter<InputUserUpdate> = new EventEmitter<InputUserUpdate>();

  userForm = this.formBuilder.group({
    name: [''],
    surname: [''],
    email: [''],
    role: ['', Validators.required]
  });

  roleErrors = {
    required: 'user.accountRoleId.should.not.be.empty'
  };

  private selectedRole: OutputUserRole | undefined;

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.selectedRole = this.userRoles.find(role => role.id === this.user.userRole.id);
    this.userForm.controls.role.setValue(this.selectedRole);
    // TODO enable the fields and add validators
    this.userForm.controls.name.setValue(this.user.name);
    this.userForm.controls.name.disable();
    this.userForm.controls.surname.setValue(this.user.surname);
    this.userForm.controls.surname.disable();
    this.userForm.controls.email.setValue(this.user.email);
    this.userForm.controls.email.disable();

  }

  getForm(): FormGroup | null {
    return this.userForm;
  }

  onSubmit(): void {
    this.submitUser.emit({
      id: this.user?.id,
      email: this.user?.email,
      name: this.user?.name,
      surname: this.user?.surname,
      accountRoleId: this.userForm?.controls?.role?.value?.id
    });
  }

  roleChanged(): void {

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '30rem',
      data: {
        title: 'user.detail.changeRole.dialog.title',
        message: 'user.detail.changeRole.dialog.message'
      }
    });

    dialogRef.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed$)
      )
      .subscribe(dialogResult => {
        this.selectedRole = dialogResult ? this.userForm?.controls?.role?.value : this.selectedRole
        this.userForm.controls.role.setValue(this.selectedRole);
      });
  }

}
