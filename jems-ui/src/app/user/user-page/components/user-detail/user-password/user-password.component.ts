import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Forms} from '../../../../../common/utils/forms';
import {InputPassword} from '@cat/api';
import {filter, take, takeUntil} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {ViewEditForm} from '@common/components/forms/view-edit-form';

@Component({
  selector: 'app-user-password',
  templateUrl: './user-password.component.html',
  styleUrls: ['./user-password.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPasswordComponent extends ViewEditForm implements OnInit {

  @Input()
  ownUser: boolean;
  @Input()
  disabled: boolean;
  @Output()
  submitPassword: EventEmitter<InputPassword> = new EventEmitter<InputPassword>();

  clearOnSuccess = true;
  passwordForm: FormGroup;

  passwordErrors = {
    required: 'user.password.should.not.be.empty',
    minlength: 'user.password.wrong.size',
  };

  oldPasswordErrors = {
    required: 'user.password.should.not.be.empty',
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    const controls: { [key: string]: any } = {
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(10),
      ])]
    };
    if (this.ownUser) {
      controls.oldPassword = ['', Validators.compose([
        Validators.required
      ])]
    }
    this.passwordForm = this.formBuilder.group(controls);

    super.ngOnInit();
  }

  getForm(): FormGroup | null {
    return this.passwordForm;
  }

  onSubmit() {
    if (!this.ownUser) {
      // admin is editing the password, no confirmation
      this.savePassword();
      return;
    }
    Forms.confirmDialog(
      this.dialog,
      'user.detail.changePassword.dialog.title',
      'user.detail.changePassword.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(changePwd => !!changePwd)
    ).subscribe(() => {
      this.savePassword();
    });
  }

  private savePassword(): void {
    this.submitted = true;
    this.submitPassword.emit({
      password: this.passwordForm?.controls?.password?.value,
      oldPassword: this.passwordForm?.controls?.oldPassword?.value
    })
  }

  enterEditMode(): void {
    this.passwordForm?.controls?.password?.setValue('');
    this.passwordForm?.controls?.oldPassword?.setValue('');
  }

}
