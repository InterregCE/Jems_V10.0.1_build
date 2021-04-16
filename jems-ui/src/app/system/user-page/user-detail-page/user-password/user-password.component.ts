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
import {filter, take, takeUntil} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {TranslateService} from '@ngx-translate/core';
import {FormState} from '@common/components/forms/form-state';
import {UserDetailPageStore} from '../user-detail-page-store.service';
import {Forms} from '../../../../common/utils/forms';

@Component({
  selector: 'app-user-password',
  templateUrl: './user-password.component.html',
  styleUrls: ['./user-password.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPasswordComponent extends ViewEditForm implements OnInit {
  // password should have: at least 10 characters, one upper case letter, one lower case letter and one digit
  static PASSWORD_REGEX = new RegExp('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{10,})');

  @Input()
  ownUser: boolean;
  @Input()
  disabled: boolean;
  @Input()
  userId: number;
  @Output()
  passwordFormState: EventEmitter<FormState> = new EventEmitter<FormState>();

  clearOnSuccess = true;
  passwordForm: FormGroup;

  passwordErrors = {
    pattern: 'user.password.constraints.not.satisfied'
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private userStore: UserDetailPageStore,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService) {
    super(changeDetectorRef, translationService);
  }

  ngOnInit(): void {
    const controls: { [key: string]: any } = {
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(10),
        Validators.pattern(UserPasswordComponent.PASSWORD_REGEX)
      ])]
    };
    if (this.ownUser) {
      controls.oldPassword = ['', Validators.compose([
        Validators.required
      ])];
    }
    this.passwordForm = this.formBuilder.group(controls);
    super.ngOnInit();
  }

  getForm(): FormGroup | null {
    return this.passwordForm;
  }

  onSubmit(): void {
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
    this.userStore.changePassword(!this.ownUser ? this.userId : null as any, this.passwordForm.value)
      .pipe(
        take(1),
      ).subscribe();
  }

  enterEditMode(): void {
    this.passwordForm.reset();
  }

}
