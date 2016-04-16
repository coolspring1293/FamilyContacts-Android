package com.noandroid.familycontacts.model;

import de.greenrobot.daogenerator.*;

/**
 * Created by leasunhy on 4/12/16.
 */
public class ModelGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1001, "com.noandroid.familycontacts.model");

        // Record
        Entity record = schema.addEntity("Record");
        record.addIdProperty();
        record.addDateProperty("time").notNull();
        record.addIntProperty("status").notNull();
        record.addStringProperty("telephoneNumber").notNull();

        // Contact
        Entity contact = schema.addEntity("Contact");
        contact.addIdProperty().getProperty();
        contact.addStringProperty("name").notNull();
        contact.addStringProperty("relationship");
        contact.addStringProperty("avatar");
        contact.addStringProperty("pinyin");
        contact.setHasKeepSections(true);

        // City
        Entity city = schema.addEntity("City");
        city.addIdProperty().getProperty();
        city.addStringProperty("province").notNull();
        city.addStringProperty("cityname").notNull();
        city.addStringProperty("weatherCode");
        city.addStringProperty("weatherInfo");
        city.addStringProperty("temperature");

        // Telephone
        Entity telephone = schema.addEntity("Telephone");
        Property telephoneOriId = telephone.addIdProperty().getProperty();
        telephone.addStringProperty("number").index();

        // Relationship: Telephone-City
        telephone.addToOne(city, telephone.addLongProperty("telCityId").getProperty());

        // Relationship: Telephone-Contact
        {
            Property contactId = telephone.addLongProperty("contactId").getProperty();
            telephone.addToOne(contact, contactId);
            ToMany contactToTelephones = contact.addToMany(telephone, contactId);
            contactToTelephones.setName("telephones");
            contactToTelephones.orderAsc(telephoneOriId);
        }

        // TelephoneInitial
        Entity telInitial = schema.addEntity("TelInitial");
        telInitial.addIdProperty();
        telInitial.addStringProperty("initial").index();

        // Relationship: TelephoneInitial-City
        telInitial.addToOne(city, telInitial.addLongProperty("telinitCityId").getProperty());

        // Blacklist
        Entity blacklist = schema.addEntity("Blacklist");
        blacklist.addIdProperty();
        blacklist.addStringProperty("PhoneNumber").index();

        // Pinyin
        Entity pinyin = schema.addEntity("Pinyin");
        pinyin.addIdProperty();
        pinyin.addStringProperty("zi").index();
        pinyin.addStringProperty("pinyin");
        pinyin.setHasKeepSections(true);

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
