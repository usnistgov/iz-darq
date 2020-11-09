import { IDescriptor } from './descriptor.model';
export enum FilterType {
  OWNED = 'OWNED',
  PUBLIC = 'PUBLIC',
  ALL = 'ALL',
}

export function filterDescriptorByType<T extends IDescriptor>(list: T[], type: FilterType, userId: string): T[] {
  return list.filter((descriptor) => {
    switch (type) {
      case FilterType.ALL:
        return true;
      case FilterType.OWNED:
        return descriptor.owner === userId;
      case FilterType.PUBLIC:
        return descriptor.public;
    }
  });
}
