import { TestBed, waitForAsync } from '@angular/core/testing';
import {AppComponent} from './app.component';
import {TestModule} from '@common/test-module';
import {RouterModule} from '@angular/router';

describe('AppComponent', () => {
  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        RouterModule
      ],
      declarations: [
        AppComponent
      ]
    }).compileComponents();
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });
});
