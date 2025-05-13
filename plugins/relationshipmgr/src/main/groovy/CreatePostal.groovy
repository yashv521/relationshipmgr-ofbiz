package main.groovy
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.base.util.UtilDateTime



def createPostalAddress() {
    def result = [:]
    try {
        // 1) Create the ContactMech1 record
        def cm = delegator.makeValue("ContactMech1")
        def newContactMechId = delegator.getNextSeqId("ContactMech1")
        cm.set("contactMechTypeId", "POSTAL_ADDRESS")
        cm.setNextSeqId()
        cm.setNonPKFields(context)
        cm = delegator.create(cm)


        // 2) Create the PostalAddress1 record linked to cm.contactMechId
        def pa = delegator.makeValue("PostalAddress1")
        pa.contactMechId = cm.contactMechId
        pa.toName         = context.toName
        pa.attnName       = context.attnName
        pa.address1       = context.address1
        pa.address2       = context.address2
        pa.city           = context.city
        pa.postalCode     = context.postalCode
        pa = delegator.create(pa)

        // 3) Return the new ID
        result.contactMechId = cm.contactMechId
        logInfo("Created ContactMech1 & PostalAddress1 with ID: ${cm.contactMechId}")




    } catch (GenericEntityException e) {
        // Correct logError signature: first exception, then message
        logError(e, "Error creating postal address: ${e.message}")
        return error("Unable to create postal address: ${e.message}")
    }
    return result
}
