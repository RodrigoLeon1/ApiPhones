package com.phones.phones.controller.web;

import com.phones.phones.controller.UserController;
import com.phones.phones.dto.CityTopDto;
import com.phones.phones.exception.user.UserDoesNotExistException;
import com.phones.phones.exception.user.UserSessionDoesNotExistException;
import com.phones.phones.model.Call;
import com.phones.phones.model.Invoice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final UserController userController;

    public ClientController(final UserController userController) {
        this.userController = userController;
    }


    /* Consulta de llamadas del usuario logueado por rango de fechas */
    @GetMapping("/me/calls")
    public ResponseEntity<List<Call>> findCallsByUserSessionBetweenDates(@RequestHeader("Authorization") final String sessionToken,
                                                                         @RequestParam(name = "from") final String from,
                                                                         @RequestParam(name = "to") final String to) throws ParseException, UserDoesNotExistException, UserSessionDoesNotExistException {
        List<Call> calls = userController.findCallsByUserSessionBetweenDates(sessionToken, from, to);
        return (calls.size() > 0) ? ResponseEntity.ok(calls) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /* Consulta de facturas del usuario logueado por rango de fechas */
    @GetMapping("/me/invoices")
    public ResponseEntity<List<Invoice>> findInvoicesByUserSessionBetweenDates(@RequestHeader("Authorization") final String sessionToken,
                                                                               @RequestParam(name = "from") final String from,
                                                                               @RequestParam(name = "to") final String to) throws ParseException, UserDoesNotExistException, UserSessionDoesNotExistException {
        List<Invoice> invoices = userController.findInvoicesByUserSessionBetweenDates(sessionToken, from, to);
        return (invoices.size() > 0) ? ResponseEntity.ok(invoices) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /* Consulta de TOP 10 destinos más llamados por el usuario */
    @GetMapping("/me/cities/top")
    public ResponseEntity<List<CityTopDto>> findTopCitiesCallsByUserSession(@RequestHeader("Authorization") final String sessionToken) throws UserDoesNotExistException, UserSessionDoesNotExistException {
        List<CityTopDto> cities = userController.findTopCitiesCallsByUserSession(sessionToken);
        return (cities.size() > 0) ? ResponseEntity.ok(cities) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
