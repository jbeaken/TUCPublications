package org.bookmarks.service;

import com.ecwid.maleorang.MailchimpClient;
import com.ecwid.maleorang.MailchimpObject;
import com.ecwid.maleorang.method.v3_0.lists.members.EditMemberMethod;
import com.ecwid.maleorang.method.v3_0.lists.members.MemberInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class MailchimpService {

	@Value("#{ applicationProperties['mailchimp-api-key'] }")
	private String apiKey;

	@Value("#{ applicationProperties['mailchimp-list-id'] }")
	private String listId;	

	public MemberInfo subscribe(String email, String firstname, String lastname) throws Exception {
		try (MailchimpClient client = new MailchimpClient(apiKey)) {
			EditMemberMethod.CreateOrUpdate method = new EditMemberMethod.CreateOrUpdate(listId, email);
			method.status = "subscribed";
			method.merge_fields = new MailchimpObject();
			method.merge_fields.mapping.put("FNAME", firstname);
			method.merge_fields.mapping.put("LNAME", lastname);

			MemberInfo member = client.execute(method);

			return member;
		}
	}
}
