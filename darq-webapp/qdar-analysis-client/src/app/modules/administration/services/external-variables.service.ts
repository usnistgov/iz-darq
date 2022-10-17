import { Observable } from 'rxjs';
import {
    ExternalQueryVariableScope,
    IExternalQueryVariable,
    IGlobalExternalQueryVariable,
    QueryVariableType,
    IIISExternalQueryVariable,
    IIISVariableValue
} from './../../shared/model/query-variable.model';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IMessage } from 'ngx-dam-framework';

@Injectable({
    providedIn: 'root'
})
export class ExternalVariableService {

    readonly URL_PREFIX = 'api/external-variable/';
    constructor(private http: HttpClient) { }

    createVariable(v: IExternalQueryVariable): Observable<IMessage<IExternalQueryVariable>> {
        return this.http.post<IMessage<IExternalQueryVariable>>(this.URL_PREFIX + 'create', v);
    }

    saveVariable(v: IExternalQueryVariable): Observable<IMessage<IExternalQueryVariable>> {
        return this.http.post<IMessage<IExternalQueryVariable>>(this.URL_PREFIX + 'update', v);
    }

    deleteVariable(id: string): Observable<IMessage<IExternalQueryVariable>> {
        return this.http.delete<IMessage<IExternalQueryVariable>>(this.URL_PREFIX + 'delete/' + id);
    }


    getVariables(): Observable<IExternalQueryVariable[]> {
        return this.http.get<IExternalQueryVariable[]>(this.URL_PREFIX);
    }

    getEmptyGlobalVariable(): IGlobalExternalQueryVariable {
        return {
            id: '',
            name: '',
            description: '',
            type: QueryVariableType.EXTERNAL,
            scope: ExternalQueryVariableScope.GLOBAL,
            value: 0,
            comment: '',
            tags: [],
            dateValueUpdated: undefined,
            dateCreated: undefined,
            dateUpdated: undefined,
        };
    }

    getEmptyIISVariable(): IIISExternalQueryVariable {
        return {
            id: '',
            name: '',
            description: '',
            type: QueryVariableType.EXTERNAL,
            scope: ExternalQueryVariableScope.IIS,
            values: [],
            tags: [],
            dateCreated: undefined,
            dateUpdated: undefined,
        };
    }

    getEmptyIISVariableValue(): IIISVariableValue {
        return {
            value: 0,
            comment: '',
            dateValueUpdated: undefined,
            dateUpdated: undefined,
            facilityId: '',
        };
    }
}
