import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {ProjectApplicationFormIdentificationPageComponent} from './project-application-form-identification-page.component';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';

describe('ProjectApplicationFormIdentificationPageComponent', () => {
  let component: ProjectApplicationFormIdentificationPageComponent;
  let fixture: ComponentFixture<ProjectApplicationFormIdentificationPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ProjectModule],
      declarations: [ProjectApplicationFormIdentificationPageComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormIdentificationPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
