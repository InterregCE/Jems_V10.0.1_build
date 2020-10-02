import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {MatSidenavModule} from '@angular/material/sidenav';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {MatCardModule} from '@angular/material/card';
import {NgxPermissionsModule} from 'ngx-permissions';

import {ApiModule, BASE_PATH} from '@cat/api';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {AppComponent} from './app.component';
import {CoreModule} from './common/core-module';
import {SharedModule} from './common/shared-module';
import {HttpLoaderFactory} from './app-i18n.module';
import {routes} from './app-routing.module';
import {AppWrapComponent} from './component/app-wrap/app-wrap.component';
import {NoAuthWrapComponent} from './component/no-auth-wrap/no-auth-wrap.component';
import {AppNotFoundComponent} from './component/app-not-found/app-not-found.component';
import {PermissionService} from './security/permissions/permission.service';
import {SecurityService} from './security/security.service';
import {OverlayContainer, OverlayModule} from '@angular/cdk/overlay';
import {ThemeService} from './theme/theme.service';
import {HttpErrorInterceptor} from './common/interceptors/http-error.interceptor';
import {AuthenticationInterceptor} from './security/authentication.interceptor';
import {LoginPageService} from './authentication/login/services/login-page-service';

@NgModule({
  declarations: [
    AppComponent,
    AppNotFoundComponent,
    AppWrapComponent,
    NoAuthWrapComponent,
  ],
  imports: [
    ApiModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    BrowserModule,
    BrowserAnimationsModule,
    CoreModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
    NgxPermissionsModule.forRoot(),
    MatSidenavModule,
    SharedModule,
    MatCardModule,
    OverlayModule,
  ],
  providers: [
    SecurityService,
    LoginPageService,
    SideNavService,
    PermissionService,
    ThemeService,
    {
      provide: BASE_PATH,
      useValue: '.'
    },
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
  exports: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(overlayContainer: OverlayContainer, themeService: ThemeService) {
    themeService.$currentTheme
      .subscribe(theme => overlayContainer.getContainerElement().classList.value = `cdk-overlay-container ${theme}`);
  }
}
