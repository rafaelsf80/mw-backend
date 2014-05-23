package backend;

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
@Api(name = "caseApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend", ownerName = "backend", packagePath=""))
public class CaseEndpoint {


    @ApiMethod(name = "storeCase")
    public void storeCase(CaseBean caseBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key caseBeanParentKey = KeyFactory.createKey("CaseParent", "cases");
            Entity caseEntity = new Entity("Case", caseBean.getId(), caseBeanParentKey);
            caseEntity.setProperty("caseTitle", caseBean.getCaseTitle());
            caseEntity.setProperty("caseOwner", caseBean.getCaseOwner());
            caseEntity.setProperty("caseCreated", caseBean.getCaseCreated());
            caseEntity.setProperty("caseClosed", caseBean.getCaseClosed());
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
        Key caseBeanParentKey = KeyFactory.createKey("CaseBeanParent", "cases");

        Key caseKey = KeyFactory.createKey("Case", id);
        Entity result = null;
        try {
            result = datastoreService.get(caseKey);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        CaseBean caseBean = new CaseBean();
//        caseBean.setId(result.getKey().getId());
//        caseBean.setCaseTitle((String) result.getProperty("caseTitle"));
//        caseBean.setCaseOwner((String) result.getProperty("caseOwner"));
//        caseBean.setCaseCreated((Date) result.getProperty("caseCreated"));
//        caseBean.setCaseClosed((Date) result.getProperty("caseClosed"));

        caseBean.setId(id);
        caseBean.setCaseTitle("Feed the cat");
        caseBean.setCaseOwner("Matt");
        //caseBean.setCaseCreated();
        //caseBean.setCaseClosed();


        return caseBean;
    }

    @ApiMethod(name = "getAllCases")
    public List<CaseBean> getAllCases() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key caseBeanParentKey = KeyFactory.createKey("CaseBeanParent", "cases");
        Query query = new Query(caseBeanParentKey);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<CaseBean> caseBeans = new ArrayList<CaseBean>();
        for (Entity result : results) {
            CaseBean caseBean = new CaseBean();
            caseBean.setId(result.getKey().getId());
            caseBean.setCaseTitle((String) result.getProperty("caseTitle"));
            caseBean.setCaseOwner((String) result.getProperty("caseOwner"));
            caseBean.setCaseCreated((Date) result.getProperty("caseCreated"));
            caseBean.setCaseClosed((Date) result.getProperty("caseClosed"));
            caseBeans.add(caseBean);
        }

        return caseBeans;
    }

    @ApiMethod(name = "getMyCases")
    public List<CaseBean> getMyCases(@Named("caseOwner") String caseOwner) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        //Key caseBeanParentKey = KeyFactory.createKey("CaseBeanParent", "cases");
        Query query = new Query("Case");
        Query.Filter ownerFilter = new Query.FilterPredicate("caseOwner", Query.FilterOperator.EQUAL,
                caseOwner);
        //Query.Filter statusFilter = new Query.FilterPredicate();
        //Query.Filter comboFilter = CompositeFilterOperator.and(ownerFilter, statusFilter);
        query.setFilter(ownerFilter);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<CaseBean> caseBeans = new ArrayList<CaseBean>();
        for (Entity result : results) {
            CaseBean caseBean = new CaseBean();
            caseBean.setId(result.getKey().getId());
            caseBean.setCaseTitle((String) result.getProperty("caseTitle"));
            caseBean.setCaseOwner((String) result.getProperty("caseOwner"));
            caseBean.setCaseCreated((Date) result.getProperty("caseCreated"));
            caseBean.setCaseClosed((Date) result.getProperty("caseClosed"));
            caseBeans.add(caseBean);
        }

        return caseBeans;
    }


    @ApiMethod(name = "deleteCase")
    public void deleteCase(@Named("id") long id) {

    }


    @ApiMethod(name = "deleteAllCases")
    public void deleteAllCases() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key caseBeanParentKey = KeyFactory.createKey("CaseBeanParent", "cases");
            Query query = new Query(caseBeanParentKey);
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


}