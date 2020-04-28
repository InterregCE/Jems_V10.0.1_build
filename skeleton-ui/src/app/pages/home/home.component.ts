import {Component, OnInit} from '@angular/core';
import {HelloWorldService, OutputGreeting} from '@cat/api';
import {FormBuilder, Validators} from '@angular/forms';

@Component({
  selector: 'app-home',
  templateUrl: 'home.component.html'
})

export class HomeComponent implements OnInit {

  result: OutputGreeting | null = null;
  error: any = null;
  loading = false;


  adminResult: OutputGreeting | null = null;
  adminError: any = null;
  adminLoading = false;

  adminForm = this.formBuilder.group({
    name: ['', Validators.required]
  });

  constructor(private helloWorldService: HelloWorldService,
              private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.loading = true;
    this.helloWorldService.getHello().toPromise()
      .then(result => this.result = result)
      .catch(error => this.error = error)
      .finally(() => this.loading = false);
  }

  onAdminMessageSubmit() {
    this.adminLoading = true;
    this.adminResult = null;
    this.helloWorldService.getHelloAdmin(this.adminForm.value.name).toPromise()
      .then(result => this.adminResult = result)
      .catch(error => this.adminError = error.error)
      .finally(() => this.adminLoading = false);
  }
}
