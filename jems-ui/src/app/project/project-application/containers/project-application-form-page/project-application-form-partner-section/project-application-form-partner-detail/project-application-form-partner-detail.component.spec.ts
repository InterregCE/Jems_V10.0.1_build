import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {ProjectApplicationFormPartnerDetailComponent} from './project-application-form-partner-detail.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {
  InputProjectContact,
  InputProjectPartnerContribution,
  OutputProjectPartner,
} from '@cat/api';
import {RouterTestingModule} from '@angular/router/testing';

describe('ProjectApplicationFormPartnerDetailComponent', () => {
  let component: ProjectApplicationFormPartnerDetailComponent;
  let fixture: ComponentFixture<ProjectApplicationFormPartnerDetailComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationForm', component: ProjectApplicationFormPartnerDetailComponent}])
      ],
      declarations: [ProjectApplicationFormPartnerDetailComponent],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormPartnerDetailComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    component.partnerId = 2;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should list project partner details', fakeAsync(() => {
    let resultPartner: OutputProjectPartner = {} as OutputProjectPartner;
    component.partner$.subscribe(result => resultPartner = result as OutputProjectPartner);

    const projectPartner = {} as OutputProjectPartner;

    httpTestingController.match({method: 'GET', url: `//api/project/1/partner/2`})
      .forEach(req => req.flush({content: projectPartner}));

    tick();
    expect(resultPartner).toEqual(projectPartner);
  }));

  it('should update a project partner contact', fakeAsync(() => {
    component.savePartnerContact$.next({} as InputProjectContact[]);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/1/partner/2/contact`
    });
  }));

  it('should update a project partner contribution', fakeAsync(() => {
    component.savePartnerContribution$.next({} as InputProjectPartnerContribution);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/1/partner/2/contribution`
    });
  }));
});
