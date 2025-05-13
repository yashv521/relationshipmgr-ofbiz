package main.groovy

import main.java.AddPerson1
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.base.util.UtilDateTime

def createTelecomNumber() {
    def result = [:]
    try {
        // Read partyId from context (this must be passed via form or screen)
        //Made a service to enter telecom details in my own made telephone1 entity
        println("=================================================================================="+parameters.partyId)
        def partyId = context.partyId
        if (!partyId) {
            return error("Missing partyId for telecom creation.")
        }
        // 1) Create ContactMech1
        def cm = delegator.makeValue("ContactMech1")
        cm.set("contactMechTypeId", "TELECOM_NUMBER")
        cm.setNextSeqId()                  // generate contactMechId
        cm.setNonPKFields(context)         // sets contactMechTypeId + infoString
        cm = delegator.create(cm)

        //1.1)Partyid


        // 2) Create TelecomNumber1 linked to it
        def tn = delegator.makeValue("TelecomNumber1")
        tn.contactMechId  = cm.contactMechId
        tn.countryCode    = context.countryCode
        tn.areaCode       = context.areaCode
        tn.contactNumber  = context.contactNumber
        tn = delegator.create(tn)

        // 3) Create PartyContactMech1

        partyId= AddPerson1.a
        println(partyId)

        def pcm = delegator.makeValue("PartyContactMech1")
        pcm.partyId = partyId
        pcm.contactMechId = cm.contactMechId
        pcm.fromDate = UtilDateTime.nowTimestamp()
        pcm = delegator.create(pcm)

        logInfo("PartyID:${pcm.partyId}")


        // 4) Return the new ID
        result.contactMechId = cm.contactMechId
        logInfo("Created ContactMech1 & TelecomNumber1 with ID: ${cm.contactMechId}")



    } catch (GenericEntityException e) {
        // Correct signature: first pass the Throwable, then the message
        println(e, "Error creating telecom number: ${e.message}")
        return error("Unable to create telecom number: ${e.message}")
    }
    return result
}
