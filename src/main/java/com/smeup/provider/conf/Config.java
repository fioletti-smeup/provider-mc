package com.smeup.provider.conf;

import java.io.Serializable;

import com.smeup.provider.model.FixedCredentials;

public interface Config extends Serializable {

    String USER = "SMEUP_USER";
    String PASSWORD = "SMEUP_PASSWORD";
    String SERVER = "SMEUP_SERVER";
    String SECRET = "SMEUP_SECRET";

    String getUser();

    String getPassword();

    String getServer();

    String getSecret();

    FixedCredentials getFixedCredentials();
}
