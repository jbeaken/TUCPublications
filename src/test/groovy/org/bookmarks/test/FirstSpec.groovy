package org.bookmarks.test

import spock.lang.Specification

class FirstSpec {
	def "testing"() {
		expect: 
		name.size() == length
		
		where:
		name << ['test', 'testing']
		length << [4, 7]
	}
}
