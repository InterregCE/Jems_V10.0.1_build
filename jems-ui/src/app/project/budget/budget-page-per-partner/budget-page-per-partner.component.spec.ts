import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import { BudgetPagePerPartnerComponent } from './budget-page-per-partner.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../common/test-module';
import {ProjectModule} from '../../project.module';
import {ActivatedRoute} from '@angular/router';

describe('BudgetPagePerPartnerComponent', () => {
  let component: BudgetPagePerPartnerComponent;
  let fixture: ComponentFixture<BudgetPagePerPartnerComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ProjectModule],
      declarations: [ BudgetPagePerPartnerComponent ]
    })
    .compileComponents();
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1'};
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BudgetPagePerPartnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch project budget per partner', fakeAsync(() => {
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/coFinancing'
    });
  }));
});
