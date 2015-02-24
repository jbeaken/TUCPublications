<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
		  	<fieldset>		
				<legend>View Customer</legend>
				<p>
					${customer.firstName}
				</p>
				<p>
					${customer.lastName}
				</p>
				<p>
					${customer.address.address1}
				</p>
				<p>
					${customer.address.address2}
				</p>				
				<p>
					${customer.address.address3}
				</p>				
			</fieldset>

