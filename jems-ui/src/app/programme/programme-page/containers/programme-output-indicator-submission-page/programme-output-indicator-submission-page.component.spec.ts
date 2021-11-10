import { ComponentFixture, fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';

import { ProgrammeOutputIndicatorSubmissionPageComponent } from './programme-output-indicator-submission-page.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {OutputIndicatorCreateRequestDTO, OutputIndicatorUpdateRequestDTO} from '@cat/api';

describe('ProgrammeFinalIndicatorSubmissionPageComponent', () => {
  let component: ProgrammeOutputIndicatorSubmissionPageComponent;
  let fixture: ComponentFixture<ProgrammeOutputIndicatorSubmissionPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeOutputIndicatorSubmissionPageComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeOutputIndicatorSubmissionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should update an indicator', fakeAsync(() => {
    component.updateOutputIndicator({} as OutputIndicatorUpdateRequestDTO);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmeindicator/output`
    });
  }));

  it('should create an indicator', fakeAsync(() => {
    component.createOutputIndicator({} as OutputIndicatorCreateRequestDTO);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/programmeindicator/output`
    });
  }));
});
