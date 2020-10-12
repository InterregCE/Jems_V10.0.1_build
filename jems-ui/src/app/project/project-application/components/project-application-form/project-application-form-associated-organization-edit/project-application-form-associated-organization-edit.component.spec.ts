import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationFormAssociatedOrganizationEditComponent } from './project-application-form-associated-organization-edit.component';

describe('ProjectApplicationFormAssociatedOrganizationEditComponent', () => {
  let component: ProjectApplicationFormAssociatedOrganizationEditComponent;
  let fixture: ComponentFixture<ProjectApplicationFormAssociatedOrganizationEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectApplicationFormAssociatedOrganizationEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormAssociatedOrganizationEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
