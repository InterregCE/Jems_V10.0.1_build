import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../../common/test-module';
import {LoginPageService} from './login-page-service';
import {I18nValidationError} from '@common/validation/i18n-validation-error';

describe('LoginPageService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [TestModule],
    providers: [
      {
        provide: LoginPageService,
        useClass: LoginPageService
      },
    ]
  }));

  it('should be created', () => {
    const service: LoginPageService = TestBed.inject(LoginPageService);
    expect(service).toBeTruthy();
  });

  it('should forward authentication problems', fakeAsync(() => {
    const service: LoginPageService = TestBed.inject(LoginPageService);

    let authError: I18nValidationError | null = null;
    service.authenticationError()
      .subscribe((error: I18nValidationError | null) => authError = error);

    service.newAuthenticationError({i18nKey: 'authentication.expired', httpStatus: 401})

    tick();
    expect(authError).toBeTruthy();
    expect((authError as any).i18nKey).toBeTruthy();
  }));
});
