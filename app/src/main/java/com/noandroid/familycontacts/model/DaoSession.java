package com.noandroid.familycontacts.model;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.noandroid.familycontacts.model.Record;
import com.noandroid.familycontacts.model.Contact;
import com.noandroid.familycontacts.model.City;
import com.noandroid.familycontacts.model.Telephone;
import com.noandroid.familycontacts.model.TelInitial;
import com.noandroid.familycontacts.model.Blacklist;

import com.noandroid.familycontacts.model.RecordDao;
import com.noandroid.familycontacts.model.ContactDao;
import com.noandroid.familycontacts.model.CityDao;
import com.noandroid.familycontacts.model.TelephoneDao;
import com.noandroid.familycontacts.model.TelInitialDao;
import com.noandroid.familycontacts.model.BlacklistDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig recordDaoConfig;
    private final DaoConfig contactDaoConfig;
    private final DaoConfig cityDaoConfig;
    private final DaoConfig telephoneDaoConfig;
    private final DaoConfig telInitialDaoConfig;
    private final DaoConfig blacklistDaoConfig;

    private final RecordDao recordDao;
    private final ContactDao contactDao;
    private final CityDao cityDao;
    private final TelephoneDao telephoneDao;
    private final TelInitialDao telInitialDao;
    private final BlacklistDao blacklistDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        recordDaoConfig = daoConfigMap.get(RecordDao.class).clone();
        recordDaoConfig.initIdentityScope(type);

        contactDaoConfig = daoConfigMap.get(ContactDao.class).clone();
        contactDaoConfig.initIdentityScope(type);

        cityDaoConfig = daoConfigMap.get(CityDao.class).clone();
        cityDaoConfig.initIdentityScope(type);

        telephoneDaoConfig = daoConfigMap.get(TelephoneDao.class).clone();
        telephoneDaoConfig.initIdentityScope(type);

        telInitialDaoConfig = daoConfigMap.get(TelInitialDao.class).clone();
        telInitialDaoConfig.initIdentityScope(type);

        blacklistDaoConfig = daoConfigMap.get(BlacklistDao.class).clone();
        blacklistDaoConfig.initIdentityScope(type);

        recordDao = new RecordDao(recordDaoConfig, this);
        contactDao = new ContactDao(contactDaoConfig, this);
        cityDao = new CityDao(cityDaoConfig, this);
        telephoneDao = new TelephoneDao(telephoneDaoConfig, this);
        telInitialDao = new TelInitialDao(telInitialDaoConfig, this);
        blacklistDao = new BlacklistDao(blacklistDaoConfig, this);

        registerDao(Record.class, recordDao);
        registerDao(Contact.class, contactDao);
        registerDao(City.class, cityDao);
        registerDao(Telephone.class, telephoneDao);
        registerDao(TelInitial.class, telInitialDao);
        registerDao(Blacklist.class, blacklistDao);
    }
    
    public void clear() {
        recordDaoConfig.getIdentityScope().clear();
        contactDaoConfig.getIdentityScope().clear();
        cityDaoConfig.getIdentityScope().clear();
        telephoneDaoConfig.getIdentityScope().clear();
        telInitialDaoConfig.getIdentityScope().clear();
        blacklistDaoConfig.getIdentityScope().clear();
    }

    public RecordDao getRecordDao() {
        return recordDao;
    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    public CityDao getCityDao() {
        return cityDao;
    }

    public TelephoneDao getTelephoneDao() {
        return telephoneDao;
    }

    public TelInitialDao getTelInitialDao() {
        return telInitialDao;
    }

    public BlacklistDao getBlacklistDao() {
        return blacklistDao;
    }

}
