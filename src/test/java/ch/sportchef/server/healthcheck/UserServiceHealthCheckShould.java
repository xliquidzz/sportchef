package ch.sportchef.server.healthcheck;

import ch.sportchef.server.App;
import ch.sportchef.server.SportChefConfiguration;
import ch.sportchef.server.representations.User;
import ch.sportchef.server.services.UserService;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceHealthCheckShould {

    @ClassRule
    public static final DropwizardAppRule<SportChefConfiguration> RULE = new DropwizardAppRule<>(App.class, "config.yaml");

    @Test
    public void returnHealthy() throws IOException {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/healthcheck", RULE.getAdminPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        final String body = response.readEntity(String.class);
        assertThat(body).contains("UserService is fine.");
    }

    @Test
    public void returnUnhealthy() throws IOException {
        final UserService userService = App.getService(UserService.class);
        final User user = userService.readUserById(1L).get();
        userService.removeUser(user);

        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/healthcheck", RULE.getAdminPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        final String body = response.readEntity(String.class);
        assertThat(body).contains("UserService has problems returning the correct reference user!");
    }
}
