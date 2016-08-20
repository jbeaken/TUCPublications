package org.bookmarks.domain;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@MappedSuperclass
public class AbstractNamedEntity extends AbstractEntity {

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null)
			name = name.trim();
		this.name = name;
	}

	public String toString() {
		return getId() + " " + getName();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (!(o instanceof AbstractNamedEntity))
			return false;

		// Our natural key has not been filled
		// So we must return false;
		if (getName() == null)
			return false;

		AbstractNamedEntity namedAbstractEntity = (AbstractNamedEntity) o;
		if (!(getName().equals(namedAbstractEntity.getName())))
			return false;

		return true;
	}

	public int compareTo(Object o) {
		AbstractNamedEntity that = (AbstractNamedEntity) o;
		return this.getName().compareTo(that.getName());
	}

	@Override
	public int hashCode() {
		int hash = 3;

		hash = 47 * hash + ((getName() != null) ? getName().hashCode() : 0);

		return hash;
	}
}
