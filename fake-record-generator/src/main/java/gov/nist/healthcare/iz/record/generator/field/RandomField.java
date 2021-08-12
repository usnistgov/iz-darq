package gov.nist.healthcare.iz.record.generator.field;

public class RandomField extends Field {
    private int minLength = 0;
    private int maxLength = 10;
    private boolean uppercase;
    private ValueFormat format = ValueFormat.ALPHABETIC;

    public RandomField() {
        super(FieldType.RANDOM);
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isUppercase() {
        return uppercase;
    }

    public void setUppercase(boolean uppercase) {
        this.uppercase = uppercase;
    }

    public ValueFormat getFormat() {
        return format;
    }

    public void setFormat(ValueFormat format) {
        this.format = format;
    }
}