package org.keycloak.social.apple;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.common.util.Time;
import org.keycloak.crypto.Algorithm;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.crypto.ServerECDSASignatureSignerContext;
import org.keycloak.crypto.SignatureSignerContext;
import org.keycloak.events.EventBuilder;
import org.keycloak.jose.jws.JWSBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.vault.VaultStringSecret;

import com.fasterxml.jackson.databind.JsonNode;

public class AppleIdentityProvider extends AbstractOAuth2IdentityProvider implements SocialIdentityProvider {
	
	protected static final Logger logger = Logger.getLogger(AppleIdentityProvider.class);
	public static final String AUTH_URL = "https://appleid.apple.com/auth/authorize";
	public static final String TOKEN_URL = "https://appleid.apple.com/auth/token";
	public static final String PROFILE_URL = "https://appleid.apple.com";
	public static final String DEFAULT_SCOPE = "name email";
	
	public AppleIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
		super(session, config);
		config.setAuthorizationUrl(AUTH_URL);
		config.setTokenUrl(TOKEN_URL);
		config.setUserInfoUrl(PROFILE_URL);
	}
	
	

	protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
		try {
			
			JsonNode profile = SimpleHttp.doGet(PROFILE_URL, session).header("Authorization", "Bearer " + accessToken).asJson();
			return extractIdentityFromProfile(null, profile);
		} catch (Exception e) {
			throw new IdentityBrokerException("Could not obtain user profile from apple.", e);
		}
	}
	
	@Override
	public SimpleHttp authenticateTokenRequest(final SimpleHttp tokenRequest) {
	        if (getConfig().isJWTAuthentication()) {
	            String jws = new JWSBuilder().type(OAuth2Constants.JWT).jsonContent(generateToken()).sign(getSignatureContext());
	            return tokenRequest
	                    .param(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT)
	                    .param(OAuth2Constants.CLIENT_ASSERTION, jws);
	        } else {
	            try (VaultStringSecret vaultStringSecret = session.vault().getStringSecret(GenerateClientSecret())) {
	                if (getConfig().isBasicAuthentication()) {
	                    return tokenRequest.authBasic(getConfig().getClientId(), vaultStringSecret.get().orElse(GenerateClientSecret()));
	                }
	                return tokenRequest
	                        .param(OAUTH2_PARAMETER_CLIENT_ID, getConfig().getClientId())
	                        .param(OAUTH2_PARAMETER_CLIENT_SECRET, GenerateClientSecret());
	            }
	        }
	    }
	
	public String GenerateClientSecret() {
		 AppleIdentityProviderConfig config = (AppleIdentityProviderConfig) getConfig();
	     String base64PrivateKey = config.getClientSecret();
	     String clientSecret = "";
	        try {
	            KeyFactory keyFactory = KeyFactory.getInstance("EC");
	            byte[] pkc8ePrivateKey = Base64.getDecoder().decode(base64PrivateKey);
	            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(pkc8ePrivateKey);
	            PrivateKey privateKey = keyFactory.generatePrivate(keySpecPKCS8);

	            KeyWrapper keyWrapper = new KeyWrapper();
	            keyWrapper.setAlgorithm(Algorithm.ES256);
	            keyWrapper.setKid(config.getKeyId());
	            keyWrapper.setPrivateKey(privateKey);
	            SignatureSignerContext signer = new ServerECDSASignatureSignerContext(keyWrapper);

	            long currentTime = Time.currentTime();
	            JsonWebToken token = new JsonWebToken();
	            token.issuer(config.getTeamId());
	            token.issuedAt((int) currentTime);
	            token.expiration((int) currentTime + 15 * 60);
	            token.audience("https://appleid.apple.com");
	            token.subject(config.getClientId());
	            clientSecret = new JWSBuilder().jsonContent(token).sign(signer);
	        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
	            logger.error("Failed to generate client secret: %s", e);
	        }
	        return clientSecret;
	}


	@Override
	protected boolean supportsExternalExchange() {
		logger.info("example2");
		return true;
	}
	@Override
	protected String getProfileEndpointForValidation(EventBuilder event) {
		logger.info("example1");
		return PROFILE_URL;
	}

 
	

	@Override
	protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
		logger.info("example");
		String id = getJsonProperty(profile, "id");

		BrokeredIdentityContext user = new BrokeredIdentityContext(id);

		String email = getJsonProperty(profile, "email");

		user.setEmail(email);

		String username = getJsonProperty(profile, "username");

		if (username == null) {
            if (email != null) {
                username = email;
            } else {
                username = id;
            }
        }

		user.setUsername(username);

		String firstName = getJsonProperty(profile, "first_name");
		String lastName = getJsonProperty(profile, "last_name");

		if (lastName == null) {
            lastName = "";
        } else {
            lastName = " " + lastName;
        }

		user.setName(firstName + lastName);
		user.setIdpConfig(getConfig());
		user.setIdp(this);

		AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());

		return user;
	}

	@Override
	protected String getDefaultScopes() {
		return DEFAULT_SCOPE;
	}

}