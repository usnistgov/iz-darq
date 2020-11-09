import { Pipe, PipeTransform } from '@angular/core';
import { PermissionService } from '../../core/services/permission.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Action } from '../../core/model/action.enum';
import { ResourceType } from '../../core/model/resouce-type.enum';
import { Scope } from '../../core/model/scope.enum';
import { AccessToken } from '../../core/model/token.enum';

@Pipe({
  name: 'accessOnScope'
})
export class AccessScopePipe implements PipeTransform {

  constructor(
    private permissions: PermissionService
  ) { }

  transform(action: string, type: string, qscope: { scope: string, facilityId?: string }, token?: string): Observable<any> {
    return this.permissions.abilities$.pipe(
      map((ability) => {
        return ability.onScopeCan(
          action as Action,
          type as ResourceType,
          { scope: qscope.scope as Scope, facilityId: qscope.facilityId },
          token as AccessToken
        );
      })
    );
  }

}
