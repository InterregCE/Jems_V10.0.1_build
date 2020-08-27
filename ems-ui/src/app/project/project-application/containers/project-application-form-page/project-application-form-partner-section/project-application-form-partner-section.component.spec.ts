import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationFormPartnerSectionComponent } from './project-application-form-partner-section.component';

describe('ProjectApplicationFormPartnerSectionComponent', () => {
  let component: ProjectApplicationFormPartnerSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormPartnerSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectApplicationFormPartnerSectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormPartnerSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
