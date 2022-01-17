import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {ProjectPartnerBudgetComponent} from './project-partner-budget.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {TestModule} from '@common/test-module';
import {ProjectModule} from '@project/project.module';
import {ActivatedRoute} from '@angular/router';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {of} from 'rxjs';
import {ProjectPartnerBudgetTabService} from '../project-partner-budget-tab.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectPartnerBudgetStore} from '@project/budget/services/project-partner-budget.store';


describe('ProjectApplicationPartnerBudgetPageComponent', () => {
  let component: ProjectPartnerBudgetComponent;
  let fixture: ComponentFixture<ProjectPartnerBudgetComponent>;
  let httpTestingController: HttpTestingController;
  let partnerDetailPageStore: ProjectPartnerDetailPageStore;
  let projectPartnerDetailPageStore: ProjectPartnerDetailPageStore;

  beforeEach(waitForAsync(() => {
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
          provide: ProjectVersionStore,
          useValue: {
            selectedVersionParam$: of('1.0')
          }
        },
        {
          provide: ProjectPartnerStore,
          useValue: {
            partner$: of({id: 2})
          }
        },
        {
          provide: ProjectPartnerDetailPageStore,
          useClass: ProjectPartnerBudgetStore
        },
        {
          provide: ProjectPartnerBudgetTabService,
          useClass: ProjectPartnerBudgetTabService
        }
      ]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    partnerDetailPageStore = TestBed.inject(ProjectPartnerDetailPageStore);
    projectPartnerDetailPageStore = TestBed.inject(ProjectPartnerDetailPageStore);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectPartnerBudgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch and save budgets', fakeAsync(() => {
    partnerDetailPageStore.budgetOptions$ = of({} as any);
    httpTestingController.expectOne({method: 'GET', url: `//api/auth/current`});

    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/costs?version=1.0'
    });
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/options?version=1.0'
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
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/partner/2/budget/unitcosts'
    });
  }));

  it('should fetch and save budgets with other costs option', fakeAsync(() => {
    partnerDetailPageStore.budgetOptions$ = of({otherCostsOnStaffCostsFlatRate: 10} as any);

    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/costs?version=1.0'
    });
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/options?version=1.0'
    });

    component.updateBudgets();
    tick();

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/partner/2/budget/staffcosts'
    });
  }));

});
