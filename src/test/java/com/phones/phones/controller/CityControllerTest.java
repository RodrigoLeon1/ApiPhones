package com.phones.phones.controller;

import com.phones.phones.utils.RestUtils;
import com.phones.phones.TestFixture;
import com.phones.phones.exception.city.CityAlreadyExistException;
import com.phones.phones.exception.city.CityDoesNotExistException;
import com.phones.phones.exception.user.UserSessionDoesNotExistException;
import com.phones.phones.model.City;
import com.phones.phones.model.User;
import com.phones.phones.service.CityService;
import com.phones.phones.session.SessionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(RestUtils.class)
@RunWith(PowerMockRunner.class)
public class CityControllerTest {

    CityController cityController;

    @Mock
    CityService cityService;
    @Mock
    SessionManager sessionManager;


    @Before
    public void setUp() {
        initMocks(this);
        PowerMockito.mockStatic(RestUtils.class);
        cityController = new CityController(cityService, sessionManager);
    }

    @Test
    public void createCityOk() throws UserSessionDoesNotExistException, CityAlreadyExistException {
        User loggedUser = TestFixture.testUser();
        City newCity = TestFixture.testCity();

        when(sessionManager.getCurrentUser("123")).thenReturn(loggedUser);
        when(cityService.create(newCity)).thenReturn(newCity);
        when(RestUtils.getLocation(newCity.getId())).thenReturn(URI.create("miUri.com"));

        ResponseEntity response = cityController.createCity("123", newCity);

        assertEquals(URI.create("miUri.com"), response.getHeaders().getLocation());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }



    @Test
    public void findAllCitiesOk() throws UserSessionDoesNotExistException {
        User loggedUser = TestFixture.testUser();
        List<City> listOfCities = TestFixture.testListOfCities();

        when(sessionManager.getCurrentUser("123")).thenReturn(loggedUser);
        when(cityService.findAll()).thenReturn(listOfCities);

        ResponseEntity<List<City>> returnedCities = cityController.findAllCities("123");

        assertEquals(listOfCities.size(), returnedCities.getBody().size());
        assertEquals(listOfCities.get(0).getName(), returnedCities.getBody().get(0).getName());
        assertEquals(listOfCities.get(0).getPrefix(), returnedCities.getBody().get(0).getPrefix());
    }


    @Test
    public void findAllCitiesNoCitiesFound() throws UserSessionDoesNotExistException {
        User loggedUser = TestFixture.testUser();
        List<City> emptyList = new ArrayList<>();
        ResponseEntity response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(sessionManager.getCurrentUser("123")).thenReturn(loggedUser);
        when(cityService.findAll()).thenReturn(emptyList);

        ResponseEntity<List<City>> returnedCities = cityController.findAllCities("123");

        assertEquals(response.getStatusCode(), returnedCities.getStatusCode());
    }

    @Test
    public void findCityByIdOk() throws UserSessionDoesNotExistException, CityDoesNotExistException {
        User loggedUser = TestFixture.testUser();
        City city = TestFixture.testCity();

        when(sessionManager.getCurrentUser("123")).thenReturn(loggedUser);
        when(cityService.findById(1L)).thenReturn(city);

        ResponseEntity<City> returnedCity = cityController.findCityById("123", 1L);

        assertEquals(city.getId(), returnedCity.getBody().getId());
        assertEquals(city.getPrefix(), returnedCity.getBody().getPrefix());
        assertEquals(city.getProvince(), returnedCity.getBody().getProvince());
        assertEquals(1L, returnedCity.getBody().getId());
    }


}
