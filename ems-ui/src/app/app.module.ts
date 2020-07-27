import {NgModule} from '@angular/core';
import {BASE_PATH} from '@cat/api';
import {AppComponent} from './app.component';
import {UserModule} from './user/user.module';
import {AuthenticationModule} from './authentication/authentication.module';
import {ProjectModule} from './project/project.module';
import {CoreModule} from './common/core-module';
import {RouterModule} from '@angular/router';
import {NgxPermissionsModule} from 'ngx-permissions';
import {ProgrammeModule} from './programme/programme.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    CoreModule,
    UserModule,
    AuthenticationModule,
    ProjectModule,
    ProgrammeModule,
    RouterModule,
    NgxPermissionsModule.forRoot(),
  ],
  providers: [
    {
      provide: BASE_PATH,
      useValue: '.'
    },
  ],
  exports: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {
}
