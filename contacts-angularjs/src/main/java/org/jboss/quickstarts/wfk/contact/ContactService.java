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
package org.jboss.quickstarts.wfk.contact;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.List;
import java.util.logging.Logger;

/**
 * This Service assumes the Control responsibility in the ECB pattern.
 * <p>
 * The validation is done here so that it may be used by other Boundary Resources.  Other Business Logic would go here
 * as well. 
 * <p>
 * There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods. 
 * 
 * @author Joshua Wilson
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class ContactService {

    @Inject
    private Logger log;

    @Inject
    private ContactValidator validator;

    @Inject
    private ContactRepository crud;
    
    /**
     * Returns a List of all persisted {@link Contact} objects, sorted alphabetically by last name.
     * 
     * @return  List of Contact objects
     */
    List<Contact> findAllOrderedByName() {
        List<Contact> contacts = crud.findAllOrderedByName();
        return contacts;
    }

    /**
     * Returns a single Contact object, specified by a Long id.
     * 
     * @param id The id field of the Contact to be returned
     * @return The Contact with the specified id
     */
    Contact findById(Long id) {
        Contact contact = crud.findById(id);
        return contact;
    }

    /**
     * Returns a single Contact object, specified by a String email.
     * <p>
     * If there is more than one Contact with the specified email, only the first encountered will be returned.
     * 
     * @param email The email field of the Contact to be returned
     * @return The first Contact with the specified email
     */
    Contact findByEmail(String email) {
        Contact contact = crud.findByEmail(email);
        return contact;
    }

    /**
     * Returns a single Contact object, specified by a String firstName.
     * <p>
     * If there is more then one, only the first will be returned.
     * 
     * @param firstName The firstName field of the Contact to be returned
     * @return The first Contact with the specified firstName
     */
    Contact findByFirstName(String firstName) {
        Contact contact = crud.findByFirstName(firstName);
        return contact;
    }

    /**
     * Returns a single Contact object, specified by a String lastName.
     * <p>
     * If there is more then one, only the first will be returned.
     * 
     * @param lastName The lastName field of the Contact to be returned
     * @return The first Contact with the specified lastName
     */
    Contact findByLastName(String lastName) {
        Contact contact = crud.findByFirstName(lastName);
        return contact;
    }

    /**
     * Creates a Contact object and persists it in the application database.
     * <p>
     * Validates the data in the Contact object using a {@link ContactValidator} object.
     * 
     * @param contact The Contact object to be persisted using a {@link ContactRepository} object
     * @return The Contact object that has been successfully persisted to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Contact create(Contact contact) throws ConstraintViolationException, ValidationException, Exception {
        log.info("ContactService.create() - Creating " + contact.getFirstName() + " " + contact.getLastName());
        
        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateContact(contact);
        
        // Write the contact to the database.
        Contact createdContact = crud.create(contact);
        
        return createdContact;
    }

    /**
     * Updates an existing Contact object in the application database.
     * <p>
     * Validates the data in the Contact object using a ContactValidator object.
     * 
     * @param contact The Contact object to be passed as an update to the application database
     * @return The Contact object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Contact update(Contact contact) throws ConstraintViolationException, ValidationException, Exception {
        log.info("ContactService.update() - Updating " + contact.getFirstName() + " " + contact.getLastName());
        
        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateContact(contact);

        // Either update the contact or add it if it can't be found.
        Contact updatedContact = crud.update(contact);
        
        return updatedContact;
    }

    /**
     * Deletes an existing Contact in the application database.
     * 
     * @param contact The Contact object to be removed from the application database
     * @return The Contact object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Contact delete(Contact contact) throws Exception {
        log.info("ContactService.delete() - Deleting " + contact.getFirstName() + " " + contact.getLastName());
        
        Contact deletedContact = null;
        
        if (contact.getId() != null) {
            deletedContact = crud.delete(contact);
        } else {
            log.info("ContactService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedContact;
    }

}
