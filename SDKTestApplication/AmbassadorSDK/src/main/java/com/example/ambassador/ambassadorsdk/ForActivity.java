package com.example.ambassador.ambassadorsdk;

/**
 * Created by coreyfields on 9/22/15.
 */
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier @Retention(RUNTIME)
public @interface ForActivity {
}