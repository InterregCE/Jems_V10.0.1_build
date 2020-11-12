import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import { ProgrammeResultIndicatorSubmissionPageComponent } from './programme-result-indicator-submission-page.component';
import {InputIndicatorResultCreate, InputIndicatorResultUpdate} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';

describe('ProgrammeResultIndicatorSubmissionPageComponent', () => {
  let component: ProgrammeResultIndicatorSubmissionPageComponent;
  let fixture: ComponentFixture<ProgrammeResultIndicatorSubmissionPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeResultIndicatorSubmissionPageComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeResultIndicatorSubmissionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update an indicator', fakeAsync(() => {
    component.updateResultIndicator({} as InputIndicatorResultUpdate);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmeindicator/result`
    });
  }));

  it('should create an indicator', fakeAsync(() => {
    component.createResultIndicator({} as InputIndicatorResultCreate);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/programmeindicator/result`
    });
  }));
});
