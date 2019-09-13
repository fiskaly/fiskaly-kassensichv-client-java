package com.fiskaly.kassensichv.client.interceptors;

import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionInterceptorTest {

    @Test
    public void rewriteRoute() {
        TransactionInterceptor interceptor =
                new TransactionInterceptor(null);

        String targetUrl = interceptor.rewriteRoute(
                "https://kassensichv.io/api/v0/tss/" +
                        "0e709b9a-9e09-4d48-9680-c1810a1e45c3/tx/" +
                        "e09bf758-d9a2-449e-bb34-af6815d76389?last_revision=1");

        assertEquals(
                "https://kassensichv.io/api/v0/tss/0e709b9a-9e09-4d48-9680-c1810a1e45c3" +
                        "/tx/e09bf758-d9a2-449e-bb34-af6815d76389/log?last_revision=1",
                targetUrl
        );

        targetUrl = interceptor.rewriteRoute(
                "https://kassensichv.io/api/v0/tss/" +
                        "0e709b9a-9e09-4d48-9680-c1810a1e45c3/tx/" +
                        "e09bf758-d9a2-449e-bb34-af6815d76389?last_revision=1?something_else=2");

        assertEquals(
                "https://kassensichv.io/api/v0/tss/0e709b9a-9e09-4d48-9680-c1810a1e45c3" +
                        "/tx/e09bf758-d9a2-449e-bb34-af6815d76389/log?last_revision=1?something_else=2",
                targetUrl
        );

        targetUrl = interceptor.rewriteRoute(
                "https://kassensichv.io/api/v0/tss/" +
                        "0e709b9a-9e09-4d48-9680-c1810a1e45c3/tx/" +
                        "e09bf758-d9a2-449e-bb34-af6815d76389");

        assertEquals(
                "https://kassensichv.io/api/v0/tss/0e709b9a-9e09-4d48-9680-c1810a1e45c3" +
                        "/tx/e09bf758-d9a2-449e-bb34-af6815d76389/log",
                targetUrl
        );
    }
}

