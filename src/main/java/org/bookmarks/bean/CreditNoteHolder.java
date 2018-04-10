package org.bookmarks.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bookmarks.domain.CreditNote;

public class CreditNoteHolder {

	private Map<String, CreditNote> creditNoteMap = new HashMap<>();

	private Integer noOfLines = 0;

	public Map<String, CreditNote> getCreditNoteMap() {
		return creditNoteMap;
	}

	public Object getNoOfAlreadyProcessed() {
		return creditNoteMap.values().stream().filter(cn -> cn.getStatus().equals("Already Processed")).count();
	}

	public Long getNoUnmatched() {
		return creditNoteMap.values().stream().filter(cn -> cn.getStatus().equals("Unmatched")).count();
	}

	public int getNoMatched() {
		return getMatched().size();
	}

	public void incrementNoOfLines() {
		noOfLines++;
	}

	public Integer getNoOfLines() {
		return noOfLines;
	}

	public double getTotalAmountInPounds() {
		return creditNoteMap.values().stream().mapToDouble(i -> i.getAmount().doubleValue()).sum();
	}

	public List<CreditNote> getMatched() {
		//Must match Secondary Match and Primary March
		return creditNoteMap.values().stream().filter(cn -> cn.getStatus().contains("Primary Match") || cn.getStatus().contains("Secondary Match")).collect(Collectors.toList());
	}


	public List<CreditNote> getAlreadyProcessed() {
		return creditNoteMap.values().stream().filter(cn -> cn.getStatus().equals("Already Processed")).collect(Collectors.toList());
	}



	public List<CreditNote> getClubAccounts() {
		return creditNoteMap.values().stream().filter(cn -> cn.isClubAccount()).collect(Collectors.toList());
	}

	public int getNoOfClubAccounts() {
		return getClubAccounts().size();
	}

	public int getNoOfClubAccountsUnprocessed() {
		return getClubAccounts().stream().filter(cn -> !cn.getStatus().equals("Already Processed")).collect(Collectors.toList()).size();
	}

	public CreditNote getCreditNote(String transactionDescription) {
			return creditNoteMap.get(transactionDescription);
	}
}