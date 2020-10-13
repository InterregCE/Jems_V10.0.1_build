import {TestBed} from '@angular/core/testing';
import {LanguageService} from './language.service';
import {TestModule} from '../test-module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TranslateService} from '@ngx-translate/core';
import {RouterTestingModule} from '@angular/router/testing';

describe('LanguageService', () => {
  let httpTestingController: HttpTestingController;
  let service: LanguageService;
  let translate: TranslateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1', component: LanguageService}])
      ],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(LanguageService);
    translate = TestBed.inject(TranslateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should set the new language', () => {
    service.changeLanguage('de')

    expect(translate.currentLang).toBe('de');
  });
});
