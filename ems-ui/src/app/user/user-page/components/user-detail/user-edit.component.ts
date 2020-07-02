import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InputUserUpdate, OutputUser, OutputUserRole} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Permission} from 'src/app/security/permissions/permission';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent extends AbstractForm implements OnInit {
  Permission = Permission;

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
    role: ['']
  });

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
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
    this.submitted = true;
    this.submitUser.emit({
      id: this.user?.id,
      email: this.user?.email,
      name: this.user?.name,
      surname: this.user?.surname,
      accountRoleId: this.userForm?.controls?.role?.value?.id || this.user.userRole.id
    });
  }
}
