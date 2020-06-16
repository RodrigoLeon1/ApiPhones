package com.phones.phones.controller;

import com.phones.phones.exception.call.CallDoesNotExistException;
import com.phones.phones.exception.user.UserSessionDoesNotExistException;
import com.phones.phones.model.Call;
import com.phones.phones.model.User;
import com.phones.phones.service.CallService;
import com.phones.phones.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calls")
public class CallController {

    private final CallService callService;
    private final SessionManager sessionManager;

    @Autowired
    public CallController(final CallService callService,
                          final SessionManager sessionManager) {
        this.callService = callService;
        this.sessionManager = sessionManager;
    }

    @GetMapping("/")
    public ResponseEntity<List<Call>> findAllCalls(@RequestHeader("Authorization") String sessionToken) throws UserSessionDoesNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            List<Call> calls = callService.findAll();
            return (calls.size() > 0) ? ResponseEntity.ok(calls) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Call> findCallById(@RequestHeader("Authorization") String sessionToken,
                                             @PathVariable final Long id) throws CallDoesNotExistException, UserSessionDoesNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            Call call = callService.findById(id);
            return ResponseEntity.ok(call);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
