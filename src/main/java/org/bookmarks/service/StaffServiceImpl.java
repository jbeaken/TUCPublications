package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.domain.Staff;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl extends AbstractService<Staff> implements StaffService {

	@Autowired
	private StaffRepository staffRepository;

	@Override
	public Repository<Staff> getRepository() {
		return staffRepository;
	}

}
