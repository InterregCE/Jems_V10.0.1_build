import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import {InputProjectManagement} from '@cat/api';
import {ProjectApplicationFormManagementSectionComponent} from './project-application-form-management-section.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {ActivatedRoute} from '@angular/router';

describe('ProjectApplicationFormManagementSectionComponent', () => {
  let component: ProjectApplicationFormManagementSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormManagementSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFormManagementSectionComponent]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1'};
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormManagementSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update project management', fakeAsync(() => {
    component.updateManagement$.next({} as InputProjectManagement);

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/description/c7'
    });
  }));
});
