import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { map } from 'rxjs/operators';
import {
  Component,
  OnInit,
  Input,
} from '@angular/core';
import { selectRouterURL, selectIsAdmin, selectIsLoggedIn, LogoutRequest } from '@usnistgov/ngx-dam-framework-legacy';
import { IServerInfo } from '../../services/app-info.service';
import { ICurrentUser } from '../../model/user.model';
import { selectCurrentUser } from '../../store/core.selectors';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  @Input()
  info: IServerInfo;

  isAdf: Observable<boolean>;
  isAdmin$: Observable<boolean>;
  isLogged$: Observable<boolean>;
  currentUser$: Observable<ICurrentUser>;

  constructor(private store: Store<any>) {
    this.isLogged$ = store.select(selectIsLoggedIn);
    this.currentUser$ = store.select(selectCurrentUser);
    this.isAdf = store.select(selectRouterURL).pipe(
      map(
        (url: string) => {
          return url.startsWith('/adf/');
        },
      ),
    );
    this.isAdmin$ = this.store.select(selectIsAdmin);
  }

  logout() {
    this.store.dispatch(new LogoutRequest());
  }

  ngOnInit(): void {
  }

}
