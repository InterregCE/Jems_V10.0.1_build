import { ComponentFixture, fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';

import { ProgrammeResultIndicatorSubmissionPageComponent } from './programme-result-indicator-submission-page.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {ResultIndicatorCreateRequestDTO, ResultIndicatorUpdateRequestDTO} from '@cat/api';

describe('ProgrammeResultIndicatorSubmissionPageComponent', () => {
  let component: ProgrammeResultIndicatorSubmissionPageComponent;
  let fixture: ComponentFixture<ProgrammeResultIndicatorSubmissionPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
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
    component.updateResultIndicator({} as ResultIndicatorUpdateRequestDTO);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmeindicator/result`
    });
  }));

  it('should create an indicator', fakeAsync(() => {
    component.createResultIndicator({} as ResultIndicatorCreateRequestDTO);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/programmeindicator/result`
    });
  }));
});
