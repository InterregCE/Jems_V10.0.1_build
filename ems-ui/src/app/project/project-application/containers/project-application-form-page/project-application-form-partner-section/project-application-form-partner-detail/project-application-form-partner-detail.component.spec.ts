import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProjectApplicationFormPartnerDetailComponent} from './project-application-form-partner-detail.component';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';

describe('ProjectApplicationFormPartnerDetailComponent', () => {
  let component: ProjectApplicationFormPartnerDetailComponent;
  let fixture: ComponentFixture<ProjectApplicationFormPartnerDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFormPartnerDetailComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormPartnerDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.projectId = 1;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
