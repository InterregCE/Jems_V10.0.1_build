import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {ProjectApplicationPartnerBudgetPageComponent} from './project-application-partner-budget-page.component';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';
import {PartnerBudgetTable} from '../../../../model/partner-budget-table';
import {PartnerBudgetTableType} from '../../../../model/partner-budget-table-type';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {of} from 'rxjs';

describe('ProjectApplicationPartnerBudgetPageComponent', () => {
  let component: ProjectApplicationPartnerBudgetPageComponent;
  let fixture: ComponentFixture<ProjectApplicationPartnerBudgetPageComponent>;
  let httpTestingController: HttpTestingController;
  let partnerStore: ProjectPartnerStore;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationPartnerBudgetPageComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {params: {projectId: 1}}
          }
        }
      ]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
    partnerStore = TestBed.inject(ProjectPartnerStore);
  }));

  beforeEach(() => {
    spyOn(partnerStore, 'getProjectPartner').and.returnValue(of({id: 2}));
    fixture = TestBed.createComponent(ProjectApplicationPartnerBudgetPageComponent);
    component = fixture.componentInstance;
    component.partnerId = 2;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch and save budgets', fakeAsync(() => {
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/partner/2/staffcost'
    });
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/partner/2/travel'
    })
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/partner/2/external'
    })
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/partner/2/equipment'
    })
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/partner/2/infrastructure'
    });

    component.saveBudgets$.next({
      staff: new PartnerBudgetTable(PartnerBudgetTableType.STAFF, []),
      travel: new PartnerBudgetTable(PartnerBudgetTableType.TRAVEL, []),
      external: new PartnerBudgetTable(PartnerBudgetTableType.EXTERNAL, []),
      equipment: new PartnerBudgetTable(PartnerBudgetTableType.EQUIPMENT, []),
      infrastructure: new PartnerBudgetTable(PartnerBudgetTableType.INFRASTRUCTURE, [])
    });
    tick();

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/partner/2/staffcost'
    });
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/partner/2/travel'
    })
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/partner/2/external'
    })
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/partner/2/equipment'
    })
    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/1/partner/2/infrastructure'
    });
  }));

});
