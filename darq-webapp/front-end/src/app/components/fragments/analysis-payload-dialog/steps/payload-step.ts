export abstract class PayloadStep<T> {
	protected constructor(private position: number) {

	}
	abstract finish();
}
