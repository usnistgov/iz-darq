package gov.nist.healthcare.iz.darq.controller.route;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.domain.LoginResponse;
import gov.nist.healthcare.auth.domain.User;
import gov.nist.healthcare.auth.service.AccountService;

@RestController
@RequestMapping("/api")
public class AuthController {
	
	@Autowired
	private AccountService accountService;
	
	@RequestMapping(value="/me", method=RequestMethod.GET)
	@ResponseBody
	public LoginResponse me(){
		Account a = this.accountService.getCurrentUser();
		if(a != null){
			return new LoginResponse(true,"me",new User(a.getId(), a.getUsername(), new ArrayList<>(a.getPrivileges())));
		}
		else {
			return new LoginResponse(false,"no user",null);
		}
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	@ResponseBody
	public void logout(HttpServletRequest req, HttpServletResponse res){
		Cookie authCookie = new Cookie("authCookie", "");
		authCookie.setPath("/api");
		authCookie.setMaxAge(0);
		authCookie.setHttpOnly(true);
		res.addCookie(authCookie);
	}
}
