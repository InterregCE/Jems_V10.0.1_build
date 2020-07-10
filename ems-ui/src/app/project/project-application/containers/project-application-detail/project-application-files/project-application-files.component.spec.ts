import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationFilesComponent } from './project-application-files.component';

describe('ProjectApplicationFilesComponent', () => {
  let component: ProjectApplicationFilesComponent;
  let fixture: ComponentFixture<ProjectApplicationFilesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectApplicationFilesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
