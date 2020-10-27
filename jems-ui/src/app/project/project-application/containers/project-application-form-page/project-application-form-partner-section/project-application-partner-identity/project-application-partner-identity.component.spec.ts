import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ProjectApplicationPartnerIdentityComponent} from './project-application-partner-identity.component';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';

describe('ProjectApplicationPartnerIdentityComponent', () => {
  let component: ProjectApplicationPartnerIdentityComponent;
  let fixture: ComponentFixture<ProjectApplicationPartnerIdentityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationForm', component: ProjectApplicationPartnerIdentityComponent}])
      ],
      declarations: [ProjectApplicationPartnerIdentityComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationPartnerIdentityComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to project partner overview', () => {
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.callThrough();

    component.redirectToPartnerOverview();

    expect(router.navigate).toHaveBeenCalledWith(['app', 'project', 'detail', 1, 'applicationForm']);
  });

});
