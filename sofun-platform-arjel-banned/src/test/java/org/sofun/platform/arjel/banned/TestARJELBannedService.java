/*
 * Copyright (c)  Sofun Gaming SAS.
 * Copyright (c)  Julien Anguenot <julien@anguenot.org>
 * Copyright (c)  Julien De Preaumont <juliendepreaumont@gmail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Julien Anguenot <julien@anguenot.org> - initial API and implementation
*/

package org.sofun.platform.arjel.banned;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountStatus;
import org.sofun.core.api.member.MemberAccountType;
import org.sofun.core.member.MemberImpl;
import org.sofun.platform.arjel.banned.api.ARJELBannedService;

/**
 * Testing ARJEL Banned Service
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestARJELBannedService extends TestCase {

    private ARJELBannedService arjel;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        arjel = new ARJELBannedServiceImpl();
    }

    @Override
    protected void tearDown() throws Exception {
        arjel = null;
        super.tearDown();
    }

    @Test
    public void testCanonical_1() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1970-02-28";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("Jean");
        member.setLastName("Dupont");
        final String canonical = arjel.getCanonicalFor(member);
        assertEquals("JEANDUPONT19700228", canonical);

    }

    @Test
    public void testCanonical_2() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1970-02-28";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("Lætitia");
        member.setLastName("LÆN");
        final String canonical = arjel.getCanonicalFor(member);
        assertEquals("LAETITIALAEN19700228", canonical);

    }

    @Test
    public void testCanonical_3() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1970-02-28";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("Éléonore");
        member.setLastName("Raphaël OEne");
        final String canonical = arjel.getCanonicalFor(member);
        assertEquals("ELEONORERAPHAELOENE19700228", canonical);

    }

    @Test
    public void testCanonical_4() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1970-02-28";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("Julien");
        member.setLastName("De Préaumont");
        final String canonical = arjel.getCanonicalFor(member);
        assertEquals("JULIENDEPREAUMONT19700228", canonical);

    }

    @Test
    public void testCanonical_5() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1970-02-28";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("Jean-Christophe");
        member.setLastName("D'Anjoux");
        final String canonical = arjel.getCanonicalFor(member);
        assertEquals("JEANCHRISTOPHEDANJOUX19700228", canonical);

    }

    @Test
    public void testHash_1() throws Exception {

        final String canonical = "JEANDUPONT19700230";
        final String secret = "Secret!";
        final String hash = arjel.getHashFor(canonical, secret);
        assertEquals(hash, "56a48a5d07a0f82108f9032fc01af423d45085f8");

    }

    @Test
    public void testHash_2() throws Exception {

        final String canonical = "LAETITIALAEN19700230";
        final String secret = "123456";
        final String hash = arjel.getHashFor(canonical, secret);
        assertEquals(hash, "61f74c57b5e7eb1b9ca944d1d258a4cddb23a7cd");

    }

    @Test
    public void testHash_3() throws Exception {

        final String canonical = "ELEONORERAPHAELOENE19700230";
        final String secret = "Bonjour1";
        final String hash = arjel.getHashFor(canonical, secret);
        assertEquals(hash, "f3b9d28ce7ee70d3125d1d5f26f6fc311b1f2539");

    }

    @Test
    public void testRecordCheck_1() throws Exception {

        final String hash = "5ab8104881ba5eefed4adfbaf83fcf5bc455ef53";
        final String record = arjel.getRecordsFor(hash);
        assertEquals("SOCCIA;HAUTE-CORSE;FRANCE", record);

    }

    @Test
    public void testRecordCheck_2() throws Exception {

        final String hash = "86c38fee0b18db5e4e591923d166e73f35d220c4";
        final String record = arjel.getRecordsFor(hash);
        assertEquals("PARIS;PARIS;FRANCE", record);

    }

    @Test
    public void testRecordCheck_3() throws Exception {

        final String hash = "31b92c5c4e656c4ce96c6328847a969833afb77c";
        final String record = arjel.getRecordsFor(hash);
        assertEquals("HAGUENAU;BAS-RHIN;FRANCE", record);

    }

    @Test
    public void testRecordCheck_4() throws Exception {

        final String hash = "00a5ab0db7b6f8cfb8bdb57f24c5442e00bd8731";
        final String record = arjel.getRecordsFor(hash);
        assertEquals("LYON;RHONE;FRANCE", record);

    }

    @Test
    // This record has a very strange behavior. I suspect one of the ARJEL DNS
    // server is not configured properly.
    public void xtestRecordCheck_5() throws Exception {

        final String hash = "e89e97b294db2f7afd26bf8e066eec68878105a8";
        final String record = arjel.getRecordsFor(hash);
        assertEquals("MARSEILLE;BOUCHES-DU-RHONE;FRANCE", record);

    }

    @Test
    public void testRecordCheck_6() throws Exception {

        final String hash = "b1c10e8d6ec0f403e82497c5857cf93989ed7d67";
        final String record = arjel.getRecordsFor(hash);
        assertEquals("SAINT-GERMAIN-EN-LAYE;YVELINES;FRANCE", record);

    }

    @Test
    public void testRecordCheck_7() throws Exception {

        final String hash = "ed6ee5da570911f37ac65cfa8daf4026e25d516b";
        final String record = arjel.getRecordsFor(hash);
        assertEquals("POINTE-A-PITRE;GUADELOUPE;FRANCE", record);

    }

    @Test
    public void testRecordCheck_8() throws Exception {

        final String hash = "56a48a5d07a0f82108f9032fc01af423d45085f8";
        final String record = arjel.getRecordsFor(hash);
        assertNull(record);

    }

    @Test
    public void testBannedMember_1() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1981-10-02";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("Julien");
        member.setLastName("De Préaumont");

        assertFalse(arjel.isBanned(member, true));

    }

    @Test
    public void testBannedMember_2() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1929-10-17";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("ghijkl");
        member.setLastName("abcdef");
        member.setBirthCountry("FRANCE");
        member.setBirthPlace("SOCCIA");
        member.setBirthArea("HAUTE-CORSE");

        assertTrue(arjel.isBanned(member, true));

    }
    
    @Test
    public void testBannedMember_2_namesake() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1929-10-17";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("ghijkl");
        member.setLastName("abcdef");
        member.setBirthCountry("FRANCE");
        member.setBirthPlace("RIGNY");
        member.setBirthArea("HAUTE-SAONE");

        assertFalse(arjel.isBanned(member, true));

    }

    @Test
    public void testBannedMember_3() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1953-11-01";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("Ghijkl");
        member.setLastName("Abcdef");
        member.setBirthCountry("FRANCE");
        member.setBirthPlace("PARIS");
        member.setBirthArea("PARIS");

        assertTrue(arjel.isBanned(member, true));

    }

    @Test
    public void testBannedMember_4() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1972-12-12";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("Ghi jkl");
        member.setLastName("Abc Def");
        member.setBirthCountry("FRANCE");
        member.setBirthPlace("HAGUENAU");
        member.setBirthArea("BAS-RHIN");

        assertTrue(arjel.isBanned(member, true));

    }

    @Test
    public void testBannedMember_5() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1989-04-28";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("GhÏ j k l");
        member.setLastName("Àb c D É f");
        member.setBirthCountry("FRANCE");
        member.setBirthPlace("LYON");
        member.setBirthArea("RHONE");

        assertTrue(arjel.isBanned(member, true));

    }

    @Test
    public void testBannedMember_6() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1989-04-28";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("=?ghi!@_ -jkl");
        member.setLastName("ABC()*+./:DEF");
        member.setBirthCountry("FRANCE");
        member.setBirthPlace("LYON");
        member.setBirthArea("RHONE");

        assertTrue(arjel.isBanned(member, true));

    }

    @Test
    public void testBannedMember_7() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1938-10-30";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("àâäçéèêëîïôöùûüÿæ¿");
        member.setLastName("ÀÂÄÇÉÈÊËÎÏÔÖÙÛÜ¿Æ¿");
        member.setBirthCountry("FRANCE");
        member.setBirthPlace("SAINT-GERMAIN-EN-LAYE");
        member.setBirthArea("YVELINES");
        
        assertTrue(arjel.isBanned(member, true));

    }

    @Test
    public void testBannedMember_8() throws Exception {

        Member member = new MemberImpl("noreply@sofungaming.com",
                MemberAccountStatus.CONFIRMED, MemberAccountType.GAMBLING_FR);
        final String birthDateStr = "1947-02-07";
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        final Date birthDate = sf.parse(birthDateStr);
        member.setBirthDate(birthDate);
        member.setFirstName("àâäçéèêëîïô 456");
        member.setLastName("ÀÂÄÇÉÈÊËÎÏÔ 123");
        member.setBirthCountry("FRANCE");
        member.setBirthPlace("POINTE-A-PITRE");
        member.setBirthArea("GUADELOUPE");

        assertTrue(arjel.isBanned(member, true));

    }

}
