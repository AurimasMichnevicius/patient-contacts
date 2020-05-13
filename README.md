CRUD Web Servisas, Pacientu registras.

Naudojama Spring Boot sistema.

Projektas sukompiliuojamas ir paleidžiamas "docker-compose up" komanda.

Patikrinimui naudojame postman
http://localhost/api/v1/

CREATE:
'  {
        "personalCode": "20456410"
        "condition": "Lengva"
    }
	
	  {
        "personalCode": "14456410",
        "condition": "Sunki"
    }
	  {
        "personalCode": "87456410",
        "condition": "Lengva"
    }
http://localhost/api/v1/patients/

READ:
(all)
http://localhost/api/v1/patients/

(id)
http://localhost/api/v1/patients/1

UPDATE:
'  {
        "personalCode": "20456410",
        "condition": "Sunki"
    }
	http://localhost/api/v1/patients/1

DELETE:
http://localhost/api/v1/patients/1

get
http://localhost/api/v1/kontaktai

    {
        "id": 12345,
        "surname": "Vangogh",
        "name": "Jake",
        "number": "+37065841738",
        "email": "jakevan@mail.com"
    }
	POST 
	
	{
    {
    	"id": 123456789,
        "surname": "Justinas",
        "name": "Jankevicius",
        "number": "+37067777777",
        "email": "jeyjey@mail.com"
    }
	put
	http://localhost/api/v1/kontaktai/12345
	{
	    {
    	"id": 123456789,
        "surname": "Justinas",
        "name": "Jankevicius",
        "number": "+37067777777",
        "email": "jeyjey@mail.com"
    }
	delete 
	http://localhost/api/v1/kontaktai/12345
	}
	POST
	http://localhost/api/v1/patientcontacts
	    {
        "id": 123456789,
        "condition": "Pabiaga beveik isgijo",
        "surname": "Justyn",
        "name": "Omegalvicius",
        "number": "+37061234567",
        "email": "Hotukas332@mail.com"
    }
	PUT
	
	http://localhost/api/v1/patientcontacts
	
		    {
        "id": 123456789,
        "condition": "Pabiaga beveik numire",
        "surname": "Justyn",
        "name": "Omegalvicius",
        "number": "+37061234567",
        "email": "Hotukas332@mail.com"
    }
	delete 
	http://localhost/api/v1/patientcontacts/1
	