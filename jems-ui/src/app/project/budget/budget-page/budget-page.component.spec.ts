import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import {BudgetPageComponent} from './budget-page.component';
import {TestModule} from '@common/test-module';
import {ProjectModule} from '../../project.module';
import {ActivatedRoute} from '@angular/router';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {of} from 'rxjs';

describe('BudgetPageComponent', () => {
  let component: BudgetPageComponent;
  let fixture: ComponentFixture<BudgetPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ProjectModule],
      declarations: [BudgetPageComponent],
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
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BudgetPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch project budget', fakeAsync(() => {
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/budget?version=1.0'
    });
  }));
});
