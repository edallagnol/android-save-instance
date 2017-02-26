package com.edallagnol.androidsaveinstance;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Save {
	Class<? extends Bundler> value() default Bundler.VoidBundler.class;
}
