package com.noandroid.familycontacts.model;

import com.noandroid.familycontacts.model.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "TELEPHONE".
 */
public class Telephone {

    private Long id;
    private String number;
    private Long telCityId;
    private Long contactId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TelephoneDao myDao;

    private City city;
    private Long city__resolvedKey;

    private Contact contact;
    private Long contact__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Telephone() {
    }

    public Telephone(Long id) {
        this.id = id;
    }

    public Telephone(Long id, String number, Long telCityId, Long contactId) {
        this.id = id;
        this.number = number;
        this.telCityId = telCityId;
        this.contactId = contactId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTelephoneDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getTelCityId() {
        return telCityId;
    }

    public void setTelCityId(Long telCityId) {
        this.telCityId = telCityId;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    /** To-one relationship, resolved on first access. */
    public City getCity() {
        Long __key = this.telCityId;
        if (city__resolvedKey == null || !city__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CityDao targetDao = daoSession.getCityDao();
            City cityNew = targetDao.load(__key);
            synchronized (this) {
                city = cityNew;
            	city__resolvedKey = __key;
            }
        }
        return city;
    }

    public void setCity(City city) {
        synchronized (this) {
            this.city = city;
            telCityId = city == null ? null : city.getId();
            city__resolvedKey = telCityId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Contact getContact() {
        Long __key = this.contactId;
        if (contact__resolvedKey == null || !contact__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContactDao targetDao = daoSession.getContactDao();
            Contact contactNew = targetDao.load(__key);
            synchronized (this) {
                contact = contactNew;
            	contact__resolvedKey = __key;
            }
        }
        return contact;
    }

    public void setContact(Contact contact) {
        synchronized (this) {
            this.contact = contact;
            contactId = contact == null ? null : contact.getId();
            contact__resolvedKey = contactId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    static public Long getCityIdForTel(String number) {
        DaoMaster daoMaster = new DaoMaster(DatabaseHelper.getDB(null).getReadableDatabase());
        TelInitialDao telinitDao = daoMaster.newSession().getTelInitialDao();
        String nospace = number.replaceAll(" ", "");
        String initial;
        if (nospace.startsWith("+")) {
            if (nospace.length() == 14 && nospace.startsWith("+86"))
                initial = nospace.substring(3, 10);
            else
                throw new IllegalArgumentException("Foreign telephone number");
        } else {
            initial = nospace.substring(0, 7);
        }
        return telinitDao.queryBuilder().where(TelInitialDao.Properties.Initial.eq(initial))
                .build().uniqueOrThrow().getTelinitCityId();
    }

    static public String getLocationForTel(String number) {
        DaoMaster daoMaster = new DaoMaster(DatabaseHelper.getDB(null).getReadableDatabase());
        CityDao cityDao = daoMaster.newSession().getCityDao();
        try {
            return cityDao.load(getCityIdForTel(number)).toString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public String getCityStr() {
        try {
            City city = this.getCity();
            return city.toString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public String getWeatherInfo() {
        try {
            return getCity().getWeatherInfo();
        } catch (Exception e) {
            return "";
        }
    }

    public Telephone(Long id, String number, Long contactId) {
        this.id = id;
        this.number = number;
        this.contactId = contactId;
        try {
            this.telCityId = getCityIdForTel(this.number);
        } catch (Exception e) {
            this.telCityId = null;
        }
    }
    // KEEP METHODS END

}