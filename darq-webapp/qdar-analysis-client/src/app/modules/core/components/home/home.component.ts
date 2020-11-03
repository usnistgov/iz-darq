import { Component, OnInit, AfterViewInit } from '@angular/core';
import { WebContentService } from '../../services/web-content.service';
import { Observable } from 'rxjs';
import { DomSanitizer } from '@angular/platform-browser';
import { map } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { TurnOffLoader } from 'ngx-dam-framework';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, AfterViewInit {

  homePage$: Observable<any>;

  constructor(
    private webContent: WebContentService,
    private sanitizer: DomSanitizer,
    private store: Store<any>) {
  }

  ngAfterViewInit(): void {
    this.store.dispatch(new TurnOffLoader());
  }

  ngOnInit(): void {
    this.homePage$ = this.webContent.getHomePage().pipe(
      map((value) => {
        return {
          ...value,
          sections: [
            ...value.sections.map((section) => ({
              ...section,
              content: this.sanitizer.bypassSecurityTrustHtml(section.content),
            }))
          ]
        };
      })
    );
  }

}
