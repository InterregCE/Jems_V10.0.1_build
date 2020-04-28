import {Component} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {SecurityService} from '../../security/security.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: 'login.component.html'
})

export class LoginComponent {

  loginForm = this.formBuilder.group({
    username: ['', Validators.required]
  });

  invalid = false;

  constructor(private formBuilder: FormBuilder,
              private securityService: SecurityService,
              private router: Router) {
  }

  onLogin() {
    this.securityService.login(this.loginForm.value.username)
      .then(() => this.router.navigate(['/']))
      .catch(() => {
        this.invalid = true;
      });
  }
}
