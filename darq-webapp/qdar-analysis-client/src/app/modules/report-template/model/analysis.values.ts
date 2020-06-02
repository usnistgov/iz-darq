export enum AnalysisType {
  VACCINCATIONS = 'V',
  VACCINCATIONS_DETECTIONS = 'VD',
  VACCINCATIONS_VOCABULARY = 'VT',
  PATIENTS_DETECTIONS = 'PD',
  PATIENTS_VOCABULARY = 'PT'
}
export enum Field {
  PROVIDER = 'PROVIDER',
  AGE_GROUP = 'AGE_GROUP',
  TABLE = 'TABLE',
  CODE = 'CODE',
  DETECTION = 'DETECTION',
  EVENT = 'EVENT',
  GENDER = 'GENDER',
  VACCINE_CODE = 'VACCINE_CODE',
  VACCINATION_YEAR = 'VACCINATION_YEAR'
}

export interface Compatibility {
  [index: string]: Field[];
}

export const fieldsForAnalysis: Compatibility = {
  [AnalysisType.VACCINCATIONS]: [Field.PROVIDER, Field.AGE_GROUP, Field.EVENT, Field.GENDER, Field.VACCINATION_YEAR, Field.VACCINE_CODE],
  [AnalysisType.VACCINCATIONS_DETECTIONS]: [Field.PROVIDER, Field.AGE_GROUP, Field.DETECTION],
  [AnalysisType.VACCINCATIONS_VOCABULARY]: [Field.PROVIDER, Field.AGE_GROUP, Field.TABLE, Field.CODE],
  [AnalysisType.PATIENTS_DETECTIONS]: [Field.AGE_GROUP, Field.DETECTION],
  [AnalysisType.PATIENTS_VOCABULARY]: [Field.AGE_GROUP, Field.TABLE, Field.CODE]
};

export const names = {
  VACCINCATIONS: 'Vaccination Events',
  VACCINCATIONS_DETECTIONS: 'Vaccination Related Detections',
  VACCINCATIONS_VOCABULARY: 'Vaccination Related Code Table',
  PATIENTS_DETECTIONS: 'Patient Related Detections',
  PATIENTS_VOCABULARY: 'Patient Related Code Table'
};
