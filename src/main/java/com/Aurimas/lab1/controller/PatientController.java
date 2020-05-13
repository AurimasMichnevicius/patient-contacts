package com.Aurimas.lab1.controller;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.Aurimas.lab1.model.Patient;
import com.Aurimas.lab1.model.Kontaktai;
import com.Aurimas.lab1.model.PatientContact;
import com.Aurimas.lab1.repository.PatientRepository;
import com.Aurimas.lab1.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class PatientController {
	public boolean isruninig = false;
    @Autowired
    private PatientRepository patientRepository;
	
//-----------------------------------------------------------------------------------------------------
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

    //create // this might be needed to change.
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
            patient.setPersonalCode(PatientDetails.getPersonalCode());
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
            patient.setPersonalCode(PatientDetails.getPersonalCode());
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
	/*
    @GetMapping("/kontaktai")
    public Kontaktai[] getAllContacts() throws JsonParseException, JsonMappingException, IOException {
        final String uri = "http://contacts:5000/contacts";
        RestTemplate restTemplate = new RestTemplate();
        try{
            return restTemplate.getForObject(uri, Kontaktai[].class);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }
	
	// not checked yet  done.
	
	@GetMapping("/kontaktai/{id}")
    public ResponseEntity<Kontaktai> getContactById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException,
            JsonParseException, JsonMappingException, IOException {
        final String uri = "http://contacts:5000/contacts/" + id;
        RestTemplate restTemplate = new RestTemplate();
        try{
            return ResponseEntity.ok().body(restTemplate.getForObject(uri, Kontaktai.class));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }

    
    @PostMapping("/kontaktai")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> createKontaktai(@Valid @RequestBody Kontaktai kontaktai) {
        try {
            final Kontaktai saveContact = kontaktai;
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(saveContact.getId()).toUri();
            final String uri = "http://contacts:5000/contacts/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Kontaktai> entity = new HttpEntity<>(saveContact, headers);
            return ResponseEntity.created(location).body(restTemplate.postForObject(uri, entity, String.class));
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    
    @PutMapping("/kontaktai/{id}")
    public ResponseEntity<Kontaktai> updateContact(@PathVariable(value = "id") Long id, @Valid @RequestBody Kontaktai contactDetails)
            throws ResourceNotFoundException {
        final String uri = "http://contacts:5000/contacts/" + id;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Kontaktai> entity = new HttpEntity<>(contactDetails, headers);
            restTemplate.put(uri, entity, Kontaktai.class);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }
*/
    //----------------------------------------------------------------------------------------------------------------------------------
/*
    @GetMapping("/patientcontacts")
    public List<PatientContact> getAllPatientContact() throws JsonParseException, JsonMappingException, IOException {
        List<Patient> patients = patientRepository.findAll();
        final String uri = "http://contacts:5000/contacts";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Kontaktai> contacts = objectMapper.readValue(response, new TypeReference<List<Kontaktai>>(){});
        List<PatientContact> patientcontacts = new ArrayList<PatientContact>();
        try{
            for(Patient patient : patients){
                for(Kontaktai kontaktai : contacts){
                    if(kontaktai.getId() == patient.getPersonalCode()){
                        PatientContact cao = new PatientContact();
                        BeanUtils.copyProperties(patient, cao);
                        BeanUtils.copyProperties(kontaktai, cao);
                        cao.setId(patient.getPersonalCode());
                        patientcontacts.add(cao);
                    }   
                }
            }
            return patientcontacts;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }*/
    /*
    // get
    @GetMapping("/patientcontacts/{id}")
    public ResponseEntity<PatientContact> getPatientContactById(@PathVariable(value = "id") Long patientId) throws ResourceNotFoundException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + patientId));
        final String uri = "http://contacts:5000/contacts/" + patient.getPersonalCode();
        RestTemplate restTemplate = new RestTemplate();
        try{
            Kontaktai kontaktai = restTemplate.getForObject(uri, Kontaktai.class);
            PatientContact cao = new PatientContact();
            BeanUtils.copyProperties(patient, cao);
            BeanUtils.copyProperties(kontaktai, cao);
            cao.setId((int)patient.getId());
            return ResponseEntity.ok().body(cao);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }


    
    // create
    @PostMapping("/patientcontacts")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<PatientContact> createPatientContact(@Valid @RequestBody PatientContact patientcontact)
            throws ResourceNotFoundException {
        try {
            Patient patient = new Patient(); // so what happends is i get updated on my patients but when i update client stuff my shit crashes 
            BeanUtils.copyProperties(patientcontact, patient);
			patient.setPersonalCode(patientcontact.getId());
            Patient savePatient = patientRepository.save(patient);
            patient = patientRepository.findById(savePatient.getId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: "));
            patient.setId((int)savePatient.getId()*10);
            savePatient = patientRepository.save(patient);
// could be here happenin 1.2
            Kontaktai kontaktai = new Kontaktai();
            BeanUtils.copyProperties(patientcontact, kontaktai);
            kontaktai.setId(savePatient.getPersonalCode());
            patientcontact.setId(((int)savePatient.getId()));
            final Kontaktai saveContact = kontaktai;

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(savePatient.getId()).toUri();
            
            //post to contacts /// problem is right here with failure of data structure 1.2 
            final String uri = "http://contacts:5000/contacts/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Kontaktai> entity = new HttpEntity<>(saveContact, headers); 
            restTemplate.postForObject(uri, entity, String.class);
			
            return ResponseEntity.created(location).body(patientcontact);
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
		// above failure 1.2 : 
    }

    
    // update
    @PutMapping("/patientcontacts/{id}")
    public ResponseEntity<PatientContact> updatePatientContact(@PathVariable(value = "id") Long id, @Valid @RequestBody PatientContact patientContactDetails)
            throws ResourceNotFoundException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + id));
        try {
            patient.setPersonalCode((int)patientContactDetails.getId()*10);
            patient.setCondition(patientContactDetails.getCondition());
            patientRepository.save(patient);
            patientContactDetails.setId((int)patient.getId());
            final String uri = "http://contacts:5000/contacts/" + patient.getPersonalCode();
            RestTemplate restTemplate = new RestTemplate();
            Kontaktai kontaktai = restTemplate.getForObject(uri, Kontaktai.class);
            
            kontaktai.setName(patientContactDetails.getName());
            kontaktai.setSurname(patientContactDetails.getSurname());
            kontaktai.setEmail(patientContactDetails.getEmail());
            kontaktai.setNumber(patientContactDetails.getNumber());
            kontaktai.setId(0);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Kontaktai> entity = new HttpEntity<>(kontaktai, headers);
            restTemplate.put(uri, entity, Kontaktai.class);

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }
}*/

    @GetMapping("/patientcontacts")
    public Object getEveryPatientContact() throws JsonParseException, JsonMappingException, IOException {
        List<Patient> patients = patientRepository.findAll();
        List<PatientContact> patientcontacts = new ArrayList<PatientContact>();
        isruninig = CheckConnection("contacts", 5000, 500);
        try{
                if(isruninig){
                    List<Kontaktai> kontaktai = GetKontaktai();
                    for(Patient patient : patients){
                        PatientContact cao = new PatientContact();
                            for(Kontaktai kontaktas : kontaktai){
                                if(kontaktas.getId() == patient.getPersonalCode()){
                                    BeanUtils.copyProperties(kontaktas, cao);  
                                    BeanUtils.copyProperties(patient, cao);
                                    cao.setId(patient.getPersonalCode());
                                    patientcontacts.add(cao);
                                }   
                            }
                    }
                    return patientcontacts;
                }else{
                    return patients;
                }
        }catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", ex);
        }
    }
    
    // get here problme and goes on . // fixed problme was accuiring information problem .
    @GetMapping("/patientcontacts/{id}")
    public ResponseEntity<Object> getPatientContactById(@PathVariable(value = "id") Long patientId) throws ResourceNotFoundException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + patientId));
        isruninig = CheckConnection("contacts", 5000, 500);
        try{
            PatientContact cao = new PatientContact();
            if(isruninig){
                Kontaktai kontaktai = getKontaktaiObjectById(patient.getPersonalCode());
                BeanUtils.copyProperties(kontaktai, cao);
                BeanUtils.copyProperties(patient, cao);
				patient.setPersonalCode(cao.getId());
                cao.setId(patient.getPersonalCode());
                return ResponseEntity.ok().body(cao);
            }else{
                return ResponseEntity.ok().body(patient);
            }
            
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }


    
    // create Fullyy fixed m, problem was my patientid wanst updaingingdfing 
    @PostMapping("/patientcontacts")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Object> CreatePatientContact(@Valid @RequestBody PatientContact patientContact)
            throws ResourceNotFoundException {
        isruninig = CheckConnection("contacts", 5000, 500);
        try {
            Patient patient = new Patient();
            BeanUtils.copyProperties(patientContact, patient);
			patient.setPersonalCode(patientContact.getId());
            Patient savePatient = patientRepository.save(patient);
            patient = patientRepository.findById(savePatient.getId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: "));
            patient.setPersonalCode(patientContact.getId());
            savePatient = patientRepository.save(patient);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(savePatient.getPersonalCode()).toUri();

            if(isruninig){
                Kontaktai kontaktai = new Kontaktai();
                BeanUtils.copyProperties(patientContact, kontaktai);
                kontaktai.setId(savePatient.getPersonalCode());
                patientContact.setId((savePatient.getPersonalCode()));
                final Kontaktai saveKontaktai = kontaktai;
                postObject(saveKontaktai);
                return ResponseEntity.created(location).body(patientContact);
            }else{
                return ResponseEntity.created(location).body(patient);
            }
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    // update // tihs is fucked soo basicly somehow this piece of **** says my structure is bad. what the hell.
    @PutMapping("/patientcontacts/{id}")
    public ResponseEntity<Object> updatePatientContact(@PathVariable(value = "id") Long id, @Valid @RequestBody PatientContact patientContactdetails)
            throws ResourceNotFoundException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + id));
        isruninig = CheckConnection("contacts", 5000, 500);
        try {
			int currentID =patient.getPersonalCode();
            patient.setPersonalCode((patientContactdetails.getId()));
            patient.setCondition(patientContactdetails.getCondition()); // mightn eed some fixing
            final Patient finalPatient = patientRepository.save(patient);
          //  patientContactdetails.setId(patient.getPersonalCode());

            if(isruninig){
            Kontaktai kontaktai = getKontaktaiObjectById(patient.getPersonalCode());
            
            kontaktai.setName(patientContactdetails.getName());
            kontaktai.setSurname(patientContactdetails.getSurname());
            kontaktai.setEmail(patientContactdetails.getEmail());
            kontaktai.setNumber(patientContactdetails.getNumber());
            kontaktai.setId(0);

            putObjrct(kontaktai, currentID);// this method is fucked not like method but i send WRONG DATA STRUCTURE 

            return ResponseEntity.status(HttpStatus.OK).body(patientContactdetails);
            }else{
                return ResponseEntity.status(HttpStatus.OK).body(finalPatient);
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    
    // delete works perfectly 
    @DeleteMapping("/patientcontacts/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deletePatientContact(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for this id :: " + id));
        patientRepository.delete(patient);
        isruninig = CheckConnection("contacts", 5000, 500);
        try{
            if(isruninig)
                deletePatientId(patient.getPersonalCode());
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return response;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontaktai not found", e);
        }
    }
    final String uri = "http://contacts:5000/contacts/";
    RestTemplate restTemplate = new RestTemplate();

    public List<Kontaktai> GetKontaktai() throws JsonParseException, JsonMappingException, IOException {
        List<Kontaktai> kontaktai;
        String response = restTemplate.getForObject(uri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        kontaktai = objectMapper.readValue(response, new TypeReference<List<Kontaktai>>(){});
        return kontaktai;
    }

    public Kontaktai getKontaktaiObjectById(int id){
        final String uriWithId = uri + id;
        RestTemplate restTemplate = new RestTemplate();
        Kontaktai kontaktai = restTemplate.getForObject(uriWithId, Kontaktai.class);
        return kontaktai;
    }
// bloga uzklausa siunciama reikia koerguoti kontaktai sukurima ar kazka panasaus tikrinu dabar. // sutaisyta, problema buvo su contact web servicu, nes jis neprima tam tikru issimciu.
    public void postObject(Kontaktai kontaktai){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Kontaktai> entity = new HttpEntity<>(kontaktai, headers); 
        restTemplate.postForObject(uri, entity, String.class);
    }
// bloga uzklausa siunciama reikia koerguoti kontaktai sukurima ar kazka panasaus tikrinu dabar.
// gali but kad contact web servicsas nepriima tam tikru issimciu, gaunamas error wrong data structure. 
    public void putObjrct(Kontaktai kontaktai, int id){
        final String uriWithId = uri + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Kontaktai> entity = new HttpEntity<>(kontaktai, headers);
        restTemplate.put(uriWithId, entity, Kontaktai.class);
    }

    public void deletePatientId(int id){
        final String uriWithId = uri + id;
        restTemplate.delete(uriWithId);
    }
	

    public static boolean CheckConnection(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
}


