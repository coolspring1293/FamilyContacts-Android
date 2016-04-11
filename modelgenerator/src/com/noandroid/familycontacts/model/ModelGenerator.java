package com.noandroid.familycontacts.model;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by leasunhy on 4/12/16.
 */
public class ModelGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "com.noandroid.familycontacts.model");

        Entity record = schema.addEntity("Record");
        record.addIdProperty();
        record.addDateProperty("time").notNull();
        record.addIntProperty("status").notNull();
        record.addStringProperty("telephoneNumber").notNull();

        Entity contact = schema.addEntity("Contact");
        Property contactId = contact.addIdProperty().getProperty();
        contact.addStringProperty("name").notNull();
        contact.addStringProperty("relationship");
        contact.addStringProperty("avatar");

        Entity city = schema.addEntity("City");
        Property cityId = city.addIdProperty().getProperty();
        city.addStringProperty("province").notNull();
        city.addStringProperty("cityname").notNull();
        city.addStringProperty("weatherCode").notNull();

        Entity telephone = schema.addEntity("Telephone");
        telephone.addStringProperty("number").primaryKey();
        telephone.addToOne(contact, contactId);
        telephone.addToOne(city, cityId);

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
