package ch.sportchef.server;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SportChefConfigurationShould {

    @Test
    public void beInstancable() {
        assertThat(new SportChefConfiguration()).isNotNull();
    }
}
