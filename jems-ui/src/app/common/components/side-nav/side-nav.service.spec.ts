import {HttpTestingController} from '@angular/common/http/testing';
import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../test-module';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Subject} from 'rxjs';
import {RouterTestingModule} from '@angular/router/testing';

describe('SideNavService', () => {
  let httpTestingController: HttpTestingController;
  let service: SideNavService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        RouterTestingModule.withRoutes([{path: 'app/project/detail/1', component: SideNavService}])
      ],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(SideNavService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should store and pass the headlines', fakeAsync(() => {
    let headlines: HeadlineRoute[] = [];
    service.getHeadlines().subscribe((items: HeadlineRoute[]) => headlines = items);

    service.setHeadlines('root', [
      {
        headline: {i18nKey: 'back.project.overview'},
        route: '/app/project/1',
      },
      {
        headline: {i18nKey: 'project.application.form.title'},
      },
      {
        headline: {i18nKey: 'Test'},
      },
      {
        headline: {i18nKey: 'A - Project Identification'},
        scrollRoute: 'applicationFormHeading',
      },
      {
        headline: {i18nKey: 'A.1 Project Identification'},
        scrollRoute: 'projectIdentificationHeading',
      }]);

    tick(60);
    expect(headlines.length).toBe(5);
    expect(headlines[0].headline.i18nKey).toBe('back.project.overview');
    expect(headlines[0].route).toBe('/app/project/1');
    expect(headlines[1].headline.i18nKey).toBe('project.application.form.title');
    expect(headlines[2].headline.i18nKey).toBe('Test');
    expect(headlines[3].headline.i18nKey).toBe('A - Project Identification');
    expect(headlines[3].scrollRoute).toBe('applicationFormHeading');
    expect(headlines[4].headline.i18nKey).toBe('A.1 Project Identification');
    expect(headlines[4].scrollRoute).toBe('projectIdentificationHeading');
  }));

  it('scroll to anchor', fakeAsync(() => {
    const fakeElement = document.createElement('div');
    spyOn(document, 'getElementById').withArgs('scrollRoute').and.returnValue(fakeElement);
    spyOn(fakeElement, 'scrollIntoView').and.callThrough();

    service.navigate({scrollRoute: 'scrollRoute'} as any);
    expect(fakeElement.scrollIntoView).toHaveBeenCalled();
  }));
});
