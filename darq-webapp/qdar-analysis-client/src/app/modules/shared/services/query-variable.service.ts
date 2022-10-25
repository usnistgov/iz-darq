import { IQueryVariableDisplay } from './../components/variable-ref-display/variable-ref-display.component';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IQueryVariable } from '../model/query-variable.model';

@Injectable({
    providedIn: 'root'
})
export class QueryVariableService {
    constructor(private http: HttpClient) { }

    getVariables(): Observable<IQueryVariable[]> {
        return this.http.get<IQueryVariable[]>('/api/query-variable/');
    }


    getVariablesDisplay(): Observable<IQueryVariableDisplay[]> {
        return this.http.get<IQueryVariableDisplay[]>('/api/query-variable/display');
    }
}
