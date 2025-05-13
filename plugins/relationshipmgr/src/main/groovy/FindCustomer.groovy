import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.entity.condition.EntityJoinOperator
import org.apache.ofbiz.entity.util.EntityFindOptions

def findCustomer(context) {
    def result = [:]
    def delegator = context.delegator

    // Create a list to hold all filter conditions
    List<EntityCondition> conditions = []

    // Add conditions based on input values
    if (context.emailAddress) {
        conditions.add(EntityCondition.makeCondition("emailAddress", EntityOperator.LIKE, "%" + context.emailAddress + "%"))
    }
    if (context.firstName) {
        conditions.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%" + context.firstName + "%"))
    }
    if (context.lastName) {
        conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%" + context.lastName + "%"))
    }
    if (context.contactNumber) {
        conditions.add(EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, "%" + context.contactNumber + "%"))
    }
    if (context.postalAddress) {
        // Match address1, city, or postalCode
        conditions.add(EntityCondition.makeCondition([
                EntityCondition.makeCondition("address1", EntityOperator.LIKE, "%" + context.postalAddress + "%"),
                EntityCondition.makeCondition("city", EntityOperator.LIKE, "%" + context.postalAddress + "%"),
                EntityCondition.makeCondition("postalCode", EntityOperator.LIKE, "%" + context.postalAddress + "%")
        ], EntityJoinOperator.OR))
    }

    // Combine conditions using AND
    EntityCondition fullCondition = conditions ? EntityCondition.makeCondition(conditions, EntityJoinOperator.AND) : null

    // Setup sorting: firstName, then lastName (like combinedName)
    List<String> orderBy = []
    if ("Y".equalsIgnoreCase(context.sortByCombinedName)) {
        orderBy.add("firstName")
        orderBy.add("lastName")
    }

    // Pagination setup
    int viewIndex = context.viewIndex ? context.viewIndex.toInteger() : 0
    int viewSize = context.viewSize ? context.viewSize.toInteger() : 20
    int startIndex = viewIndex * viewSize

    // Get all matching records from the view entity
    List<GenericValue> allResults = delegator.findList(
            "FindCustomerView",
            fullCondition,
            null,
            orderBy,
            new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true)
    )

    int totalCount = allResults.size()

    // Get only the records for the current page
    List<GenericValue> currentPageResults = allResults.subList(
            Math.min(startIndex, totalCount),
            Math.min(startIndex + viewSize, totalCount)
    )

    // Prepare result
    result.partyList = currentPageResults
    result.totalCount = totalCount
    result.viewIndex = viewIndex
    result.viewSize = viewSize

    return result
}

// OFBiz will call this directly — pass the context to our method
return findCustomer(context)
