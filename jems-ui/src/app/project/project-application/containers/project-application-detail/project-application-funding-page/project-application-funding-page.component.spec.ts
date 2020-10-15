import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProjectApplicationFundingPageComponent} from './project-application-funding-page.component';
import {ProjectModule} from '../../../../project.module';
import {TestModule} from '../../../../../common/test-module';
import {Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';

describe('ProjectApplicationFundingPageComponent', () => {
  let component: ProjectApplicationFundingPageComponent;
  let fixture: ComponentFixture<ProjectApplicationFundingPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1', component: ProjectApplicationFundingPageComponent}])
      ],
      declarations: [ProjectApplicationFundingPageComponent],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFundingPageComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to project', () => {
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.callThrough();

    component.redirectToProject();

    expect(router.navigate).toHaveBeenCalledWith(['app', 'project', 'detail', 1]);
  });
});
