package com.phones.phones.controller;

import com.phones.phones.dto.UserDto;
import com.phones.phones.exception.user.UserAlreadyExistException;
import com.phones.phones.exception.user.UserNotExistException;
import com.phones.phones.exception.user.UserSessionNotExistException;
import com.phones.phones.exception.user.UsernameAlreadyExistException;
import com.phones.phones.model.Call;
import com.phones.phones.model.Invoice;
import com.phones.phones.model.Line;
import com.phones.phones.model.User;
import com.phones.phones.service.UserService;
import com.phones.phones.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CallController callController;
    private final LineController lineController;
    private final SessionManager sessionManager;

    @Autowired
    public UserController(final UserService userService,
                          final CallController callController,
                          final LineController lineController,
                          final SessionManager sessionManager) {
        this.userService = userService;
        this.callController = callController;
        this.lineController = lineController;
        this.sessionManager = sessionManager;
    }


    @PostMapping("/")
    public ResponseEntity createUser(@RequestHeader("Authorization") final String sessionToken,
                                     @RequestBody @Valid final User user) throws UsernameAlreadyExistException, UserAlreadyExistException, UserSessionNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.add(user));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestHeader("Authorization") final String sessionToken) throws UserSessionNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            List<UserDto> users = userService.getAll();
            return (users.size() > 0) ? ResponseEntity.ok(users) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<UserDto>> getUserById(@RequestHeader("Authorization") final String sessionToken,
                                                         @PathVariable final Long id) throws UserNotExistException, UserSessionNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            return ResponseEntity.ok(userService.getById(id));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteUserById(@RequestHeader("Authorization") final String sessionToken,
                                                  @PathVariable final Long id) throws UserNotExistException, UserSessionNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            return ResponseEntity.ok(userService.disableById(id));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserById(@RequestHeader("Authorization") final String sessionToken,
                                               @RequestBody @Valid final User user,
                                               @PathVariable final Long id) throws UserNotExistException, UsernameAlreadyExistException, UserSessionNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            return ResponseEntity.ok(userService.updateById(id, user));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}/calls")
    public ResponseEntity<List<Call>> getCallsByUserId(@RequestHeader("Authorization") String sessionToken,
                                                       @PathVariable final Long id) throws UserSessionNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            List<Call> calls = callController.getCallsByUserId(id);
            return (calls.size() > 0) ? ResponseEntity.ok(calls) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/me/calls")
    public ResponseEntity<List<Call>> getCallsByUserSession(@RequestHeader("Authorization") final String sessionToken,
                                                            @RequestParam(name = "from") final String from,
                                                            @RequestParam(name = "to") final String to) throws ParseException, UserSessionNotExistException {
        if (from == null || to == null) {
            throw new ValidationException("Date from and date to must have a value");
        }
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        Date fromDate = new SimpleDateFormat("dd/MM/yyyy").parse(from);
        Date toDate = new SimpleDateFormat("dd/MM/yyyy").parse(to);
        List<Call> calls = callController.getCallsByUserIdBetweenDates(currentUser.getId(), fromDate, toDate);
        return (calls.size() > 0) ? ResponseEntity.ok(calls) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/lines")
    public ResponseEntity<List<Line>> getLinesByUserId(@RequestHeader("Authorization") final String sessionToken,
                                                       @PathVariable final Long id) throws UserSessionNotExistException {
        User currentUser = sessionManager.getCurrentUser(sessionToken);
        if (currentUser.hasRoleEmployee()) {
            List<Line> lines = lineController.getLinesByUserId(id);
            return ResponseEntity.ok(lines);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @GetMapping("/me/lines")
    public ResponseEntity<List<Line>> getLinesByUserSession() {
        return null;
    }

    // between dates
    @GetMapping("/me/invoices")
    public ResponseEntity<List<Invoice>> getInvoicesByUserSession() {
        return null;
    }

    /*
    @GetMapping("/me/destination/top")
    Usar una projection para este metodo (limit 10)
        {
            "ciudad": x,
            "cantidad de veces": x
        }
     */


    public Optional<User> login(final String username,
                                final String password) throws UserNotExistException, ValidationException {
        if ((username != null) && (password != null)) {
            return userService.login(username, password);
        } else {
            throw new ValidationException("Username and password must have a value");
        }
    }

}
