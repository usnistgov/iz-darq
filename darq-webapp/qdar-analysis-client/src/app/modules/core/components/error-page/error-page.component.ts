import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { TurnOffLoader } from '@usnistgov/ngx-dam-framework-legacy';

@Component({
  selector: 'app-error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss']
})
export class ErrorPageComponent implements OnInit {

  constructor(private store: Store<any>) { }

  ngOnInit(): void {
    this.store.dispatch(new TurnOffLoader(true));
  }

}
