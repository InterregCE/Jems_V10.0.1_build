import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {TopBarComponent} from './top-bar.component';
import {TestModule} from '../../test-module';
import {TopBarService} from '@common/components/top-bar/top-bar.service';

describe('TopBarComponent', () => {
  let component: TopBarComponent;
  let fixture: ComponentFixture<TopBarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule
      ],
      declarations: [TopBarComponent],
      providers: [
        {
          provide: TopBarService,
          useClass: TopBarService
        },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TopBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
