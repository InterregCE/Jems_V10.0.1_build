import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import {InputProjectLongTermPlans} from '@cat/api';
import { ProjectApplicationFormFuturePlansSectionComponent } from './project-application-form-future-plans-section.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';

describe('ProjectApplicationFormFuturePlansSectionComponent', () => {
  let component: ProjectApplicationFormFuturePlansSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormFuturePlansSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ ProjectApplicationFormFuturePlansSectionComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormFuturePlansSectionComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update project long-term plans', fakeAsync(() => {
    component.updateProjectDescription$.next({} as InputProjectLongTermPlans);

    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/description'
    })
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/description/c8'
    })
  }));
});
