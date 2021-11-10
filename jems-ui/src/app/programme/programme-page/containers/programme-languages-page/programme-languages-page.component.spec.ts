import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';

import {ProgrammeLanguagesPageComponent} from './programme-languages-page.component';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {ProgrammeLanguageDTO} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';

describe('ProgrammeLanguagesPageComponent', () => {
  let component: ProgrammeLanguagesPageComponent;
  let fixture: ComponentFixture<ProgrammeLanguagesPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeLanguagesPageComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeLanguagesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update programme languages', fakeAsync(() => {
    const spyOnReload = spyOn(component, 'reloadLanguages');
    const languages = new Array({ code: ProgrammeLanguageDTO.CodeEnum.EN, ui: true, fallback: false, input: false} as ProgrammeLanguageDTO );

    component.saveLanguages$.next(languages);
    let success = false;
    component.languagesSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({ method: 'GET', url: `//api/auth/current` });
    httpTestingController.match({ method: 'GET', url: `//api/programmeLanguage` });

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmeLanguage`
    }).flush(languages);
    httpTestingController.verify();

    tick();
    expect(spyOnReload).toHaveBeenCalled();
    expect(success).toBeTruthy();
  }));

});
