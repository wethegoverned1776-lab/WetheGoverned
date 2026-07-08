package net.wetheGoverned.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AddressUtilsTest {

    @Test
    fun normalizeAddress_shouldHandleAbbreviations() {
        val addr1 = AddressUtils.normalizeAddress("123 Main St.", "New York", "10001")
        val addr2 = AddressUtils.normalizeAddress("123 Main Street", "new york", "10001-1234")
        
        assertEquals(addr1, addr2, "Abbreviations and casing should result in the same normalized string")
    }

    @Test
    fun normalizeAddress_shouldHandleDirectionals() {
        val addr1 = AddressUtils.normalizeAddress("456 N Western Ave", "Chicago", "60601")
        val addr2 = AddressUtils.normalizeAddress("456 North Western Avenue", "CHICAGO", "60601")
        
        assertEquals(addr1, addr2, "Directionals should be expanded and normalized")
    }

    @Test
    fun generateFingerprint_shouldBeConsistent() {
        val street = "789 Broadway Blvd"
        val city = "Los Angeles"
        val zip = "90001"
        
        val fp1 = AddressUtils.generateFingerprint(street, city, zip)
        val fp2 = AddressUtils.generateFingerprint("789 broadway blvd", "los angeles", "90001")
        
        assertEquals(fp1, fp2, "Fingerprints for logically identical addresses must match")
    }

    @Test
    fun generateFingerprint_shouldDistinguishDifferentAddresses() {
        val fp1 = AddressUtils.generateFingerprint("123 Main St", "Anytown", "12345")
        val fp2 = AddressUtils.generateFingerprint("124 Main St", "Anytown", "12345")
        
        assertNotEquals(fp1, fp2, "Different house numbers must result in different fingerprints")
    }
}
