import { ComponentFixture, fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';
import {InputProjectLongTermPlans} from '@cat/api';
import {ProjectApplicationFormFuturePlansSectionComponent} from './project-application-form-future-plans-section.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {ActivatedRoute} from '@angular/router';

describe('ProjectApplicationFormFuturePlansSectionComponent', () => {
  let component: ProjectApplicationFormFuturePlansSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormFuturePlansSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFormFuturePlansSectionComponent]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1'};
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormFuturePlansSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update project long-term plans', fakeAsync(() => {
    component.updateFuturePlans$.next({} as InputProjectLongTermPlans);

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/description/c8'
    });
  }));
});
