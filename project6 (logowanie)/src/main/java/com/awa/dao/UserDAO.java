package com.awa.dao;

import com.awa.entities.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

@Stateless
public class UserDAO {

    private final static String UNIT_NAME = "Projectbd_PU";

    @PersistenceContext(unitName = UNIT_NAME)
    protected EntityManager em;



    public void create(User user) {
        em.persist(user);
    }
   public User loginUser(String username, String password) {
    if (username == null || password == null) {
        return null;
    }

    try {
      
        return em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username AND u.passwordhash = :password",
                User.class)
                .setParameter("username", username)
                .setParameter("password", password) 
                .getSingleResult();
    } catch (NoResultException e) {
        return null;
    }
}

  
    public User findByEmail(String email) {
        try {
            return em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

   
    public User login(String username, String passwordHash) {
        try {
            return em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username AND u.passwordhash = :password", 
                    User.class)
                    .setParameter("username", username)
                    .setParameter("password", passwordHash)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; 
        }
    }
    
   public List<String> getUserRole(User user) {
    if (user == null || user.getRole() == null) {
        return List.of(); 
    }

    return List.of(user.getRole().getName());
}
}