import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {SecurityService} from '../../security/security.service';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {

  loginForm = this.formBuilder.group({
    email: ['', Validators.required],
    password: ['', Validators.required]
  });
  returnUrl = false;
  authenticationError: Observable<I18nValidationError | null> = this.securityService.authenticationError;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private securityService: SecurityService) {
  }

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
  }

  onSubmit() {
    this.securityService.login({
      email: this.loginForm.controls.email.value,
      password: this.loginForm.controls.password.value
    }).subscribe(login => this.router.navigate([this.returnUrl]));
  }
}
