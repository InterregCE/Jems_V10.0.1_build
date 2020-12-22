import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ProjectPartnerBudgetOptionsComponent} from './project-partner-budget-options.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectPartnerStore} from '../../../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {ActivatedRoute} from '@angular/router';
import {BudgetOptions} from '../../../../project-application/model/budget-options';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';


describe('ProjectApplicationFormPartnerBudgetOptionsComponent', () => {
  let component: ProjectPartnerBudgetOptionsComponent;
  let fixture: ComponentFixture<ProjectPartnerBudgetOptionsComponent>;
  let httpTestingController: HttpTestingController;
  let partnerStore: ProjectPartnerStore;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectPartnerBudgetOptionsComponent],
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
    fixture = TestBed.createComponent(ProjectPartnerBudgetOptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch and save budget options', fakeAsync(() => {
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/partner/2/budget/options'
    });

    component.updateBudgetOptions(new BudgetOptions(8, 10, 10, null));
    tick();

    httpTestingController.expectOne({
      method: 'PUT',
      url: '//api/project/partner/2/budget/options'
    });
  }));
});
