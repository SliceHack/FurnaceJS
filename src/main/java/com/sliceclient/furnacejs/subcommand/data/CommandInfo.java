package com.sliceclient.furnacejs.subcommand.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The command info annotation
 *
 * @author Nick
 * @since 9/22/2022 3:40 PM
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String name();
    String[] aliases() default {};
    String permission();
}
