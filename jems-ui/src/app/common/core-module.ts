import {APP_INITIALIZER, NgModule, Optional, SkipSelf} from '@angular/core';
import {DatePipe, KeyValuePipe} from '@angular/common';
import {MatSortHeader} from '@angular/material/sort';
import {SecurityService} from '../security/security.service';
import {LoginPageService} from '../authentication/login/services/login-page-service';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {PermissionService} from '../security/permissions/permission.service';
import {ThemeService} from '../theme/theme.service';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthenticationInterceptor} from '../security/authentication.interceptor';
import {HttpErrorInterceptor} from './interceptors/http-error.interceptor';
import {BASE_PATH, AvailableProgrammeLanguagesDTO} from '@cat/api';
import {MaterialConfigModule} from './material/material-config-module';
import {AppInitializerService} from './services/app-initializer.service';

export function loadLanguages(appInitializerService: AppInitializerService): () => Promise<AvailableProgrammeLanguagesDTO> {
  return () => appInitializerService.loadLanguages();
}

@NgModule({
  imports: [
    MaterialConfigModule
  ],
  providers: [
    DatePipe,
    KeyValuePipe,
    MatSortHeader,
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
    },
    {
      provide: APP_INITIALIZER,
      useFactory: loadLanguages,
      deps: [AppInitializerService],
      multi: true
    }
  ]
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule?: CoreModule) {
    if (parentModule) {
      throw new Error(
        'CoreModule is already loaded. Import it in the AppModule only');
    }
  }
}
