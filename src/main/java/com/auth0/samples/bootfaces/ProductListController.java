package com.auth0.samples.bootfaces;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Scope(value = "session")
@Component(value = "productList")
@ELBeanName(value = "productList")
@Join(path = "/", to = "/product-list.jsf")
public class ProductListController {
	@Autowired
	private ProductRepository productRepository;
	private List<Product> products;

	@PostConstruct
	public void init() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		KeycloakSecurityContext securityContext = ((KeycloakPrincipal) request.getUserPrincipal()).getKeycloakSecurityContext();

		final String realm = securityContext.getRealm();
		System.out.println("*** Realm: " + realm);

		IDToken idToken = securityContext.getIdToken();
		final String id = idToken.getId();
		String userName = idToken.getName();
		String email = idToken.getEmail();
		System.out.println("*** Id Token: " + id + "," + userName + "," + email);

		AccessToken accessToken = securityContext.getToken();
		Set<String> userRoles =	accessToken.getRealmAccess().getRoles();
		for (String userRole : userRoles) {
			System.out.println("  User role: " + userRole);
		}

	}

	@Deferred
	@RequestAction
	@IgnorePostback
	public void loadData() {
		products = productRepository.findAll();
	}

	public List<Product> getProducts() {
		return products;
	}
}
