package com.Aurimas.lab1.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.Aurimas.lab1.model.Patient;
import com.Aurimas.lab1.model.PatientContact;
import com.Aurimas.lab1.model.Kontaktai;
import com.Aurimas.lab1.repository.PatientRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Aurimas.lab1.exception.ResourceNotFoundException;


@RestController
@RequestMapping("/api/v1")
public class KontaktaiController {
    final String uri = "http://contacts:5000/contacts/";
    RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/kontaktai")
    public Kontaktai[] getAllKontaktai() throws JsonParseException, JsonMappingException, IOException {
        try{
            return restTemplate.getForObject(uri, Kontaktai[].class);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }



    @GetMapping("/kontaktai/{id}")
    public ResponseEntity<Kontaktai> getKontaktaiById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException,
            JsonParseException, JsonMappingException, IOException {
        final String uriWithId = uri + id;
        RestTemplate restTemplate = new RestTemplate();
        try{
            return ResponseEntity.ok().body(restTemplate.getForObject(uriWithId, Kontaktai.class));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }

    
    @PostMapping("/kontaktai")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> createKontaktai(@Valid @RequestBody Kontaktai kontaktai) {
        try {
            final Kontaktai saveKontakta = kontaktai;
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(saveKontakta.getId()).toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Kontaktai> entity = new HttpEntity<>(saveKontakta, headers);
            return ResponseEntity.created(location).body(restTemplate.postForObject(uri, entity, String.class));
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    
    @PutMapping("/kontaktai/{id}")
    public ResponseEntity<Kontaktai> updateKontaktai(@PathVariable(value = "id") Long id, @Valid @RequestBody Kontaktai kontaktaiDetails)
            throws ResourceNotFoundException {
        final String uriWithId = uri + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Kontaktai> entity = new HttpEntity<>(kontaktaiDetails, headers);
            restTemplate.put(uriWithId, entity, Kontaktai.class);
            return ResponseEntity.status(HttpStatus.OK).body(kontaktaiDetails);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

    @DeleteMapping("/kontaktai/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deleteKontaktai(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        try{
            final String uriWithId = uri + id;
            restTemplate.delete(uriWithId, 10);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return response;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktas not found", e);
        }
    }

    
}