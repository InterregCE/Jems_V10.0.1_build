import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import {InputProjectOverallObjective} from '@cat/api';
import {ProjectApplicationFormOverallObjectiveSectionComponent} from './project-application-form-overall-objective-section.component';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';

describe('ProjectApplicationFormOverallObjectiveSectionComponent', () => {
  let component: ProjectApplicationFormOverallObjectiveSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormOverallObjectiveSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFormOverallObjectiveSectionComponent]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
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

  it('should update project overall objective', fakeAsync(() => {
    component.updateProjectDescription$.next({} as InputProjectOverallObjective);

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/description/c1'
    })
  }));
});
