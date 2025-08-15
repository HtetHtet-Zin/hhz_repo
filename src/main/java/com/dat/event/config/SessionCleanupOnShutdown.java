package com.dat.event.config;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class SessionCleanupOnShutdown implements HttpSessionListener {

    private static final Set<HttpSession> sessions =
            Collections.synchronizedSet(new HashSet<>());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sessions.add(se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessions.remove(se.getSession());
    }

    @PreDestroy
    public void invalidateAllSessions() {
        for (HttpSession session : sessions ) {
            try {
                session.invalidate();
            } catch (IllegalStateException ignored) {
                // Already invalidated
                log.info("Already invalidated");
            }
        }
        log.info("All active HTTP sessions invalidated on shutdown.");
    }
}
