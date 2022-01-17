import { ComponentFixture, fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';

import { ProgrammeLumpSumsSubmissionPageComponent } from './programme-lump-sums-submission-page.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '@common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {ProgrammeLumpSumDTO} from '@cat/api';

describe('ProgrammeLumpSumsSubmissionPageComponent', () => {
  let component: ProgrammeLumpSumsSubmissionPageComponent;
  let fixture: ComponentFixture<ProgrammeLumpSumsSubmissionPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeLumpSumsSubmissionPageComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeLumpSumsSubmissionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update a lump sum', fakeAsync(() => {
    component.updateLumpSum({} as ProgrammeLumpSumDTO);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/costOption/lumpSum`
    });
  }));

  it('should create a lump sum', fakeAsync(() => {
    component.createLumpSum({} as ProgrammeLumpSumDTO);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/costOption/lumpSum`
    });
  }));
});
