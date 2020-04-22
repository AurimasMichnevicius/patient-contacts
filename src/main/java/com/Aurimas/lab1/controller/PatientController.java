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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.Aurimas.lab1.model.Patient;
import com.Aurimas.lab1.model.Contact;
import com.Aurimas.lab1.model.PatientContact;
import com.Aurimas.lab1.repository.PatientRepository;
import com.Aurimas.lab1.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class PatientController {
    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/patients")
    public List <Patient> getAllPatients(){
        return patientRepository.findAll();
    }

    //get
    @GetMapping("/patients/{id}")
    public ResponseEntity <Patient> getPatientById(@PathVariable(value = "id") Long patientId) throws ResourceNotFoundException{
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + patientId));
        return ResponseEntity.ok().body(patient);
    }

    //create
    @PostMapping("/patients")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity <Patient> createPatient(@Valid @RequestBody Patient patient) {
        try{
            final Patient savePatient = patientRepository.save(patient);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savePatient.getId())
                    .toUri();
            return ResponseEntity.created(location).body(savePatient);
        }
        catch (DataAccessException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
        
    }

    //update
    @PutMapping("/patients/{id}")
    public ResponseEntity <Patient> updatePatient(@PathVariable(value = "id") Long patientId,
                                          @Valid @RequestBody Patient PatientDetails) throws ResourceNotFoundException{
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(()-> new ResourceNotFoundException("Patient not found for this id :: " + patientId));
        try{
            patient.setpersonalCode(PatientDetails.getPersonalCode());
			patient.setCondition(PatientDetails.getCondition());
            final Patient updatedPatient = patientRepository.save(patient);
            return ResponseEntity.status(HttpStatus.OK).body(updatedPatient);
        }
        catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

	@PatchMapping("/patients/{id}")
    public ResponseEntity <Patient> patchPatient(@PathVariable(value = "id") Long patientId,
                                          @Valid @RequestBody Patient PatientDetails) throws ResourceNotFoundException{
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(()-> new ResourceNotFoundException("Patient not found for this id :: " + patientId));
			if (PatientDetails.getPersonalCode() != 0)
			{
            patient.setpersonalCode(PatientDetails.getPersonalCode());
			}
			if (PatientDetails.getCondition() != null)
			{
			patient.setCondition(PatientDetails.getCondition());
			}
            final Patient updatedPatient = patientRepository.save(patient);
            return ResponseEntity.status(HttpStatus.OK).body(updatedPatient);
    }
    //delete
    @DeleteMapping("/patients/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map <String, Boolean> deletePatient(@PathVariable(value = "id") Long patientId) throws ResourceNotFoundException{
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(()-> new ResourceNotFoundException("Patient not found for this id :: " + patientId));

        patientRepository.delete(patient);
        Map <String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
		
	}
    @GetMapping("/kontaktai")
    public Contact[] getAllContacts() throws JsonParseException, JsonMappingException, IOException {
        final String uri = "http://contacts:5000/contacts";
        RestTemplate restTemplate = new RestTemplate();
        try{
            return restTemplate.getForObject(uri, Contact[].class);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found", e);
        }
    }
	
	// not checked yet 
	
	@GetMapping("/kontaktai/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException,
            JsonParseException, JsonMappingException, IOException {
        final String uri = "http://contacts:5000/contacts/" + id;
        RestTemplate restTemplate = new RestTemplate();
        try{
            return ResponseEntity.ok().body(restTemplate.getForObject(uri, Contact.class));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found", e);
        }
    }

    
    @PostMapping("/kontaktai")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> createContact(@Valid @RequestBody Contact contact) {
        try {
            final Contact saveContact = contact;
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(saveContact.getId()).toUri();
            final String uri = "http://contacts:5000/contacts/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Contact> entity = new HttpEntity<>(saveContact, headers);
            return ResponseEntity.created(location).body(restTemplate.postForObject(uri, entity, String.class));
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    
    @PutMapping("/kontaktai/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable(value = "id") Long id, @Valid @RequestBody Contact contactDetails)
            throws ResourceNotFoundException {
        final String uri = "http://contacts:5000/contacts/" + id;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Contact> entity = new HttpEntity<>(contactDetails, headers);
            restTemplate.put(uri, entity, Contact.class);
            return ResponseEntity.status(HttpStatus.OK).body(contactDetails);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

    @DeleteMapping("/kontaktai/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deleteOwner(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        try{
            final String uri = "http://contacts:5000/contacts/" + id;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.delete(uri, 10);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return response;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found", e);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/patientcontacts")
    public List<PatientContact> getAllPatientContact() throws JsonParseException, JsonMappingException, IOException {
        List<Patient> patients = patientRepository.findAll();
        final String uri = "http://contacts:5000/contacts";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Contact> contacts = objectMapper.readValue(response, new TypeReference<List<Contact>>(){});
        List<PatientContact> ownedCars = new ArrayList<PatientContact>();
        try{
            for(Patient patient : patients){
                for(Contact contact : contacts){
                    if(contact.getId() == patient.getPersonalCode()){
                        PatientContact cao = new PatientContact();
                        BeanUtils.copyProperties(patient, cao);
                        BeanUtils.copyProperties(contact, cao);
                        cao.setId((int)patient.getId());
                        ownedCars.add(cao);
                    }   
                }
            }
            return ownedCars;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found", e);
        }
    }
    
    // get
    @GetMapping("/patientcontacts/{id}")
    public ResponseEntity<PatientContact> getPatientContactById(@PathVariable(value = "id") Long patientId) throws ResourceNotFoundException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + patientId));
        final String uri = "http://contacts:5000/contacts/" + patient.getPersonalCode();
        RestTemplate restTemplate = new RestTemplate();
        try{
            Contact contact = restTemplate.getForObject(uri, Contact.class);
            PatientContact cao = new PatientContact();
            BeanUtils.copyProperties(patient, cao);
            BeanUtils.copyProperties(contact, cao);
            cao.setId((int)patient.getId());
            return ResponseEntity.ok().body(cao);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found", e);
        }
    }


    
    // create
    @PostMapping("/patientcontacts")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<PatientContact> createOwnedCar(@Valid @RequestBody PatientContact patientcontact)
            throws ResourceNotFoundException {
        try {
            Patient patient = new Patient();
            BeanUtils.copyProperties(patientcontact, patient);
            Patient savePatient = patientRepository.save(patient);
            patient = patientRepository.findById(savePatient.getId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: "));
            patient.setId((int)savePatient.getId()*10);
            savePatient = patientRepository.save(patient);

            Contact contact = new Contact();
            BeanUtils.copyProperties(patientcontact, contact);
            contact.setId(savePatient.getPersonalCode());
            patientcontact.setId(((int)savePatient.getId()));
            final Contact saveContact = contact;

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(savePatient.getId()).toUri();
            
            //post to contacts
            final String uri = "http://contacts:5000/contacts/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Contact> entity = new HttpEntity<>(saveContact, headers); 
            restTemplate.postForObject(uri, entity, String.class);

            return ResponseEntity.created(location).body(patientcontact);
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

    
    // update
    @PutMapping("/patientcontacts/{id}")
    public ResponseEntity<PatientContact> updateOwnedCar(@PathVariable(value = "id") Long id, @Valid @RequestBody PatientContact patientContactDetails)
            throws ResourceNotFoundException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + id));
        try {
            patient.setpersonalCode(patientContactDetails.getPersonalCode());
            patient.setCondition(patientContactDetails.getCondition());
            patientRepository.save(patient);
            patientContactDetails.setId((int)patient.getId());
            final String uri = "http://contacts:5000/contacts/" + patient.getPersonalCode();
            RestTemplate restTemplate = new RestTemplate();
            Contact contact = restTemplate.getForObject(uri, Contact.class);
            
            contact.setName(patientContactDetails.getName());
            contact.setSurname(patientContactDetails.getSurname());
            contact.setEmail(patientContactDetails.getEmail());
            contact.setNumber(patientContactDetails.getNumber());
            contact.setId(0);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Contact> entity = new HttpEntity<>(contact, headers);
            restTemplate.put(uri, entity, Contact.class);

            return ResponseEntity.status(HttpStatus.OK).body(patientContactDetails);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    
    // delete
    @DeleteMapping("/patientcontacts/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deletePatientContact(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + id));
        patientRepository.delete(patient);

        final String uri = "http://contacts:5000/contacts/" + patient.getPersonalCode();
        RestTemplate restTemplate = new RestTemplate();
        try{
            restTemplate.delete(uri);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return response;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found", e);
        }
    }
}

