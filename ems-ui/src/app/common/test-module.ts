import {NgModule} from '@angular/core';
import {SharedModule} from './shared-module';
import {DatePipe} from '@angular/common';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {
  NgxPermissionsConfigurationStore,
  NgxPermissionsModule,
  NgxPermissionsService,
  NgxPermissionsStore,
  NgxRolesStore
} from 'ngx-permissions';

@NgModule({
  declarations: [],
  imports: [
    SharedModule,
    HttpClientTestingModule,
    RouterTestingModule,
    HttpClientTestingModule,
    NgxPermissionsModule
  ],
  providers: [
    DatePipe,
    NgxPermissionsStore,
    NgxPermissionsConfigurationStore,
    NgxRolesStore,
    NgxPermissionsService,
  ],
  exports: []
})
export class TestModule {
}
