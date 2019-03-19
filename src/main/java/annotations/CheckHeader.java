/**
 *  Copyright IBM Corporation 2018, 2019
 */

package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckHeader {}
