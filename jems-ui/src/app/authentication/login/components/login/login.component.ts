import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {LoginRequest} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import {ResourceStoreService} from '@common/services/resource-store.service';
import {InfoService} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {untilDestroyed} from '@ngneat/until-destroy';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent extends AbstractForm {

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

  constructor(private readonly formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              public resourceStore: ResourceStoreService,
              private infoService: InfoService) {
    super(changeDetectorRef, translationService);
    this.accessibilityStatementUrl$ = this.infoService.getVersionInfo().pipe(
      map(info => info.accessibilityStatementUrl)
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
