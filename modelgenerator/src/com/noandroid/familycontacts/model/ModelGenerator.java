package com.noandroid.familycontacts.model;

import de.greenrobot.daogenerator.*;

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
        contact.addIdProperty().getProperty();
        contact.addStringProperty("name").notNull();
        contact.addStringProperty("relationship");
        contact.addStringProperty("avatar");

        Entity city = schema.addEntity("City");
        city.addIdProperty().getProperty();
        city.addStringProperty("province").notNull();
        city.addStringProperty("cityname").notNull();
        city.addStringProperty("weatherCode").notNull();

        Entity telephone = schema.addEntity("Telephone");
        Property telephoneOriId = telephone.addIdProperty().getProperty();
        telephone.addStringProperty("number").index();
        Property contactId = telephone.addLongProperty("contactId").getProperty();
        Property cityId = telephone.addLongProperty("cityId").getProperty();

        telephone.addToOne(contact, contactId);
        telephone.addToOne(city, cityId);

        ToMany contactToTelephones = contact.addToMany(telephone, contactId);
        contactToTelephones.setName("telephones");
        contactToTelephones.orderAsc(telephoneOriId);

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
