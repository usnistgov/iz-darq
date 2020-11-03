export interface IToolConfiguration {
  id: string;
  properties: IToolConfigurationKey;
}

export interface IToolConfigurationKey {
  key: string;
  value: string;
  required: boolean;
}

export interface IToolConfigurationKeyValue {
  key: string;
  value: string;
}
