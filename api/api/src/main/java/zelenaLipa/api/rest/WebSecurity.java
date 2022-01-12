package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //Two important querries that are being sent to DB while logging in
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("SELECT username, password, enabled FROM useraccount WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT useraccount.username, role.name FROM useraccount INNER JOIN employee ON useraccount.genid = employee.genid INNER JOIN role ON employee.roleid = role.roleid WHERE username = ?;");

    }

    //Konfiguriramo sto raditi u slucaju dolazenja http zahtjeva
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/user/**").hasAnyRole("EMPLOYEE", "DIRECTOR", "REVISER", "ACCOUNTANT", "ACCOUNTANT_INT4", "ACCOUNTANT_R6", "ACCOUNTANT_P9")
                .antMatchers("/director/**").hasRole("DIRECTOR")
                .antMatchers("/reviser/**").hasRole("REVISER")
                .antMatchers("/accountant/**").hasAnyRole("ACCOUNTANT", "ACCOUNTANT_INT4", "ACCOUNTANT_R6", "ACCOUNTANT_P9")
                .antMatchers("/").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/login").permitAll().and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/user").failureForwardUrl("/login?error=true").and()
                .logout().clearAuthentication(true).deleteCookies().invalidateHttpSession(true).logoutSuccessUrl("/");

        http.httpBasic(); //Basic config
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login");

    }

}
