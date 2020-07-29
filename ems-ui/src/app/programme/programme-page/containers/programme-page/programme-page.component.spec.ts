import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import { ProgrammePageComponent } from './programme-page.component';
import {InputProgrammeData} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';

describe('ProgrammePageComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammePageComponent;
  let fixture: ComponentFixture<ProgrammePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProgrammePageComponent ],
      imports: [
        ProgrammeModule,
        TestModule
      ],
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update a programme', fakeAsync(() => {
    const user = {cci: 'some cci'} as InputProgrammeData;

    component.saveProgrammeData$.next(user);
    let success = false;
    component.programmeSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/programmedata`
    }).flush(user);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmedata`
    }).flush(user);
    httpTestingController.verify();

    tick();
    expect(success).toBeTruthy();
  }));
});
