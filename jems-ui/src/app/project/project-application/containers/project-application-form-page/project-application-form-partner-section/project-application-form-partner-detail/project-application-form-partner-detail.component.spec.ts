import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {ProjectApplicationFormPartnerDetailComponent} from './project-application-form-partner-detail.component';
import {Router} from '@angular/router';
import {HttpTestingController} from '@angular/common/http/testing';
import {OutputProjectPartner, InputProjectPartnerCreate, InputProjectPartnerUpdate, InputProjectPartnerContribution, InputProjectPartnerContact} from '@cat/api';

describe('ProjectApplicationFormPartnerDetailComponent', () => {
  let component: ProjectApplicationFormPartnerDetailComponent;
  let fixture: ComponentFixture<ProjectApplicationFormPartnerDetailComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
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

  it('should navigate to project partner overview', () => {
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.callThrough();

    component.redirectToPartnerOverview();

    expect(router.navigate).toHaveBeenCalledWith(['app', 'project', 'detail', 1, 'applicationForm']);
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

  it('should update a project partner', fakeAsync(() => {
    component.savePartner$.next({} as InputProjectPartnerUpdate);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/1/partner`
    })
  }));

  it('should create a project partner', fakeAsync(() => {
    component.createPartner$.next({} as InputProjectPartnerCreate);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/project/1/partner`
    })
  }));

  it('should update a project partner contact', fakeAsync(() => {
    component.savePartnerContact$.next({} as InputProjectPartnerContact[]);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/1/partner/2/contact`
    })
  }));

  it('should update a project partner contribution', fakeAsync(() => {
    component.savePartnerContribution$.next({} as InputProjectPartnerContribution);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/1/partner/2/contribution`
    })
  }));
});
