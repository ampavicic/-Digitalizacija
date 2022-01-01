package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

//Samo jednom mozemo ovu anotaciju koristiti
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    //Definirali smo je u app.properties (baza PostgreSQL) (zato je Autowired)
    @Autowired
    DataSource dataSource;

    //Konfigutiramo koje podatke vuci iz baze kad trebamo korisnika autentificirat
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(new BCryptPasswordEncoder).

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("SELECT username, password, enabled FROM useraccount WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT useraccount.username, role.name FROM useraccount INNER JOIN employee ON useraccount.genid = employee.genid INNER JOIN role ON employee.roleid = role.roleid WHERE username = ?;");
        //Defaultne dvije funkcije koje se pozivaju pri autentifikaciji


    }

    //Konfiguriramo sto raditi u slucaju dolazenja http zahtjeva
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                //.antMatchers("/director").hasRole("DIRECTOR") /*Samo korisnik s ulogom director može pristupiti tom zahtjevu*/
                .antMatchers("/director/**").hasRole("DIRECTOR")
                .antMatchers("/employee/**").hasRole("EMPLOYEE")
                .antMatchers("/").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/login").permitAll().and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/user").failureForwardUrl("/login?error=true").and()
                .logout().clearAuthentication(true).deleteCookies().invalidateHttpSession(true).logoutSuccessUrl("/");

        /*.formLogin(form -> form.defaultSuccessUrl("/userpage").failureForwardUrl("/login?error=true")).*/

        http.httpBasic(); //Osnovna konfiguracija
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login"); //Defaultna, bitna kod login formi

    }

}
