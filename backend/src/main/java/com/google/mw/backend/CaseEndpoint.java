package com.google.mw.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** An endpoint class we are exposing */
@Api(name = "caseApi", version = "v1",
        namespace = @ApiNamespace(ownerDomain = "backend.mw.google.com", ownerName = "backend.mw.google.com", packagePath=""))
public class CaseEndpoint {

    @ApiMethod(name = "updateCase")
    public void updateCase(CaseBean caseBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            //Key caseBeanParentKey = KeyFactory.createKey("CaseBeanParent", caseBean.getOwner());
            //Entity caseEntity = new Entity("Case", caseBean.getId(), caseBeanParentKey);

            Entity caseEntity;
            if (caseBean.getId() == 0) {
                //Each Case is a root identity (no parent), auto-assign ID.
                caseEntity = new Entity("Case");

            } else {
                //This method of creating the Entity will simply overwrite the Entity in the datastore.
                //If syncing is needed (rather than last-update-wins), need to check value in a transaction first.
                Key caseKey = KeyFactory.createKey("Case", caseBean.getId());
                caseEntity = new Entity(caseKey);
            }

            caseEntity.setProperty("title", caseBean.getTitle());
            caseEntity.setProperty("owner", caseBean.getOwner());
            caseEntity.setProperty("dateCreated", caseBean.getDateCreated());
            caseEntity.setProperty("dateClosed", caseBean.getDateClosed());
            caseEntity.setProperty("status", caseBean.getStatus());
            caseEntity.setProperty("comments", caseBean.getComments());
            caseEntity.setProperty("latitude", caseBean.getLatitude());
            caseEntity.setProperty("longitude", caseBean.getLongitude());
            caseEntity.setProperty("notificationNeeded", caseBean.getNotificationNeeded());
//            caseEntity.setProperty("geoPt", caseBean.getGeoPt());
            datastoreService.put(caseEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
    @ApiMethod(name = "getCase")
    public CaseBean getCase(@Named("id") long id) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Key caseKey = KeyFactory.createKey("Case", id);
        Entity caseEntity = null;
        try {
            caseEntity = datastoreService.get(caseKey);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        //Entity result2 = datastoreService.get(id);

        CaseBean caseBean = convertEntitytoBean(caseEntity);
        return caseBean;
    }

    @ApiMethod(name = "getAllCases")
    public List<CaseBean> getAllCases() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Case");
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<CaseBean> caseBeans = new ArrayList<CaseBean>();
        for (Entity result : results) {
            CaseBean caseBean = convertEntitytoBean(result);

            caseBeans.add(caseBean);
        }

        return caseBeans;
    }

    @ApiMethod(name = "getMyCases")
    public List<CaseBean> getMyCases(@Named("owner") String owner) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Case");
        Query.Filter ownerFilter = new Query.FilterPredicate("owner", Query.FilterOperator.EQUAL,
                owner);
        //Query.Filter statusFilter = new Query.FilterPredicate();
        //Query.Filter comboFilter = CompositeFilterOperator.and(ownerFilter, statusFilter);
        query.setFilter(ownerFilter);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<CaseBean> caseBeans = new ArrayList<CaseBean>();
        for (Entity result : results) {
            CaseBean caseBean = convertEntitytoBean(result);
            caseBeans.add(caseBean);
        }

        return caseBeans;
    }


    @ApiMethod(name = "deleteCase")
    public void deleteCase(@Named("id") long id) {
        Key caseKey = KeyFactory.createKey("Case", id);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            //not checking if it exists, just deleting.
            datastoreService.delete(caseKey);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }


    @ApiMethod(name = "deleteAllCases")
    public void deleteAllCases() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Query query = new Query("Case");
            List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            for (Entity result : results) {
                datastoreService.delete(result.getKey());
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    //TODO: Discuss with Sandi, Nadia, Rafa the use case for this method
    //Because Endpoints can't return a primitive (boolean), this method returns the
    //List<CaseBean> and does NOT mark the cases as notification actually sent.
    //Prefer to delete this method and just keep getAllNewNotifictions... do it in one call.

    @ApiMethod(name = "hasNewNotification", path="notifications")
    public List<CaseBean> hasNewNotification(@Named("owner") String owner) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Case");
        Query.Filter ownerFilter = new Query.FilterPredicate("owner", Query.FilterOperator.EQUAL,
                owner);
        Query.Filter notificationNeededFilter = new Query.FilterPredicate("notificationNeeded", Query.FilterOperator.EQUAL,
                true);
        Query.Filter comboFilter = Query.CompositeFilterOperator.and(ownerFilter, notificationNeededFilter);
        query.setFilter(comboFilter);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        //create a list of cases that will be sent as notifications
        ArrayList<CaseBean> caseBeans = new ArrayList<CaseBean>();
        for (Entity result : results) {
            CaseBean caseBean = convertEntitytoBean(result);
            caseBeans.add(caseBean);
        }
        return caseBeans;
    }

    //TODO: Discuss with Sandi & Rafa & Nadia, what is the use case for this method?
    //Seems simpler just to use getMyCases every time.
    @ApiMethod(name = "getAllNewNotifications", path="notifications")
    public List<CaseBean> getAllNewNotifications(@Named("owner") String owner) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Case");
        Query.Filter ownerFilter = new Query.FilterPredicate("owner", Query.FilterOperator.EQUAL,
                owner);
        Query.Filter notificationNeededFilter = new Query.FilterPredicate("notificationNeeded", Query.FilterOperator.EQUAL,
                true);
        Query.Filter comboFilter = Query.CompositeFilterOperator.and(ownerFilter, notificationNeededFilter);
        query.setFilter(comboFilter);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

        //create a list of cases that will be sent as notifications
        ArrayList<CaseBean> caseBeans = new ArrayList<CaseBean>();
        for (Entity result : results) {
            CaseBean caseBean = convertEntitytoBean(result);
            caseBeans.add(caseBean);
        }

        //Update the cases so they won't be re-notified
        Transaction txn = datastoreService.beginTransaction();
        try {
            for (Entity result : results) {
                result.setProperty("notificationNeeded", false);
                datastoreService.put(result);
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }


        return caseBeans;


    }

    private CaseBean convertEntitytoBean(Entity caseEntity) {
        //todo: need some validation / protection in case of null values.
        CaseBean caseBean = new CaseBean();
        caseBean.setId(caseEntity.getKey().getId());
        caseBean.setTitle(getNullSafeString(caseEntity.getProperty("title")));
        caseBean.setOwner(getNullSafeString(caseEntity.getProperty("owner")));
        caseBean.setDateCreated((Date) caseEntity.getProperty("dateCreated"));
        caseBean.setDateClosed((Date) caseEntity.getProperty("dateClosed"));

        //caseBean.setStatus((CaseBean.CaseStatus) caseEntity.getProperty("status").toString());
        caseBean.setStatus(getNullSafeString(caseEntity.getProperty("status")));
        caseBean.setComments(getNullSafeString(caseEntity.getProperty("comments")));
        caseBean.setLatitude((Double) caseEntity.getProperty("latitude"));
        caseBean.setLongitude((Double) caseEntity.getProperty("longitude"));
        caseBean.setNotificationNeeded((Boolean) caseEntity.getProperty("notificationNeeded"));
//        GeoPt geoPt = new GeoPt((Float) caseEntity.getProperty("latitude"), (Float) caseEntity.getProperty("longitude"));
//        caseBean.setGeoPt(geoPt);


        return caseBean;
    }
    private String getNullSafeString (Object o) {
        if (o == null)
        {
            return null;
        }
        else {
            return o.toString();
        }
    }


}