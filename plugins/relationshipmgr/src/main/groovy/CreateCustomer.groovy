package main.groovy
import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.base.util.*

def CreateCustomer() {
    def result = [:]
    try{
        def email = parameters.email
        def firstName = parameters.firstName
        def lastName = parameters.lastName

//        def partyId = delegator.getNextSeqId("partyId")
        def partyId = "CUST-" + System.currentTimeMillis()  // Not ideal, but avoids duplicates


        def party = delegator.makeValue("Party1")
        party.set("partyId",partyId)
        party.set("partyTypeId","PERSON")
        party.create();

        def person = delegator.makeValue("Person1")
        person.set("partyId", partyId)
        person.set("firstName", firstName)
        person.set("lastName", lastName)
        person.create()

        def partyRole=delegator.makeValue("PartyRole1")
        partyRole.set("partyId",partyId)
        partyRole.set("roleTypeId","CUSTOMER")
        partyRole.create()


        def contactMechId = delegator.getNextSeqId("ContactMech1")
        def newcontactMechId="cust-"+contactMechId;
        def contactMech = delegator.makeValue("ContactMech1")
        contactMech.set("contactMechId", newcontactMechId)
        contactMech.set("contactMechTypeId", "EMAIL")
        contactMech.set("infoString", email)
        contactMech.create()


        def partyContactMech = delegator.makeValue("PartyContactMech1")
        partyContactMech.set("partyId", partyId)
        partyContactMech.set("contactMechId", newcontactMechId)
        partyContactMech.set("contactMechPurposeId","OFFICE")
        partyContactMech.set("fromDate", UtilDateTime.nowTimestamp())
        partyContactMech.create()

        result.partyId = partyId
        result.contactMechId = newcontactMechId
    } catch (Exception e) {
        logError("Error creating customer: ${e.message}")
        return error("Error creating customer.")
    }
    return result
}

def UpdateCustomer() {
    def result = [:]
    try{
        def partyId = context.partyId
        def email = parameters.email
        def pa = delegator.makeValue("PostalAddress1")
        def newcontactMechId = context.contactMechId
        pa.set("contactMechId",newcontactMechId)
        pa.toName         = context.toName
        pa.attnName       = context.attnName
        pa.address1       = context.address1
        pa.address2       = context.address2
        pa.city           = context.city
        pa.postalCode     = context.postalCod
        pa = delegator.create(pa)

        def tn = delegator.makeValue("TelecomNumber1")
        tn.set("contactMechId",newcontactMechId)
        tn.countryCode    = context.countryCode
        tn.areaCode       = context.areaCode
        tn.contactNumber  = context.contactNumber
        tn = delegator.create(tn)

        def contactMech = delegator.makeValue("ContactMech1")
        contactMech.set("contactMechId", newcontactMechId)
        contactMech.set("contactMechTypeId", "EMAIL")
        contactMech.set("infoString", email)
        contactMech.create()

        def partyContactMech = delegator.makeValue("PartyContactMech1")
        partyContactMech.set("partyId", partyId)
        partyContactMech.set("contactMechId", newcontactMechId)
        partyContactMech.set("contactMechPurposeId","OFFICE")
        partyContactMech.set("fromDate", UtilDateTime.nowTimestamp())
        partyContactMech.create()

        result.partyId = partyId
        result.contactMechId = newcontactMechId

    }
    catch (Exception e){
        logError("Error creating customer: ${e.message}")
        return error("Error creating customer.")
    }
    return result
}
