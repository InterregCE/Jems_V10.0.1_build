import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationFormWorkPackageOutputComponent } from './project-application-form-work-package-output.component';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpTestingController} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';

describe('ProjectApplicationFormWorkPackageOutputComponent', () => {
  let component: ProjectApplicationFormWorkPackageOutputComponent;
  let fixture: ComponentFixture<ProjectApplicationFormWorkPackageOutputComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationFormWorkPackage/detail/1', component: ProjectApplicationFormWorkPackageOutputComponent}])
      ],
      declarations: [ ProjectApplicationFormWorkPackageOutputComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1', workPackageId: '1'};
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormWorkPackageOutputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
