import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import { ProgrammeUnitCostsSubmissionPageComponent } from './programme-unit-costs-submission-page.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {ProgrammeUnitCostDTO} from '@cat/api';

describe('ProgrammeUnitCostsSubmissionPageComponent', () => {
  let component: ProgrammeUnitCostsSubmissionPageComponent;
  let fixture: ComponentFixture<ProgrammeUnitCostsSubmissionPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeUnitCostsSubmissionPageComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeUnitCostsSubmissionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update a unit cost', fakeAsync(() => {
    component.updateUnitCost({} as ProgrammeUnitCostDTO);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/costOption/unitCost`
    });
  }));

  it('should create a unit cost', fakeAsync(() => {
    component.createUnitCost({} as ProgrammeUnitCostDTO);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/costOption/unitCost`
    });
  }));
});
