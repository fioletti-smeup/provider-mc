package com.smeup.provider.conf;

import java.io.Serializable;

import com.smeup.provider.model.FixedCredentials;

public interface Config extends Serializable {

    String getUser();

    String getPassword();

    String getServer();

    TokenConfig getTokenConfig();

    FixedCredentials getFixedCredentials();
}
