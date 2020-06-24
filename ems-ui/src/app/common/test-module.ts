import {NgModule} from '@angular/core';
import {SharedModule} from './shared-module';
import {DatePipe} from '@angular/common';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';

@NgModule({
  declarations: [],
  imports: [
    SharedModule,
    HttpClientTestingModule,
    RouterTestingModule,
    HttpClientTestingModule
  ],
  providers: [
    DatePipe
  ],
  exports: []
})
export class TestModule {
}
