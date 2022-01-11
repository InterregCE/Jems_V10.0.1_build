import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {MainPageTemplateComponent} from './main-page-template.component';
import {TestModule} from '@common/test-module';

describe('MainPageTemplateComponent', () => {
  let component: MainPageTemplateComponent;
  let fixture: ComponentFixture<MainPageTemplateComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule
      ],
      declarations: [
        MainPageTemplateComponent
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MainPageTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
