import {TestBed} from '@angular/core/testing';
import {LanguageStore} from './language-store.service';
import {TestModule} from '../test-module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TranslateService} from '@ngx-translate/core';
import {RouterTestingModule} from '@angular/router/testing';

describe('LanguageStore', () => {
  let httpTestingController: HttpTestingController;
  let service: LanguageStore;
  let translate: TranslateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1', component: LanguageStore}])
      ],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(LanguageStore);
    translate = TestBed.inject(TranslateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should set the new language', () => {
    service.setSystemLanguageAndUpdateProfile('de');
    expect(translate.currentLang).toBe('de');
  });
});
