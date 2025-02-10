import { of } from 'rxjs';
import { MessageService } from '@usnistgov/ngx-dam-framework-legacy';
import { Action } from '@ngrx/store';
import { Type } from '@angular/core';

export function handleError(messageService: MessageService, FailureAction: Type<Action>) {
  return (error: any) => {
    return of(
      messageService.actionFromError(error),
      new FailureAction(error),
    );
  };
}
