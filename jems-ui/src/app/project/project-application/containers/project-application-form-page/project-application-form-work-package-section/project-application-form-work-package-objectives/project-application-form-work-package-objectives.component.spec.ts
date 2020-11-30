import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import { ProjectApplicationFormWorkPackageObjectivesComponent } from './project-application-form-work-package-objectives.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {RouterTestingModule} from '@angular/router/testing';

describe('ProjectApplicationFormWorkPackageObjectivesComponent', () => {
  let component: ProjectApplicationFormWorkPackageObjectivesComponent;
  let fixture: ComponentFixture<ProjectApplicationFormWorkPackageObjectivesComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationFormWorkPackage', component: ProjectApplicationFormWorkPackageObjectivesComponent}])
      ],
      declarations: [ProjectApplicationFormWorkPackageObjectivesComponent],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormWorkPackageObjectivesComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    component.workPackageId = 1;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
