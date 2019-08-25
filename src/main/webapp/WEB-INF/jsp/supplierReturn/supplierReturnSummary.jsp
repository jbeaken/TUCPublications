<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
Lines in return
<display:table name="supplierDeliveryLineList" requestURI="" decorator="org.bookmarks.ui.SupplierDeliveryLineDecorator">
<display:column property="stockItem.title" title="Title"/>
<display:column property="stockItem.category.name" title="Category"/>
<display:column property="amount" title="Amount"/>
</display:table>
