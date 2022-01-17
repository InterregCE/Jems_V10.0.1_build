import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AbstractFormComponent} from '@common/components/forms/abstract-form.component';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InfoService, UserRegistrationDTO} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import {UserPasswordComponent} from '../../../../system/user-page/user-detail-page/user-password/user-password.component';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {ResourceStoreService} from '@common/services/resource-store.service';


@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.scss']
})
export class UserRegistrationComponent extends AbstractFormComponent implements OnInit {
  @Output()
  submitUser: EventEmitter<UserRegistrationDTO> = new EventEmitter<UserRegistrationDTO>();
  @Output()
  loginRedirect: EventEmitter<null> = new EventEmitter<null>();

  largeLogo$ = this.resourceStore.largeLogo$;

  hide = true;
  clearOnSuccess = true;
  permanentSuccessAlert = true;
  areTermsAccepted = false;
  termsAndPrivacyPolicyUrl$: Observable<string>;

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
    password: ['', Validators.compose([
      Validators.required,
      Validators.pattern(UserPasswordComponent.PASSWORD_REGEX)
    ])],
    acceptTerms: ['', Validators.compose([
      Validators.requiredTrue,
    ])]
  });

  emailErrors = {
    email: 'user.email.wrong.format'
  };

  passwordErrors = {
    pattern: 'user.password.constraints.not.satisfied',
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              public resourceStore: ResourceStoreService,
              private router: Router,
              private infoService: InfoService
  ) {
    super(changeDetectorRef, translationService);
    this.termsAndPrivacyPolicyUrl$ = this.infoService.getVersionInfo().pipe(
      map(info => info.termsAndPrivacyPolicyUrl)
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  getForm(): FormGroup | null {
    return this.userForm;
  }

  onSubmit(): void {
    this.submitted = true;
    this.submitUser.emit({
      name: this.userForm?.controls?.name?.value,
      surname: this.userForm?.controls?.surname?.value,
      email: this.userForm?.controls?.email.value,
      password: this.userForm?.controls?.password?.value
    });
  }

  redirectToLogin(): void {
    this.loginRedirect.emit();
  }
}
