package com.codemetrictech.seed_go;

import java.util.HashMap;

class Session {
    private HashMap cookies = new HashMap<>();

    Session() { }

    HashMap getCookies() {
        return this.cookies;
    }

    void setCookies(HashMap cookies) {
        this.cookies = cookies;
    }


}