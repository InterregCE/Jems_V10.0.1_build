import { ComponentFixture, fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';
import {ProjectApplicationFormOverallObjectiveSectionComponent} from './project-application-form-overall-objective-section.component';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';

describe('ProjectApplicationFormOverallObjectiveSectionComponent', () => {
  let component: ProjectApplicationFormOverallObjectiveSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormOverallObjectiveSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFormOverallObjectiveSectionComponent]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1'};
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormOverallObjectiveSectionComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
