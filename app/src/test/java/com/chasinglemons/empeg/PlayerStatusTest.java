package com.chasinglemons.empeg;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PlayerStatusTest {
    @Test public void acceptsExtraFieldsAndAnyOrder() {
        PlayerStatus status = PlayerStatus.parse("notify_Title = \"mer06\";\n"
                + "notify_Duration = \"123\";notify_RunLen = \"1\";"
                + "notify_FidTime = \"1234  0:00:46\";notify_Artist = \"Historic Sounds\";");
        assertEquals("Historic Sounds - mer06 (00:46)", status.displayText());
    }

    @Test public void missingTimeAndArtistDoNotDiscardTitle() {
        assertEquals("mer06", PlayerStatus.parse("notify_Title=\"mer06\";").displayText());
        assertEquals("Empeg connected", PlayerStatus.parse("").displayText());
        assertEquals("Empeg connected", PlayerStatus.parse(null).displayText());
    }

    @Test public void malformedTimeDoesNotCrash() {
        assertEquals("Track", PlayerStatus.parse("notify_Title=\"Track\";"
                + "notify_FidTime=\"1234\";").displayText());
    }

    @Test public void escapedQuotesAndTwoPartTimeArePreserved() {
        PlayerStatus status = PlayerStatus.parse("notify_Artist=\"The \\\"Band\\\"\";"
                + "notify_Title=\"Song\";notify_FidTime=\"1234  12:34\";");
        assertEquals("The \"Band\" - Song (12:34)", status.displayText());
    }
}
