package main.java;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;


import java.util.Map;

public class AddPerson1 {
    public static final String MODULE = AddPerson1.class.getName();
    static String a ="";
    public static Map<String,Object> createPerson(DispatchContext dctx, Map<String,? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try{
            //Create Party first
            String partyId =  delegator.getNextSeqId("Party1");
            GenericValue party = delegator.makeValue("Party1");
            party.set("partyId",partyId);
            party.set("partyTypeId",context.get("partyTypeId"));
            delegator.create(party);
            // Step 2: Create Person1 (with same partyId)
            GenericValue person = delegator.makeValue("Person1");
            person.set("partyId", partyId);
            person.set("firstName", context.get("firstName"));
            person.set("lastName", context.get("lastName"));
            person.set("birthDate", context.get("birthDate"));
            delegator.create(person);

            // Optional Step 3: Create PartyRole1
            String roleTypeId = (String) context.get("roleTypeId");

            if (context.get("roleTypeId") != null) {
                GenericValue partyRole = delegator.makeValue("PartyRole1");
                partyRole.set("partyId", partyId);
                partyRole.set("roleTypeId", context.get("roleTypeId"));
                delegator.create(partyRole);
            }

            a = partyId;
            result.put("partyId", partyId);
            Debug.logInfo("Person1 with partyId " + partyId + " created successfully.", MODULE);
        }
        catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
            return ServiceUtil.returnError("Error creating Person1 and related entities: " + e.getMessage());
        }
        return result;
    }
}
