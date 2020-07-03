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
import {InputPassword} from '@cat/api'
import {Forms} from '../../../../../common/utils/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';

@Component({
  selector: 'app-user-password',
  templateUrl: './user-password.component.html',
  styleUrls: ['./user-password.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPasswordComponent extends AbstractForm implements OnInit {

  @Input()
  userId: number;
  @Input()
  ownUser: boolean
  @Output()
  submitPassword: EventEmitter<InputPassword> = new EventEmitter<InputPassword>();

  editMode = false;
  passwordForm: FormGroup;

  passwordErrors = {
    required: 'user.password.should.not.be.empty',
    minlength: 'user.password.wrong.size',
  };

  oldPasswordErrors = {
    required: 'user.password.should.not.be.empty',
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
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
    this.enterViewMode();
  }

  getForm(): FormGroup | null {
    return this.passwordForm;
  }

  onSubmit() {
    this.submitted = true;
    this.submitPassword.emit({
      password: this.passwordForm?.controls?.password?.value,
      oldPassword: this.passwordForm?.controls?.oldPassword?.value
    })
  }

  enterViewMode(): void {
    this.editMode = false;

    this.passwordForm?.controls?.password?.setValue('******');
    this.passwordForm?.controls?.oldPassword?.setValue('******');

    Forms.disableControls(this.passwordForm)
  }

  enterEditMode(): void {
    this.editMode = true;

    this.passwordForm?.controls?.password?.setValue('');
    this.passwordForm?.controls?.oldPassword?.setValue('');

    Forms.enableControls(this.passwordForm)
  }

}
