import { Pipe, PipeTransform } from '@angular/core';
import { PermissionService } from '../../core/services/permission.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Action } from '../../core/model/action.enum';
import { ResourceType } from '../../core/model/resouce-type.enum';

@Pipe({
  name: 'accessOnResource'
})
export class AccessResourcePipe implements PipeTransform {

  constructor(
    private permissions: PermissionService
  ) { }

  transform(action: string, value: any, type: string): Observable<any> {
    return this.permissions.abilities$.pipe(
      map((ability) => {
        return ability.onResourceCan(action as Action, type as ResourceType, value);
      })
    );
  }

}
