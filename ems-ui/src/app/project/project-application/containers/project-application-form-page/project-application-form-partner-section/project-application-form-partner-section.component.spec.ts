import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProjectApplicationFormPartnerSectionComponent} from './project-application-form-partner-section.component';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';

describe('ProjectApplicationFormPartnerSectionComponent', () => {
  let component: ProjectApplicationFormPartnerSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormPartnerSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFormPartnerSectionComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormPartnerSectionComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
