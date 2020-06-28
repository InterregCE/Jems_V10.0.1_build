import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {TopBarComponent} from './components/top-bar/top-bar.component';
import {MenuComponent} from './components/menu/menu.component';
import {SecurityService} from '../security/security.service';
import {AuthenticationInterceptor} from '../security/authentication.interceptor';
import {HttpErrorInterceptor} from './interceptors/http-error.interceptor';
import {MatTabsModule} from '@angular/material/tabs';
import {PermissionService} from '../security/permissions/permission.service';
import {TopBarService} from '@common/components/top-bar/top-bar.service';
import {FormFieldErrorsComponent} from '@common/components/forms/form-field-errors/form-field-errors.component';
import {KeyValuePipe} from '@angular/common';
import {SharedModule} from './shared-module';

const declarations = [
  TopBarComponent,
  MenuComponent,
  FormFieldErrorsComponent
];

@NgModule({
  declarations: [
    declarations
  ],
  imports: [
    SharedModule,
    MatTabsModule
  ],
  providers: [
    SecurityService,
    PermissionService,
    TopBarService,
    KeyValuePipe,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthenticationInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    }
  ],
  exports: [
    declarations
  ]
})
export class CoreModule {
}
