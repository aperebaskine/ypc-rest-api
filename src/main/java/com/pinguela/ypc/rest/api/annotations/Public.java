package com.pinguela.ypc.rest.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.ws.rs.NameBinding;

/**
 * This annotation is used to declare endpoints (either class-level or method-level) that do *not* require authentication.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Public {

}
