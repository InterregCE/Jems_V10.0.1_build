import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {ProgrammeLanguagesPageComponent} from './programme-languages-page.component';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {OutputProgrammeData} from '@cat/api';
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

  it('should update a programme', fakeAsync(() => {
    const spyOnReload = spyOn(component, 'reloadPage');
    const programme = {cci: 'some cci'} as OutputProgrammeData;
    programme.systemLanguageSelections = new Array({ name: 'EN', selected: true, translationKey: 'translation.en'});

    component.saveProgrammeData$.next(programme);
    let success = false;
    component.programmeSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/programmedata`
    }).flush(programme);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmedata`
    }).flush(programme);
    httpTestingController.verify();

    tick();
    expect(spyOnReload).toHaveBeenCalled();
    expect(success).toBeTruthy();
  }));

});
