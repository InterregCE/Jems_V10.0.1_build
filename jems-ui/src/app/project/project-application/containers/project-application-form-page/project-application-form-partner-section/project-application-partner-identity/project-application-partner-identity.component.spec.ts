import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {ProjectApplicationPartnerIdentityComponent} from './project-application-partner-identity.component';
import {TestModule} from '@common/test-module';
import {ProjectModule} from '@project/project.module';
import {RouterTestingModule} from '@angular/router/testing';

describe('ProjectApplicationPartnerIdentityComponent', () => {
  let component: ProjectApplicationPartnerIdentityComponent;
  let fixture: ComponentFixture<ProjectApplicationPartnerIdentityComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationFormPartner', component: ProjectApplicationPartnerIdentityComponent}])
      ],
      declarations: [ProjectApplicationPartnerIdentityComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationPartnerIdentityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
