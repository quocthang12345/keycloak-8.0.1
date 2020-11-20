/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.testsuite.federation.storage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.events.Details;
import org.keycloak.models.ClientModel;
import org.keycloak.models.Constants;
import org.keycloak.models.RealmModel;
import org.keycloak.models.cache.infinispan.ClientAdapter;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.storage.CacheableStorageProviderModel;
import org.keycloak.storage.client.ClientStorageProvider;
import org.keycloak.storage.client.ClientStorageProviderModel;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.federation.HardcodedClientStorageProviderFactory;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.runonserver.RunOnServerDeployment;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.util.BasicAuthHelper;
import org.keycloak.util.TokenUtil;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static org.junit.Assert.assertEquals;
import static org.keycloak.testsuite.admin.ApiUtil.findUserByUsername;

/**
 * Test that clients can override auth flows
 *
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 */
public class ClientStorageTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    @Override
    public void configureTestRealm(RealmRepresentation testRealm) {
    }

    protected String providerId;

    @Deployment
    public static WebArchive deploy() {
        return RunOnServerDeployment.create(UserResource.class)
                .addPackages(true, "org.keycloak.testsuite");
    }

    protected String addComponent(ComponentRepresentation component) {
        Response resp = adminClient.realm("test").components().add(component);
        resp.close();
        String id = ApiUtil.getCreatedId(resp);
        getCleanup().addComponentId(id);
        return id;
    }

    @Before
    public void addProvidersBeforeTest() throws URISyntaxException, IOException {
        ComponentRepresentation provider = new ComponentRepresentation();
        provider.setName("client-storage-hardcoded");
        provider.setProviderId(HardcodedClientStorageProviderFactory.PROVIDER_ID);
        provider.setProviderType(ClientStorageProvider.class.getName());
        provider.setConfig(new MultivaluedHashMap<>());
        provider.getConfig().putSingle(HardcodedClientStorageProviderFactory.CLIENT_ID, "hardcoded-client");
        provider.getConfig().putSingle(HardcodedClientStorageProviderFactory.REDIRECT_URI, oauth.getRedirectUri());

        providerId = addComponent(provider);
    }

    protected String userId;

    @Before
    public void clientConfiguration() {
        userId = findUserByUsername(adminClient.realm("test"), "test-user@localhost").getId();
        oauth.clientId("hardcoded-client");
    }





    @Test
    public void testClientStats() throws Exception {
        testDirectGrant("hardcoded-client");
        testDirectGrant("hardcoded-client");
        testBrowser("test-app");
        offlineTokenDirectGrantFlowNoRefresh();
        List<Map<String, String>> list = adminClient.realm("test").getClientSessionStats();
        boolean hardTested = false;
        boolean testAppTested = false;
        for (Map<String, String> entry : list) {
            if (entry.get("clientId").equals("hardcoded-client")) {
                Assert.assertEquals("3", entry.get("active"));
                Assert.assertEquals("1", entry.get("offline"));
                hardTested = true;
            } else if (entry.get("clientId").equals("test-app")) {
                Assert.assertEquals("1", entry.get("active"));
                Assert.assertEquals("0", entry.get("offline"));
                testAppTested = true;
            }
        }
        Assert.assertTrue(hardTested && testAppTested);
    }


    @Test
    public void testBrowser() throws Exception {
        String clientId = "hardcoded-client";
        testBrowser(clientId);
        //Thread.sleep(10000000);
    }

     private void testBrowser(String clientId) {
        oauth.clientId(clientId);
        String loginFormUrl = oauth.getLoginFormUrl();
        //log.info("loginFormUrl: " + loginFormUrl);

        //Thread.sleep(10000000);

        driver.navigate().to(loginFormUrl);

        loginPage.assertCurrent();

        // Fill username+password. I am successfully authenticated
        oauth.fillLoginForm("test-user@localhost", "password");
        appPage.assertCurrent();

        events.expectLogin().client(clientId).detail(Details.USERNAME, "test-user@localhost").assertEvent();

        String code = oauth.getCurrentQuery().get(OAuth2Constants.CODE);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        Assert.assertNotNull(tokenResponse.getAccessToken());
        Assert.assertNotNull(tokenResponse.getRefreshToken());

        events.clear();

    }

    @Test
    public void testGrantAccessTokenNoOverride() throws Exception {
        testDirectGrant("hardcoded-client");
    }

    private void testDirectGrant(String clientId) {
        Client httpClient = javax.ws.rs.client.ClientBuilder.newClient();
        String grantUri = oauth.getResourceOwnerPasswordCredentialGrantUrl();
        WebTarget grantTarget = httpClient.target(grantUri);

        {   // test no password
            String header = BasicAuthHelper.createHeader(clientId, "password");
            Form form = new Form();
            form.param(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
            form.param("username", "test-user@localhost");
            Response response = grantTarget.request()
                    .header(HttpHeaders.AUTHORIZATION, header)
                    .post(Entity.form(form));
            assertEquals(401, response.getStatus());
            response.close();
        }

        {   // test invalid password
            String header = BasicAuthHelper.createHeader(clientId, "password");
            Form form = new Form();
            form.param(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
            form.param("username", "test-user@localhost");
            form.param("password", "invalid");
            Response response = grantTarget.request()
                    .header(HttpHeaders.AUTHORIZATION, header)
                    .post(Entity.form(form));
            assertEquals(401, response.getStatus());
            response.close();
        }

        {   // test valid password
            String header = BasicAuthHelper.createHeader(clientId, "password");
            Form form = new Form();
            form.param(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
            form.param("username", "test-user@localhost");
            form.param("password", "password");
            Response response = grantTarget.request()
                    .header(HttpHeaders.AUTHORIZATION, header)
                    .post(Entity.form(form));
            assertEquals(200, response.getStatus());
            response.close();
        }

        httpClient.close();
        events.clear();
    }

    @Test
    public void testDailyEviction() {
        testIsCached();

        testingClient.server().run(session -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            Calendar eviction = Calendar.getInstance();
            eviction.add(Calendar.HOUR, 1);
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.EVICT_DAILY);
            model.setEvictionHour(eviction.get(HOUR_OF_DAY));
            model.setEvictionMinute(eviction.get(MINUTE));
            realm.updateComponent(model);
        });
        testIsCached();
        setTimeOffset(2 * 60 * 60); // 2 hours in future
        testNotCached();
        testIsCached();

        setDefaultCachePolicy();
        testIsCached();

    }
    @Test
    public void testWeeklyEviction() {
        testIsCached();

        testingClient.server().run(session -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            Calendar eviction = Calendar.getInstance();
            eviction.add(Calendar.HOUR, 4 * 24);
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.EVICT_WEEKLY);
            model.setEvictionDay(eviction.get(DAY_OF_WEEK));
            model.setEvictionHour(eviction.get(HOUR_OF_DAY));
            model.setEvictionMinute(eviction.get(MINUTE));
            realm.updateComponent(model);
        });
        testIsCached();
        setTimeOffset(2 * 24 * 60 * 60); // 2 days in future
        testIsCached();
        setTimeOffset(5 * 24 * 60 * 60); // 5 days in future
        testNotCached();
        testIsCached();

        setDefaultCachePolicy();
        testIsCached();

    }
    @Test
    public void testMaxLifespan() {
        testIsCached();

        testingClient.server().run(session -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.MAX_LIFESPAN);
            model.setMaxLifespan(1 * 60 * 60 * 1000);
            realm.updateComponent(model);
        });
        testIsCached();

        setTimeOffset(1/2 * 60 * 60); // 1/2 hour in future

        testIsCached();

        setTimeOffset(2 * 60 * 60); // 2 hours in future

        testNotCached();
        testIsCached();

        setDefaultCachePolicy();
        testIsCached();

    }

    private void testNotCached() {
        testingClient.server().run(session -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientModel hardcoded = realm.getClientByClientId("hardcoded-client");
            Assert.assertNotNull(hardcoded);
            Assert.assertFalse(hardcoded instanceof ClientAdapter);
        });
    }


    @Test
    public void testIsCached() {
        testingClient.server().run(session -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientModel hardcoded = realm.getClientByClientId("hardcoded-client");
            Assert.assertNotNull(hardcoded);
            Assert.assertTrue(hardcoded instanceof org.keycloak.models.cache.infinispan.ClientAdapter);
        });
    }


    @Test
    public void testNoCache() {
        testIsCached();

        testingClient.server().run(session -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.NO_CACHE);
            realm.updateComponent(model);
        });

        testNotCached();

        // test twice because updating component should evict
        testNotCached();

        // set it back
        setDefaultCachePolicy();
        testIsCached();


    }

    private void setDefaultCachePolicy() {
        testingClient.server().run(session -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.DEFAULT);
            realm.updateComponent(model);
        });
    }

    @Test
    public void offlineTokenDirectGrantFlow() throws Exception {
        oauth.scope(OAuth2Constants.OFFLINE_ACCESS);
        oauth.clientId("hardcoded-client");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("password", "test-user@localhost", "password");
        Assert.assertNull(tokenResponse.getErrorDescription());
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);

        events.expectLogin()
                .client("hardcoded-client")
                .user(userId)
                .session(token.getSessionState())
                .detail(Details.GRANT_TYPE, OAuth2Constants.PASSWORD)
                .detail(Details.TOKEN_ID, token.getId())
                .detail(Details.REFRESH_TOKEN_ID, offlineToken.getId())
                .detail(Details.REFRESH_TOKEN_TYPE, TokenUtil.TOKEN_TYPE_OFFLINE)
                .detail(Details.USERNAME, "test-user@localhost")
                .removeDetail(Details.CODE_ID)
                .removeDetail(Details.REDIRECT_URI)
                .removeDetail(Details.CONSENT)
                .assertEvent();

        Assert.assertEquals(TokenUtil.TOKEN_TYPE_OFFLINE, offlineToken.getType());
        Assert.assertEquals(0, offlineToken.getExpiration());

        testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), userId);

        // Assert same token can be refreshed again
        testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), userId);
    }
    public void offlineTokenDirectGrantFlowNoRefresh() throws Exception {
        oauth.scope(OAuth2Constants.OFFLINE_ACCESS);
        oauth.clientId("hardcoded-client");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("password", "test-user@localhost", "password");
        Assert.assertNull(tokenResponse.getErrorDescription());
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);
    }

    private String testRefreshWithOfflineToken(AccessToken oldToken, RefreshToken offlineToken, String offlineTokenString,
                                               final String sessionId, String userId) {
        // Change offset to big value to ensure userSession expired
        setTimeOffset(99999);
        Assert.assertFalse(oldToken.isActive());
        Assert.assertTrue(offlineToken.isActive());

        // Assert userSession expired
        testingClient.testing().removeExpired("test");
        try {
            testingClient.testing().removeUserSession("test", sessionId);
        } catch (NotFoundException nfe) {
            // Ignore
        }

        OAuthClient.AccessTokenResponse response = oauth.doRefreshTokenRequest(offlineTokenString, "password");
        AccessToken refreshedToken = oauth.verifyToken(response.getAccessToken());
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals(sessionId, refreshedToken.getSessionState());

        // Assert new refreshToken in the response
        String newRefreshToken = response.getRefreshToken();
        Assert.assertNotNull(newRefreshToken);
        Assert.assertNotEquals(oldToken.getId(), refreshedToken.getId());

        Assert.assertEquals(userId, refreshedToken.getSubject());

        Assert.assertTrue(refreshedToken.getRealmAccess().isUserInRole(Constants.OFFLINE_ACCESS_ROLE));


        EventRepresentation refreshEvent = events.expectRefresh(offlineToken.getId(), sessionId)
                .client("hardcoded-client")
                .user(userId)
                .removeDetail(Details.UPDATED_REFRESH_TOKEN_ID)
                .detail(Details.REFRESH_TOKEN_TYPE, TokenUtil.TOKEN_TYPE_OFFLINE)
                .assertEvent();
        Assert.assertNotEquals(oldToken.getId(), refreshEvent.getDetails().get(Details.TOKEN_ID));

        setTimeOffset(0);
        return newRefreshToken;
    }


}