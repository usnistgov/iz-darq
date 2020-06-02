import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { flatMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { CoreActionTypes, CoreActions, GoToEntity } from './core.actions';
import { Router } from '@angular/router';
import { EntityType } from '../model/entity.model';
import { MessageService, UserMessage, MessageType } from 'ngx-dam-framework';

@Injectable()
export class CoreEffects {


  @Effect()
  goTo$ = this.actions$.pipe(
    ofType(CoreActionTypes.GoToEntity),
    flatMap((action: GoToEntity) => {
      // tslint:disable-next-line: no-small-switch
      switch (action.payload.type) {
        case EntityType.CONFIGURATION:
          this.router.navigate(['/', 'configurations', ...action.payload.id ? [action.payload.id] : []]);
          return of();

        case EntityType.TEMPLATE:
          this.router.navigate(['/', 'report-templates', ...action.payload.id ? [action.payload.id] : []]);
          return of();

        case EntityType.FACILITY:
          this.router.navigate(['/', 'facility', ...action.payload.id ? [action.payload.id] : []]);
          return of();

        default:
          return [
            this.messageService.userMessageToAction(new UserMessage(MessageType.FAILED, 'Can\'t go to ' + action.payload.type)),
          ];
      }
    })
  );


  constructor(
    private actions$: Actions<CoreActions>,
    private messageService: MessageService,
    private router: Router,
  ) { }

}
