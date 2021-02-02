package org.keycloak.social.apple;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.AuthenticationRequest;
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
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.endpoints.AuthorizationEndpoint;
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
	protected UriBuilder createAuthorizationUrl(AuthenticationRequest request) {
        final UriBuilder uriBuilder = UriBuilder.fromUri(getConfig().getAuthorizationUrl())
                .queryParam(OAUTH2_PARAMETER_SCOPE, getConfig().getDefaultScope())
                .queryParam(OAUTH2_PARAMETER_STATE, request.getState().getEncoded())
                .queryParam(OAUTH2_PARAMETER_RESPONSE_TYPE, "code")
                .queryParam(OAUTH2_PARAMETER_CLIENT_ID, getConfig().getClientId())
				.queryParam("response_mode", "form_post")
                .queryParam(OAUTH2_PARAMETER_REDIRECT_URI, request.getRedirectUri());

        String loginHint = request.getAuthenticationSession().getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);
        if (getConfig().isLoginHint() && loginHint != null) {
            uriBuilder.queryParam(OIDCLoginProtocol.LOGIN_HINT_PARAM, loginHint);
        }

        if (getConfig().isUiLocales()) {
            uriBuilder.queryParam(OIDCLoginProtocol.UI_LOCALES_PARAM, session.getContext().resolveLocale(null).toLanguageTag());
        }

        String prompt = getConfig().getPrompt();
        if (prompt == null || prompt.isEmpty()) {
            prompt = request.getAuthenticationSession().getClientNote(OAuth2Constants.PROMPT);
        }
        if (prompt != null) {
            uriBuilder.queryParam(OAuth2Constants.PROMPT, prompt);
        }

        String nonce = request.getAuthenticationSession().getClientNote(OIDCLoginProtocol.NONCE_PARAM);
        if (nonce == null || nonce.isEmpty()) {
            nonce = UUID.randomUUID().toString();
            request.getAuthenticationSession().setClientNote(OIDCLoginProtocol.NONCE_PARAM, nonce);
        }
        uriBuilder.queryParam(OIDCLoginProtocol.NONCE_PARAM, nonce);

        String acr = request.getAuthenticationSession().getClientNote(OAuth2Constants.ACR_VALUES);
        if (acr != null) {
            uriBuilder.queryParam(OAuth2Constants.ACR_VALUES, acr);
        }
        String forwardParameterConfig = getConfig().getForwardParameters() != null ? getConfig().getForwardParameters(): "";
        List<String> forwardParameters = Arrays.asList(forwardParameterConfig.split("\\s*,\\s*"));
        for(String forwardParameter: forwardParameters) {
            String name = AuthorizationEndpoint.LOGIN_SESSION_NOTE_ADDITIONAL_REQ_PARAMS_PREFIX + forwardParameter.trim();
            String parameter = request.getAuthenticationSession().getClientNote(name);
            if(parameter != null && !parameter.isEmpty()) {
                uriBuilder.queryParam(forwardParameter, parameter);
            }
        }
        return uriBuilder;
    }
	
	@Override
	public SimpleHttp authenticateTokenRequest(final SimpleHttp tokenRequest) {
	        if (getConfig().isJWTAuthentication()) {
	            String jws = new JWSBuilder().type(OAuth2Constants.JWT).jsonContent(generateToken()).sign(getSignatureContext());
	            return tokenRequest
	                    .param(OAuth2Constants.CLIENT_ASSERTION_TYPE, OAuth2Constants.CLIENT_ASSERTION_TYPE_JWT)
	                    .param(OAuth2Constants.CLIENT_ASSERTION, jws);
	        } else {
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
		        }catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
		            logger.error("Failed to generate client secret: %s", e);
		        }
	            try (VaultStringSecret vaultStringSecret = session.vault().getStringSecret(clientSecret)) {
	                if (getConfig().isBasicAuthentication()) {
	                    return tokenRequest.authBasic(getConfig().getClientId(), vaultStringSecret.get().orElse(clientSecret));
	                }
	                return tokenRequest
	                        .param(OAUTH2_PARAMETER_CLIENT_ID, getConfig().getClientId())
	                        .param(OAUTH2_PARAMETER_CLIENT_SECRET, clientSecret);
	            }
	        }
	    }
	


	@Override
	protected boolean supportsExternalExchange() {
		return true;
	}
	@Override
	protected String getProfileEndpointForValidation(EventBuilder event) {
		return PROFILE_URL;
	}

 
	

	@Override
	protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
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