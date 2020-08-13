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
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {HelpMenuComponent} from '@common/components/top-bar/help-menu/help-menu.component';
import {AlertComponent} from '@common/components/forms/form-validation/alert.component';
import {MatSortHeader} from '@angular/material/sort';
import {MatSelectModule} from '@angular/material/select';
import {TabService} from './services/tab.service';
import {MatSidenavModule} from '@angular/material/sidenav';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {LanguageService} from './services/language.service';

const declarations = [
  TopBarComponent,
  MenuComponent,
  FormFieldErrorsComponent,
  ConfirmDialogComponent,
  HelpMenuComponent,
  AlertComponent
];

@NgModule({
  declarations: [
    declarations
  ],
  imports: [
    SharedModule,
    MatTabsModule,
    MatDialogModule,
    MatMenuModule,
    MatIconModule,
    MatSidenavModule,
    MatSelectModule,
  ],
  providers: [
    SecurityService,
    PermissionService,
    TopBarService,
    SideNavService,
    LanguageService,
    KeyValuePipe,
    MatSortHeader,
    TabService,
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
