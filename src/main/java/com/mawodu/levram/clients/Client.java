package com.mawodu.levram.clients;

import java.util.concurrent.Future;

public interface Client {

    public Future<String> makeRequest(String param);
    public String name();
}
