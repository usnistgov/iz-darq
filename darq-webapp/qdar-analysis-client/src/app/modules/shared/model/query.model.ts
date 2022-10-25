import { IConfigurationPayload } from './../../configuration/model/configuration.model';
import { IQueryPayload } from './../../report-template/model/report-template.model';
import { IConfigurationDescriptor } from 'src/app/modules/configuration/model/configuration.model';
import { EntityType } from 'src/app/modules/shared/model/entity.model';
import { IDescriptor } from './descriptor.model';


export interface IQueryDescriptor extends IDescriptor {
    type: EntityType.QUERY;
    compatibilities: IConfigurationDescriptor[];
}

export interface IQuery extends IQueryDescriptor {
    type: EntityType.QUERY;
    query: IQueryPayload;
    configuration: IConfigurationPayload;
}
