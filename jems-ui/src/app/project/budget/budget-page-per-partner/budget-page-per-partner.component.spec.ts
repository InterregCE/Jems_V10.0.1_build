import { ComponentFixture, fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';

import {BudgetPagePerPartnerComponent} from './budget-page-per-partner.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '@common/test-module';
import {ProjectModule} from '../../project.module';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {of} from 'rxjs';

describe('BudgetPagePerPartnerComponent', () => {
  let component: BudgetPagePerPartnerComponent;
  let fixture: ComponentFixture<BudgetPagePerPartnerComponent>;
  let httpTestingController: HttpTestingController;
  let projectStore: ProjectStore;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ProjectModule],
      declarations: [BudgetPagePerPartnerComponent],
      providers: [
        {
          provide: ProjectVersionStore,
          useValue: {
            currentRouteVersion$: of('1.0')
          }
        }
      ]
    })
      .compileComponents();
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.params = {projectId: '1'};
    httpTestingController = TestBed.inject(HttpTestingController);
    projectStore = TestBed.inject(ProjectStore);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BudgetPagePerPartnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch project budget per partner', fakeAsync(() => {
    projectStore.projectId$.next(1);
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/coFinancing?version=1.0'
    });
  }));
});
