import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import {InputProjectRelevance} from '@cat/api';
import {ProjectApplicationFormProjectRelevanceAndContextSectionComponent} from './project-application-form-project-relevance-and-context-section.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {RouterTestingModule} from '@angular/router/testing';
import {ActivatedRoute} from '@angular/router';

describe('ProjectApplicationFormProjectRelevanceAndContextSectionComponent', () => {
  let component: ProjectApplicationFormProjectRelevanceAndContextSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormProjectRelevanceAndContextSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{
            path: 'app/project/detail/1/applicationForm',
            component: ProjectApplicationFormProjectRelevanceAndContextSectionComponent
          }])
      ],
      declarations: [ProjectApplicationFormProjectRelevanceAndContextSectionComponent]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1'};
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormProjectRelevanceAndContextSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update project relevance', fakeAsync(() => {
    component.updateProjectRelevance$.next({} as InputProjectRelevance);

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/description/c2'
    })
  }));

  it('should delete an entry from project relevance tables', fakeAsync(() => {
    component.deleteEntriesFromTables$.next({} as InputProjectRelevance);

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/description/c2'
    })
  }));
});
