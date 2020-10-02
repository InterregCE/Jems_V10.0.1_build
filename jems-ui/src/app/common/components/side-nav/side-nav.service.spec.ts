import {HttpTestingController} from '@angular/common/http/testing';
import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../test-module';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Subject} from 'rxjs';

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
    service.getHeadlines().subscribe((items: HeadlineRoute[]) => headlines = items);

    service.setHeadlines(destroyed$, [
      {
        headline: 'back.project.overview',
        route: '/app/project/1',
      },
      {
        headline: 'project.application.form.title',
      },
      {
        headline: 'Test',
      },
      {
        headline: 'A - Project Identification',
        scrollRoute: 'applicationFormHeading',
      },
      {
        headline: 'A.1 Project Identification',
        scrollRoute: 'projectIdentificationHeading',
      }]);

    tick(60);
    expect(headlines.length).toBe(5);
    expect(headlines[0].headline).toBe('back.project.overview')
    expect(headlines[0].route).toBe('/app/project/1')
    expect(headlines[1].headline).toBe('project.application.form.title')
    expect(headlines[2].headline).toBe('Test')
    expect(headlines[3].headline).toBe('A - Project Identification')
    expect(headlines[3].scrollRoute).toBe('applicationFormHeading')
    expect(headlines[4].headline).toBe('A.1 Project Identification')
    expect(headlines[4].scrollRoute).toBe('projectIdentificationHeading')
  }));

  it('scroll to anchor', fakeAsync(() => {
    const fakeElement = document.createElement('div');
    spyOn(document, 'getElementById').withArgs('scrollRoute').and.returnValue(fakeElement);
    spyOn(fakeElement, 'scrollIntoView').and.callThrough();

    service.navigate({scrollRoute: 'scrollRoute'} as any);
    expect(fakeElement.scrollIntoView).toHaveBeenCalled();
  }));
});
