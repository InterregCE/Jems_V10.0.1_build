import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractForm} from "@common/components/forms/abstract-form";
import { LoginRequest } from '@cat/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent extends AbstractForm {

  @Output()
  submitLogin: EventEmitter<LoginRequest> = new EventEmitter<LoginRequest>();

  loginForm = this.formBuilder.group({
    email: ['', Validators.required],
    password: ['', Validators.required]
  });

  submitted = false;
  registerLink = '/register';

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef)
  }

  onSubmit() {
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
