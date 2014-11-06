package org.amaze.db.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Index
{
	
	String indexName() default "";

	String isUnique() default "false";

	String isClustered() default "false";

	String isBusinessConstraint() default "false";

	String displayName() default "false";

	String[] columnNames() default "";
}
