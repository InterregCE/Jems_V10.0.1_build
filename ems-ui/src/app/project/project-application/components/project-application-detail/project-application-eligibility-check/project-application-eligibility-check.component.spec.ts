import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationEligibilityCheckComponent } from './project-application-eligibility-check.component';

describe('ProjectApplicationEligibilityCheckComponent', () => {
  let component: ProjectApplicationEligibilityCheckComponent;
  let fixture: ComponentFixture<ProjectApplicationEligibilityCheckComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectApplicationEligibilityCheckComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationEligibilityCheckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
