package com.example

import android.content.Context
import org.xmlpull.v1.XmlPullParser
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PT's PreM1", appName)
    }

    @Test
    fun `network_security_config parses cleanly with unique lowercase domains and disables cleartext`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val parser = context.resources.getXml(R.xml.network_security_config)
        val seenLowercaseDomains = mutableSetOf<String>()
        var baseCleartextAttr: String? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "base-config" -> {
                        baseCleartextAttr = parser.getAttributeValue(null, "cleartextTrafficPermitted")
                    }
                    "domain" -> {
                        val domainText = parser.nextText().trim().lowercase(Locale.US)
                        assertFalse(
                            "Duplicate case-insensitive domain '$domainText' in network_security_config",
                            seenLowercaseDomains.contains(domainText)
                        )
                        seenLowercaseDomains.add(domainText)
                    }
                }
            }
            eventType = parser.next()
        }
        parser.close()

        assertEquals("false", baseCleartextAttr)
        assertTrue(seenLowercaseDomains.contains("ptprem1.online"))
    }
}
