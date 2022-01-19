import {ChangeDetectorRef, Component, EventEmitter, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractFormComponent} from '@common/components/forms/abstract-form.component';
import {InfoService, LoginRequest} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import {ResourceStoreService} from '@common/services/resource-store.service';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Component({
  selector: 'jems-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent extends AbstractFormComponent {

  @Output()
  submitLogin: EventEmitter<LoginRequest> = new EventEmitter<LoginRequest>();

  hide = true;
  loginForm = this.formBuilder.group({
    email: ['', Validators.required],
    password: ['', Validators.required]
  });

  registerLink = '/no-auth/register';

  largeLogo$ = this.resourceStore.largeLogo$;

  accessibilityStatementUrl$: Observable<string>;
  termsAndPrivacyPolicyUrl$: Observable<string>;
  helpdeskUrl$: Observable<string>;

  constructor(private readonly formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              public resourceStore: ResourceStoreService,
              private infoService: InfoService) {
    super(changeDetectorRef, translationService);

    this.accessibilityStatementUrl$ = this.infoService.getVersionInfo().pipe(
      map(info => info.accessibilityStatementUrl)
    );
    this.termsAndPrivacyPolicyUrl$ = this.infoService.getVersionInfo().pipe(
      map(info => info.termsAndPrivacyPolicyUrl)
    );
    this.helpdeskUrl$ = this.infoService.getVersionInfo().pipe(
      map(info => info.helpdeskUrl)
    );

  }

  onSubmit(): void {
    this.submitted = true;
    this.submitLogin.emit({
      email: this.loginForm.controls.email.value,
      password: this.loginForm.controls.password.value
    });
  }

  getForm(): FormGroup | null {
    return this.loginForm;
  }
}
