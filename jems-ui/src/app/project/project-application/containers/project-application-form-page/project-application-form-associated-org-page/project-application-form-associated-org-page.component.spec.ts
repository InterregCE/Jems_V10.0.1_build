import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProjectApplicationFormAssociatedOrgPageComponent} from './project-application-form-associated-org-page.component';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {ActivatedRoute} from '@angular/router';

describe('ProjectApplicationFormAssociatedOrgPageComponent', () => {
  let component: ProjectApplicationFormAssociatedOrgPageComponent;
  let fixture: ComponentFixture<ProjectApplicationFormAssociatedOrgPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ProjectModule],
      declarations: [ProjectApplicationFormAssociatedOrgPageComponent]
    })
      .compileComponents();
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1'};
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormAssociatedOrgPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
