import { IRange } from '../../shared/model/age-group.model';
import { IDamResource } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';
import { IDescriptor } from '../../shared/model/descriptor.model';

export interface IConfigurationDescriptor extends IDescriptor, IDamResource {
  type: EntityType.CONFIGURATION;
}

export interface IDigestConfiguration extends IConfigurationDescriptor {
  description: string;
  payload: IConfigurationPayload;
}

export interface IConfigurationPayload {
  ageGroups: IRange[];
  detections: string[];
  asOf?: string;
  activatePatientMatching?: boolean;
  mismoPatientMatchingConfiguration?: string;
  complexDetections?: IComplexDetection[];
}

export enum ComplexDetectionTarget {
  RECORD = 'RECORD',
  VACCINATION = 'VACCINATION'
}

export enum ExpressionType {
  AND = 'AND',
  OR = 'OR',
  XOR = 'XOR',
  NOT = 'NOT',
  IMPLY = 'IMPLY',
  DETECTION = 'DETECTION'
}

export interface IExpression {
  type: ExpressionType;
}

export interface IANDExpression {
  expressions: IExpression[];
  type: ExpressionType.AND;
}

export interface IORExpression {
  expressions: IExpression[];
  type: ExpressionType.OR;
}

export interface IXORExpression {
  expressions: IExpression[];
  type: ExpressionType.XOR;
}

export interface IIMPLYExpression {
  left?: IExpression;
  right?: IExpression;
  type: ExpressionType.IMPLY;
}

export interface INOTExpression {
  expression?: IExpression;
  type: ExpressionType.NOT;
}

export interface IDetectionExpression {
  code: string;
  type: ExpressionType.DETECTION;
}


export interface IComplexDetection {
  code: string;
  target: ComplexDetectionTarget;
  expression?: IExpression;
  description: string;
}
