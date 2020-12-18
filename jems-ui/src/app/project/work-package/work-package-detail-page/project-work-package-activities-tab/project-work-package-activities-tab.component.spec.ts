import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProjectWorkPackageActivitiesTabComponent} from './project-work-package-activities-tab.component';
import {TestModule} from '../../../../common/test-module';
import {ProjectModule} from '../../../project.module';
import {ProjectWorkPackagePageStore} from '../project-work-package-page-store.service';

describe('ProjectWorkPackageActivitiesTabComponent', () => {
  let component: ProjectWorkPackageActivitiesTabComponent;
  let fixture: ComponentFixture<ProjectWorkPackageActivitiesTabComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ProjectModule],
      declarations: [ProjectWorkPackageActivitiesTabComponent],
      providers: [ProjectWorkPackagePageStore]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectWorkPackageActivitiesTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
