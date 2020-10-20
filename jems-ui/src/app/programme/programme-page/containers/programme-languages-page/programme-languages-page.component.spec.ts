import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {ProgrammeLanguagesPageComponent} from './programme-languages-page.component';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {OutputProgrammeLanguage} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';

describe('ProgrammeLanguagesPageComponent', () => {
  let component: ProgrammeLanguagesPageComponent;
  let fixture: ComponentFixture<ProgrammeLanguagesPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
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
    const languages = new Array({ code: OutputProgrammeLanguage.CodeEnum.EN, ui: true, fallback: false, input: false} as OutputProgrammeLanguage );

    component.saveLanguages$.next(languages);
    let success = false;
    component.languagesSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({ method: 'GET', url: `//api/auth/current` });
    httpTestingController.match({ method: 'GET', url: `//api/programmelanguage` });

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmelanguage`
    }).flush(languages);
    httpTestingController.verify();

    tick();
    expect(spyOnReload).toHaveBeenCalled();
    expect(success).toBeTruthy();
  }));

});
