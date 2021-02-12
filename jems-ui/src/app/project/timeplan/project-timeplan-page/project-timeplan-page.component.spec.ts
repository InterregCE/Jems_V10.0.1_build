import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectTimeplanPageComponent } from './project-timeplan-page.component';

describe('ProjectTimeplanPageComponent', () => {
  let component: ProjectTimeplanPageComponent;
  let fixture: ComponentFixture<ProjectTimeplanPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectTimeplanPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectTimeplanPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
