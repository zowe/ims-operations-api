package application.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//    	http
//    	.csrf().disable()
//    	.authorizeRequests().anyRequest().authenticated()
//        .antMatchers(HttpMethod.GET,"/*").permitAll()
//        .and()
//        .httpBasic();
    	
    	 /*
    	  * Currently roles are partially working;
    	  * 1.) Passing in the correct password first works as expected, but if you alter the password,
    	  * the change is not recognized and the authorization is allowed (incorrect functionality)
    	  * 2.) Putting in the incorrect user gives a 404 Error not 401 unauthorized
    	  *
    	  *
    	  * Also, the custom roles are not being recognized
    	  */
    	 
        http
         .csrf().disable()
         .authorizeRequests()
         
         // HTTP Method users
//         .antMatchers(HttpMethod.GET).hasAnyRole("get-user", "ims-admin")
//         .antMatchers(HttpMethod.PUT).hasAnyRole("put-user", "ims-admin")
//         .antMatchers(HttpMethod.POST).hasAnyRole("post-user", "ims-admin")
 
         // Program user
         .antMatchers(HttpMethod.GET, "/**/program").hasRole("ADMIN")
//         .antMatchers(HttpMethod.GET, "/**/program").hasAnyRole("pgm-user","ims-admin")
//         .antMatchers(HttpMethod.POST, "/**/program").hasAnyRole("pgm-user","ims-admin")
//         .antMatchers(HttpMethod.PUT, "/**/program").hasAnyRole("pgm-user","ims-admin")
//         .antMatchers(HttpMethod.DELETE, "/**/program").hasAnyRole("pgm-user","ims-admin")
//         
//         // Region user
//         .antMatchers(HttpMethod.GET, "/**/region").hasAnyRole("region-user","ims-admin")
//         .antMatchers(HttpMethod.PUT, "/**/region/**").hasAnyRole("region-user","ims-admin")
//         
//         // Transaction user
//         .antMatchers(HttpMethod.GET, "/**/transaction").hasAnyRole("tran-user","ims-admin")
//         .antMatchers(HttpMethod.POST, "/**/transaction").hasAnyRole("tran-user","ims-admin")
//         .antMatchers(HttpMethod.PUT, "/**/transaction").hasAnyRole("tran-user","ims-admin")
//         .antMatchers(HttpMethod.DELETE, "/**/transaction").hasAnyRole("tran-user","ims-admin")
        
         .and()
         .httpBasic();
    }
  
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
//        	.withUser("admin").password(passwordEncoder().encode("password")).roles("ims-admin", "get-user", "put-user", "post-user", "pgm-user", "tran-user", "region-user").and()
//            .withUser("get-user").password(passwordEncoder().encode("password")).roles("get-user").and()
//            .withUser("put-user").password(passwordEncoder().encode("password")).roles("put-user").and()
//            .withUser("post-user").password(passwordEncoder().encode("password")).roles("post-user").and()
//            .withUser("pgm-user").password(passwordEncoder().encode("password")).roles("pgm-user").and()
//            .withUser("tran-user").password(passwordEncoder().encode("password")).roles("tran-user").and()
//            .withUser("region-user").password(passwordEncoder().encode("password")).roles("region-user");
        .withUser("admin").password(passwordEncoder().encode("secret")).roles("ADMIN");
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
