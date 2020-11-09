import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {WorkPackageDetailsComponent} from './work-package-details.component';
import {Router} from '@angular/router';
import {HttpTestingController} from '@angular/common/http/testing';
import {OutputWorkPackage, InputWorkPackageCreate, InputWorkPackageUpdate} from '@cat/api';
import {RouterTestingModule} from '@angular/router/testing';

describe('WorkPackageDetailsComponent', () => {
  let component: WorkPackageDetailsComponent;
  let fixture: ComponentFixture<WorkPackageDetailsComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationForm', component: WorkPackageDetailsComponent}])
      ],
      declarations: [WorkPackageDetailsComponent],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkPackageDetailsComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
    component.workPackageId$.next(1);
  });

  it('should navigate to work package overview', () => {
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.callThrough();

    component.redirectToWorkPackageOverview();

    expect(router.navigate).toHaveBeenCalledWith(['app', 'project', 'detail', 1, 'applicationFormWorkPackage']);
  });

  it('should list work packages details', fakeAsync(() => {
    let resultWP: OutputWorkPackage = {} as OutputWorkPackage;
    component.workPackageDetails$.subscribe(result => resultWP = result as OutputWorkPackage);

    const workPackage = {} as OutputWorkPackage;

    httpTestingController.match({method: 'GET', url: `//api/project/1/workpackage/1`})
      .forEach(req => req.flush({content: workPackage}));

    tick();
    expect(resultWP).toEqual(workPackage);
  }));

  it('should update a work package', fakeAsync(() => {
    component.updateWorkPackageData$.next({} as InputWorkPackageUpdate);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/1/workpackage`
    })
  }));

  it('should create a work package', fakeAsync(() => {
    component.createWorkPackageData$.next({} as InputWorkPackageCreate);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/project/1/workpackage`
    })
  }));
});
