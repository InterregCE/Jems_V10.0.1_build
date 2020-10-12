import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationFormAssociatedOrganizationsListComponent } from './project-application-form-associated-organizations-list.component';

describe('ProjectApplicationFormAssociatedOrganizationsListComponent', () => {
  let component: ProjectApplicationFormAssociatedOrganizationsListComponent;
  let fixture: ComponentFixture<ProjectApplicationFormAssociatedOrganizationsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectApplicationFormAssociatedOrganizationsListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormAssociatedOrganizationsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
