package gov.nist.healthcare.iz.darq.controller.route;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.web.bind.annotation.*;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.domain.User;
import gov.nist.healthcare.auth.service.AccountService;

@RestController
@RequestMapping("/api")
public class AuthController {
	
	@Autowired
	private AccountService accountService;
	
	@RequestMapping(value="/me", method=RequestMethod.GET)
	@ResponseBody
	public User me(){
		Account a = this.accountService.getCurrentUser();
		if(a != null){
			return new User(a.getId(), a.getUsername(), new ArrayList<>(a.getPrivileges()));
		}
		else {
			throw new CredentialsExpiredException("");
		}
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	@ResponseBody
	public void logout(HttpServletRequest req, HttpServletResponse res){
		Cookie authCookie = new Cookie("authCookie", "");
		authCookie.setPath("/");
		authCookie.setMaxAge(0);
		authCookie.setHttpOnly(true);
		res.addCookie(authCookie);
	}
}
