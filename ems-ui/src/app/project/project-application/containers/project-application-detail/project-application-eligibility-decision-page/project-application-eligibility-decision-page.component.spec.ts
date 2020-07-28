import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectApplicationEligibilityDecisionPageComponent } from './project-application-eligibility-decision-page.component';
import {Router} from '@angular/router';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';

describe('ProjectApplicationEligibilityDecisionPageComponent', () => {
  let component: ProjectApplicationEligibilityDecisionPageComponent;
  let fixture: ComponentFixture<ProjectApplicationEligibilityDecisionPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ProjectModule],
      declarations: [ ProjectApplicationEligibilityDecisionPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationEligibilityDecisionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to project', () => {
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.callThrough();
    component.projectId = 1;

    component.redirectToProject();

    expect(router.navigate).toHaveBeenCalledWith(['project', 1]);
  });
});
