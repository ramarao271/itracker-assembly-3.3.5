package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

import static org.itracker.Assert.assertEntityComparator;
import static org.itracker.Assert.assertEntityComparatorEquals;
import static org.junit.Assert.*;

public class IssueAttachmentTest {
    private IssueAttachment iss;

    @Test
    public void testSetIssue() {
        try {
            iss.setIssue(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetOriginalFileName() {
        try {
            iss.setOriginalFileName(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetFileExtension() {
        iss.setOriginalFileName("abc.txt");
        assertEquals("FileExtension is txt", ".txt", iss.getFileExtension());

        iss.setOriginalFileName("abc.txt.c");
        assertEquals("FileExtension is c", ".c", iss.getFileExtension());

        iss.setOriginalFileName(".c");
        assertEquals("FileExtension is empty", "", iss.getFileExtension());

        iss.setOriginalFileName("abc");
        assertEquals("FileExtension is empty", "", iss.getFileExtension());
    }

    @Test
    public void testSetFileData() {
        try {
            iss.setFileData(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testgetFileData() {
        try {
            iss.setFileData(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        assertNull("file data is null", iss.getFileData());
    }

    @Test
    public void testSetType() {
        try {
            iss.setType(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testToString() {
        assertNotNull("toString", iss.toString());

    }

    /**
     * TODO: does it still fail sometimes?
     */
    @Test
    @Ignore
    public void testSizeComparator() {
        final IssueAttachment attA, attB;
        final Issue issueA, issueB;
        issueA = new Issue();
        issueB = new Issue();

        attA = new IssueAttachment(issueA, "aaa", "type", "desc", 200l);
        attB = new IssueAttachment(issueB, "bbb", "type", "desc", 200l);


        assertEntityComparator("size1 comparator: attA, attB",
                IssueAttachment.SIZE_COMPARATOR, attA, attB);
        assertEntityComparator("size1 comparator: attA, null",
                IssueAttachment.SIZE_COMPARATOR, attA, null);

        attA.setSize(attB.getSize());

        assertEntityComparator("size2 comparator: attA, attB",
                IssueAttachment.SIZE_COMPARATOR, attA, attB);
        assertEntityComparator("size2 comparator: attA, null",
                IssueAttachment.SIZE_COMPARATOR, attA, null);

        attA.setOriginalFileName(attB.getOriginalFileName());

        assertEntityComparatorEquals("size3 comparator: attA, attB",
                IssueAttachment.SIZE_COMPARATOR, attA, attB);
        assertEntityComparatorEquals("size3 comparator: attA, attA",
                IssueAttachment.SIZE_COMPARATOR, attA, attA);
    }

    @Test
    public void testOriginalFilenameComparator() {
        IssueAttachment attA, attB;
        Issue issueA, issueB;
        issueA = new Issue();
        issueB = new Issue();

        attA = new IssueAttachment(issueA, "aaa", "type", "desc", 200l);
        attA.setCreateDate(new Date());
        attB = new IssueAttachment(issueB, "bbb", "type", "desc", 200l);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
        attB.setCreateDate(new Date());


        assertEntityComparator("filename comparator",
                IssueAttachment.ORIGIINAL_FILENAME_COMPARATOR, attA, attB);
        assertEntityComparator("filename comparator",
                IssueAttachment.ORIGIINAL_FILENAME_COMPARATOR, attA, null);

        attA.setOriginalFileName(attB.getOriginalFileName());

        assertEntityComparator("filename comparator",
                IssueAttachment.ORIGIINAL_FILENAME_COMPARATOR, attA, attB);
        assertEntityComparator("filename comparator",
                IssueAttachment.ORIGIINAL_FILENAME_COMPARATOR, attA, null);

        attA.setCreateDate(attB.getCreateDate());

        assertEntityComparatorEquals("filename comparator",
                IssueAttachment.ORIGIINAL_FILENAME_COMPARATOR, attA, attB);
        assertEntityComparatorEquals("filename comparator",
                IssueAttachment.ORIGIINAL_FILENAME_COMPARATOR, attA, attA);
    }


    @Before
    public void setUp() throws Exception {
        iss = new IssueAttachment();
    }

    @After
    public void tearDown() throws Exception {
        iss = null;
    }

}
