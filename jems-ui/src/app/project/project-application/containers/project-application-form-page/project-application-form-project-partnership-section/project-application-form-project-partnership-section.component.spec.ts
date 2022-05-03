import { ComponentFixture, fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';
import {ProjectApplicationFormProjectPartnershipSectionComponent} from './project-application-form-project-partnership-section.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '@common/test-module';
import {ProjectModule} from '@project/project.module';
import {ActivatedRoute} from '@angular/router';

describe('ProjectApplicationFormProjectPartnershipSectionComponent', () => {
  let component: ProjectApplicationFormProjectPartnershipSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormProjectPartnershipSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFormProjectPartnershipSectionComponent]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1'};
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormProjectPartnershipSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
