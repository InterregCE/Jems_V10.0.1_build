import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationFormAssociatedOrgDetailComponent } from './project-application-form-associated-org-detail.component';

describe('ProjectApplicationFormAssociatedOrgDetailComponent', () => {
  let component: ProjectApplicationFormAssociatedOrgDetailComponent;
  let fixture: ComponentFixture<ProjectApplicationFormAssociatedOrgDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectApplicationFormAssociatedOrgDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormAssociatedOrgDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
