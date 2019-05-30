package com.codemetrictech.seed_go;

import java.util.HashMap;

public class Session {
    private HashMap cookies = new HashMap<>();

    public Session() {

    }

    public HashMap getCookies() {
        return this.cookies;
    }

    public void setCookies(HashMap cookies) {
        this.cookies = cookies;
    }


}