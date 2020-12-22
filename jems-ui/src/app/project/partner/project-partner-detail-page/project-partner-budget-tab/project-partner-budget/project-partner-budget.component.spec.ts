import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ProjectPartnerBudgetComponent} from './project-partner-budget.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectPartnerStore} from '../../../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {ActivatedRoute} from '@angular/router';
import {PartnerBudgetTable} from '../../../../project-application/model/partner-budget-table';
import {PartnerBudgetTableType} from '../../../../project-application/model/partner-budget-table-type';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {AuthenticationStore} from '../../../../../authentication/service/authentication-store.service';


describe('ProjectApplicationPartnerBudgetPageComponent', () => {
  let component: ProjectPartnerBudgetComponent;
  let fixture: ComponentFixture<ProjectPartnerBudgetComponent>;
  let httpTestingController: HttpTestingController;
  let partnerStore: ProjectPartnerStore;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectPartnerBudgetComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {params: {projectId: 1}}
          }
        },
        {
          provide: ProjectPartnerDetailPageStore,
          useClass: ProjectPartnerDetailPageStore
        }
      ]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    partnerStore = TestBed.inject(ProjectPartnerStore);
  }));

  beforeEach(() => {
    partnerStore.partner$.next({id: 2});
    fixture = TestBed.createComponent(ProjectPartnerBudgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch and save budgets', fakeAsync(() => {
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/staffcosts'
    });
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/travel'
    });
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/external'
    });
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/equipment'
    });
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/infrastructure'
    });

    component.updateBudgets();
    tick();

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/partner/2/budget/staffcosts'
    });
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/partner/2/budget/travel'
    });
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/partner/2/budget/external'
    });
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/partner/2/budget/equipment'
    });
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/partner/2/budget/infrastructure'
    });
  }));

});
