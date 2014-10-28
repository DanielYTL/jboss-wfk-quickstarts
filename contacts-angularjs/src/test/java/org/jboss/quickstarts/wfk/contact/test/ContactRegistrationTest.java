/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.contact.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.ContactRepository;
import org.jboss.quickstarts.wfk.contact.ContactRESTService;
import org.jboss.quickstarts.wfk.contact.ContactService;
import org.jboss.quickstarts.wfk.contact.ContactValidator;
import org.jboss.quickstarts.wfk.util.Resources;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

// JAX-RS 2.0 import statement
//import javax.ws.rs.client.*;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Contact creation functionality
 * (see {@link ContactRESTService#createContact(Contact) createContact(Contact)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see ContactRESTService
 */
@RunWith(Arquillian.class)
public class ContactRegistrationTest {

    /*
     * Many of the comments in the code below contain code for use with JAX-RS 2.0 for demonstration purposes.
     * If you are not using JAX-RS 2.0 then these comments may be ignored.
     */

    @Deployment
    public static Archive<?> createTestArchive() {
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();



        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Contact.class, 
                        ContactRESTService.class, 
                        ContactRepository.class, 
                        ContactValidator.class, 
                        ContactService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    ContactRESTService contactRESTService;
    
    @Inject
    Logger log;
    
    // The URI is needed for the JAX-RS 2.0 tests.
//    private static URI uri = UriBuilder.fromUri("http://localhost/jboss-contacts-angularjs/rest/contact").port(8080).build();
    
    // JAX-RS 2.0 Client API
//    private static Client client = ClientBuilder.newClient();
    
    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    private Date date = new Date(498484800000L);

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Contact contact = createContactInstance("Jack", "Doe", "jack@mailinator.com", "(212) 555-1234", date);
        Response response = contactRESTService.createContact(contact);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New contact was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Contact contact = createContactInstance("", "", "", "", date);
        Response response = contactRESTService.createContact(contact);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 4,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Contact contact = createContactInstance("Jane", "Doe", "jane@mailinator.com", "(212) 555-1234", date);
        contactRESTService.createContact(contact);

        // Register a different user with the same email
        Contact anotherContact = createContactInstance("John", "Doe", "jane@mailinator.com", "(213) 355-1234", date);
        Response response = contactRESTService.createContact(anotherContact);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate contact register attempt failed with return code " + response.getStatus());
    }

    // Uncomment when you have access to JAX-RS 2.0
//    @Test
//    @InSequence(4)
//    public void shouldNotCreateANullContact() throws JAXBException {
//        //POSTs a null Contact
//        Response response = client.target(uri).request().post(Entity.entity(null, MediaType.APPLICATION_JSON));
//        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
//    }
//    
//    @Test
//    @InSequence(5)
//    public void shouldNotFindTheContactID() throws JAXBException {
//        // GETs a Contact with an unknown ID
//        Response response = client.target(uri).path("unknownID").request().get();
//        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
//    }
//    
//    @Test
//    @InSequence(6)
//    public void shouldCreateAndDeleteAContact() throws JAXBException {
//        
//        Contact contact = createContactInstance("Jason", "Smith", "jason@mailinator.com", "2125551234", date);
//        
//        // POSTs a Contact
//        Response response = client.target(uri).request().post(Entity.entity(contact, MediaType.APPLICATION_JSON));
//        
//        assertEquals(Response.Status.CREATED, response.getStatusInfo());
//        URI contactURI = response.getLocation();
//        
//        // With the location, GETs the Contact
//        response = client.target(contactURI).request().get();
//        contact = response.readEntity(Contact.class);
//        assertEquals(Response.Status.OK, response.getStatusInfo());
//        assertEquals("Jason", contact.getFirstName());
//        
//        // GETs the Contact ID and DELETEs it
//        String contactID = contactURI.toString().split("/")[6];
//        response = client.target(uri).path(contactID).request().delete();
//        assertEquals(Response.Status.NO_CONTENT, response.getStatusInfo());
//        
//        // GETs the Contact and checks if it has been deleted
//        response = client.target(bookURI).request().get();
//        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
//    }


    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.contact.Contact Contact} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param firstName The first name of the Contact being created
     * @param lastName  The last name of the Contact being created
     * @param email     The email address of the Contact being created
     * @param phone     The phone number of the Contact being created
     * @param birthDate The birth date of the Contact being created
     * @return The Contact object create
     */
    private Contact createContactInstance(String firstName, String lastName, String email, String phone, Date birthDate) {
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneNumber(phone);
        contact.setBirthDate(birthDate);
        return contact;
    }
}
