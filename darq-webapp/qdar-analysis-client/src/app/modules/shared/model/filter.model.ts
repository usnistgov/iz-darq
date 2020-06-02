import { IDescriptor } from './descriptor.model';
export enum FilterType {
  OWNED = 'OWNED',
  PUBLIC = 'PUBLIC',
  ALL = 'ALL',
}

export function filterDescriptorByType<T extends IDescriptor>(list: T[], type: FilterType): T[] {
  return list.filter((descriptor) => {
    switch (type) {
      case FilterType.ALL:
        return true;
      case FilterType.OWNED:
        return descriptor.owned;
      case FilterType.PUBLIC:
        return descriptor.published;
    }
  });
}
