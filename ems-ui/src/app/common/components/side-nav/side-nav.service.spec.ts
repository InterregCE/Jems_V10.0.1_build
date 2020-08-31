import {HttpTestingController} from '@angular/common/http/testing';
import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../test-module';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Subject} from 'rxjs';
import {HeadlineType} from '@common/components/side-nav/headline-type';

describe('SideNavService', () => {
  let httpTestingController: HttpTestingController;
  let service: SideNavService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(SideNavService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should store and pass the headlines', fakeAsync(() => {
    const destroyed$ = new Subject();

    let headlines: HeadlineRoute[] = [];
    service.getHeadlines().subscribe((items: HeadlineRoute[]) => {headlines = items;});

    service.setHeadlines(destroyed$, [
      {
        headline: 'back.project.overview',
        route: '/project/1',
        type: HeadlineType.BACKROUTE
      },
      {
        headline: 'project.application.form.title',
        type: HeadlineType.TITLE
      },
      {
        headline: 'Test',
        type: HeadlineType.SUBTITLE
      },
      {
        headline: 'A - Project Identification',
        scrollRoute: 'applicationFormHeading',
        type: HeadlineType.SECTION
      },
      {
        headline: 'A.1 Project Identification',
        scrollRoute: 'projectIdentificationHeading',
        type: HeadlineType.SUBSECTION
      }]);

    tick(60);
    expect(headlines.length).toBe(5);
    expect(headlines[0].headline).toBe('back.project.overview')
    expect(headlines[0].route).toBe('/project/1')
    expect(headlines[0].type).toBe(HeadlineType.BACKROUTE)
    expect(headlines[1].headline).toBe('project.application.form.title')
    expect(headlines[1].type).toBe(HeadlineType.TITLE)
    expect(headlines[2].headline).toBe('Test')
    expect(headlines[2].type).toBe(HeadlineType.SUBTITLE)
    expect(headlines[3].headline).toBe('A - Project Identification')
    expect(headlines[3].scrollRoute).toBe('applicationFormHeading')
    expect(headlines[3].type).toBe(HeadlineType.SECTION)
    expect(headlines[4].headline).toBe('A.1 Project Identification')
    expect(headlines[4].scrollRoute).toBe('projectIdentificationHeading')
    expect(headlines[4].type).toBe(HeadlineType.SUBSECTION);

    destroyed$.next();
    tick();
    expect(headlines.length).toBe(0);
  }));

  it('should store and pass the alert status', fakeAsync(() => {
    const destroyed$ = new Subject();

    let alertStatus = true;
    service.getAlertStatus().subscribe((status: boolean) => alertStatus = status);

    service.setAlertStatus(false);
    service.setHeadlines(destroyed$,[])

    tick(60);
    expect(alertStatus).toBe(false);

    destroyed$.next();
    tick();
    expect(alertStatus).toBeUndefined();
  }));
});
