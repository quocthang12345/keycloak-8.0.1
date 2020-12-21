package org.keycloak.social.apple;

import org.keycloak.broker.oidc.OIDCIdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

public class AppleIdentityProviderConfig extends OIDCIdentityProviderConfig {

    AppleIdentityProviderConfig(IdentityProviderModel identityProviderModel) {
        super(identityProviderModel);
    }

    public String getKeyId() {
        return getConfig().get("keyId");
    }

    public String getTeamId() {
        return getConfig().get("teamId");
    }
}
