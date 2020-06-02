import * as CoreActions from './core.actions';

describe('Core', () => {
  it('should create an instance', () => {
    expect(new CoreActions.LoadCores()).toBeTruthy();
  });
});
