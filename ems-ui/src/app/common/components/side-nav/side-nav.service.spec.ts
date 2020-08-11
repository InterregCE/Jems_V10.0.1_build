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

    service.setHeadlines(destroyed$,[
      new HeadlineRoute('back.project.details', '/project/1', HeadlineType.BACKROUTE),
      new HeadlineRoute('project.application.form.title', '', HeadlineType.TITLE),
      new HeadlineRoute('Test', '', HeadlineType.SUBTITLE),
      new HeadlineRoute('A - Project Identification', 'applicationFormHeading', HeadlineType.SECTION),
      new HeadlineRoute('A.1 Project Identification', 'projectIdentificationHeading', HeadlineType.SUBSECTION)]);

    tick(60);
    expect(headlines.length).toBe(5);
    expect(headlines[0].headline).toBe('back.project.details')
    expect(headlines[0].route).toBe('/project/1')
    expect(headlines[0].type).toBe(HeadlineType.BACKROUTE)
    expect(headlines[1].headline).toBe('project.application.form.title')
    expect(headlines[1].route).toBe('')
    expect(headlines[1].type).toBe(HeadlineType.TITLE)
    expect(headlines[2].headline).toBe('Test')
    expect(headlines[2].route).toBe('')
    expect(headlines[2].type).toBe(HeadlineType.SUBTITLE)
    expect(headlines[3].headline).toBe('A - Project Identification')
    expect(headlines[3].route).toBe('applicationFormHeading')
    expect(headlines[3].type).toBe(HeadlineType.SECTION)
    expect(headlines[4].headline).toBe('A.1 Project Identification')
    expect(headlines[4].route).toBe('projectIdentificationHeading')
    expect(headlines[4].type).toBe(HeadlineType.SUBSECTION);

    destroyed$.next();
    tick();
    expect(headlines.length).toBe(0);
  }));
});
