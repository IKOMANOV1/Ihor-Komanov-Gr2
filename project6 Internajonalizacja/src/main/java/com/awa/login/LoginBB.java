package com.awa.login;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.simplesecurity.RemoteClient;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.awa.dao.UserDAO;
import com.awa.entities.User;

@Named
@RequestScoped
public class LoginBB {
	private static final String PAGE_MAIN = "personList?faces-redirect=true";
	private static final String PAGE_LOGIN = "/login";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private String login;
	private String pass;
        
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@Inject
	UserDAO userDAO;

	public String doLogin() {
		FacesContext ctx = FacesContext.getCurrentInstance();

		// 1. verify login and password - get User from "database"
		User user = userDAO.loginUser(login, pass);

		// 2. if bad login or password - stay with error info
		if (user == null) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Niepoprawny login lub hasło", null));
			return PAGE_STAY_AT_THE_SAME;
		}

		// 3. if logged in: get User roles, save in RemoteClient and store it in session
		
		RemoteClient<User> client = new RemoteClient<User>(); //create new RemoteClient
                client.setDetails(user);
		
		List<String> roles = userDAO.getUserRole(user); //get User roles 
		
		if (roles != null) { //save roles in RemoteClient
			for (String role: roles) {
				client.getRoles().add(role);
			}
		}
	
		//store RemoteClient with request info in session (needed for SecurityFilter)
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		client.store(request);

		// and enter the system (now SecurityFilter will pass the request)
		return PAGE_MAIN;
	}
	
	public String doLogout(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		//Invalidate session
		// - all objects within session will be destroyed
		// - new session will be created (with new ID)
		session.invalidate();
		return PAGE_LOGIN;
	}
	
        public List<String>  getUserRole() {
    if (login == null) return null; // или если user хранится — то проверяй user    
    User user = userDAO.loginUser(login, pass);
    if (user == null) return null;

            return userDAO.getUserRole(user);
}
}
