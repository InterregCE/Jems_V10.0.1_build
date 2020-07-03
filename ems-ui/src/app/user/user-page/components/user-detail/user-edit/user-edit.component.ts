import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {InputUserUpdate, OutputUser, OutputUserRole} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Permission} from 'src/app/security/permissions/permission';
import {Forms} from '../../../../../common/utils/forms';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserEditComponent extends AbstractForm implements OnInit {
  Permission = Permission;

  @Input()
  userRoles: OutputUserRole[];
  @Input()
  user: OutputUser;
  @Output()
  submitUser: EventEmitter<InputUserUpdate> = new EventEmitter<InputUserUpdate>();

  editMode = false;

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
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.userForm.controls.name.setValue(this.user.name);
    this.userForm.controls.surname.setValue(this.user.surname);
    this.userForm.controls.email.setValue(this.user.email);
    this.enterViewMode();
  }

  getForm(): FormGroup | null {
    return this.userForm;
  }

  onSubmit(): void {
    this.submitted = true;
    this.submitUser.emit({
      id: this.user?.id,
      name: this.userForm?.controls?.name?.value,
      surname: this.userForm?.controls?.surname?.value,
      email: this.userForm?.controls?.email?.value,
      accountRoleId: this.userForm?.controls?.role?.value?.id || this.user.userRole.id
    });
  }

  enterEditMode(): void {
    this.editMode = true;
    Forms.enableControls(this.userForm);
  }

  enterViewMode(): void {
    this.editMode = false;

    this.userForm.controls.name.setValue(this.user.name);
    this.userForm.controls.surname.setValue(this.user.surname);
    this.userForm.controls.email.setValue(this.user.email);

    Forms.disableControls(this.userForm);
  }
}
