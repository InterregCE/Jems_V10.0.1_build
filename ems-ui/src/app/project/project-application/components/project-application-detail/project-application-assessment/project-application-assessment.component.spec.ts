import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationAssessmentComponent } from './project-application-assessment.component';

describe('ProjectApplicationAssessmentComponent', () => {
  let component: ProjectApplicationAssessmentComponent;
  let fixture: ComponentFixture<ProjectApplicationAssessmentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectApplicationAssessmentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationAssessmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
