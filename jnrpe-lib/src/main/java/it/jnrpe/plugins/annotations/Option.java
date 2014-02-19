/**
 * <description of class>
 *
 * @author fred
 *
 */
package it.jnrpe.plugins.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Plugin option definition.
 *
 * @author Frederico Campos
 */
@Retention(RetentionPolicy.RUNTIME)

public @interface Option {
	String shortName();
	String longName();
	String description();
	String argName();
	boolean optionalArgs();
	boolean hasArgs();
	boolean required();
	String option();
}
