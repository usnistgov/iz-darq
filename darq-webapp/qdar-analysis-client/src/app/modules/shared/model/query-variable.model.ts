export enum QueryVariableType {
    ADF = 'ADF',
    EXTERNAL = 'EXTERNAL',
}

export enum ExternalQueryVariableScope {
    IIS = 'IIS',
    GLOBAL = 'GLOBAL',
}

export interface IQueryVariable {
    id: string;
    name: string;
    description: string;
    type: QueryVariableType;
}

export interface IADFQueryVariable extends IQueryVariable {
    type: QueryVariableType.ADF;
}

export interface IExternalQueryVariable extends IQueryVariable {
    type: QueryVariableType.EXTERNAL;
    scope: ExternalQueryVariableScope;
    dateCreated: Date;
    tags: string[];
    dateUpdated: Date;
}

export interface IGlobalExternalQueryVariable extends IExternalQueryVariable {
    scope: ExternalQueryVariableScope.GLOBAL;
    value: number;
    dateValueUpdated: string;
    comment?: string;
}

export interface IIISExternalQueryVariable extends IExternalQueryVariable {
    scope: ExternalQueryVariableScope.IIS;
    values: IIISVariableValue[];
}

export interface IIISVariableValue {
    value: number;
    dateValueUpdated: string;
    dateUpdated: Date;
    facilityId: string;
    comment?: string;
}
