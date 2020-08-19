import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import {ProgrammeAreaComponent} from './programme-area.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProgrammeModule} from '../../../programme.module';
import {TestModule} from '../../../../common/test-module';

describe('ProgrammeAreaComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammeAreaComponent;
  let fixture: ComponentFixture<ProgrammeAreaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ProgrammeModule,
        TestModule
      ],
      declarations: [ProgrammeAreaComponent]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeAreaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should download initial nuts metadata', () => {
    httpTestingController.expectOne({method: 'GET', url: '//api/nuts/metadata'});
  });

  it('should download nuts metadata', fakeAsync(() => {
    component.downloadLatestNuts$.next();

    httpTestingController.expectOne({method: 'GET', url: '//api/nuts/metadata'});
    httpTestingController.expectOne({method: 'POST', url: '//api/nuts/download'});
  }));
});
