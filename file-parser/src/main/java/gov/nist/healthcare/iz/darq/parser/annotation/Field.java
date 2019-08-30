package gov.nist.healthcare.iz.darq.parser.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
	String name();
	int index() default -1;
	boolean coded() default false;
	String table() default "";
	boolean required() default false;
}
