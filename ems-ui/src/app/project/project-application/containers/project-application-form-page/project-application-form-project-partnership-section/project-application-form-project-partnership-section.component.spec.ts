import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import {InputProjectPartnership} from '@cat/api';
import { ProjectApplicationFormProjectPartnershipSectionComponent } from './project-application-form-project-partnership-section.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';

describe('ProjectApplicationFormProjectPartnershipSectionComponent', () => {
  let component: ProjectApplicationFormProjectPartnershipSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormProjectPartnershipSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ ProjectApplicationFormProjectPartnershipSectionComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormProjectPartnershipSectionComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update project parthenrship', fakeAsync(() => {
    component.updateProjectDescription$.next({} as InputProjectPartnership);

    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/description'
    })
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/description/c3'
    })
  }));
});
