package org.itracker;

import org.apache.log4j.Logger;
import org.dom4j.*;
import org.itracker.model.Entity;

import java.util.Comparator;
import java.util.List;

/**
 * Itracker specific assertions for include static to test-classes.
 */
public class Assert extends junit.framework.Assert {

    private static final Logger log = Logger.getLogger(Assert.class);

    /**
     * asserts true when comparator compares for lhs being &lt; rhs and rhs &gt; lhs.
     */
    @SuppressWarnings("unchecked")
    public static void assertEntityComparator(String message, Comparator comparator, Entity lhs, Entity rhs) {
        if (log.isDebugEnabled()) {
            log.debug("assertEntityComparator: " + message + " " + comparator);
        }
        if (null == lhs) {
            throw new IllegalArgumentException("lhs must not both be null.");
        }
        // test nullpointer

        if (null == rhs) {

            try {
                assertNull(message + " lhs, null: " + comparator, comparator.compare(lhs, rhs));
                fail();
            } catch (NullPointerException npe) {
            } // ok

            try {
                assertNull(message + " null, lhs: " + comparator, comparator.compare(rhs, lhs));
                fail();
            } catch (NullPointerException npe) {
            } // ok

            return;
        }

        assertTrue(message + " lhs, rhs: " + comparator, 0 > comparator.compare(lhs, rhs));
        assertTrue(message + " rhs, lhs: " + comparator, 0 < comparator.compare(rhs, lhs));
    }

    /**
     * asserts true when comparator compares for lhs being equal to rhs and rhs equal lhs.
     */

    @SuppressWarnings("unchecked")
    public static void assertEntityComparatorEquals(String message, Comparator comparator, Entity lhs, Entity rhs) {
        if (log.isDebugEnabled()) {
            log.debug("assertEntityComparatorEquals: " + message + " " + comparator);
        }
        if (null == lhs || null == rhs) {
            throw new IllegalArgumentException("rhs and lhs must not be null.");
        }

        assertTrue(message + " lhs, rhs: " + comparator, 0 == comparator.compare(lhs, rhs));
        assertTrue(message + " rhs, lhs: " + comparator, 0 == comparator.compare(rhs, lhs));
    }



    public static void assertContainsAll(String message, Node expected, Node actual) {
        if (expected == actual) {
            return;
        }
        assertNotNull(message, actual);
        if (expected.equals(actual)) {
            return;
        }
        if (expected instanceof Document && actual instanceof Document) {
            assertContainsAll(message + " rootElement", ((Document) expected).getRootElement(),
                    ((Document) actual).getRootElement());
        }
        if (expected instanceof Element && actual instanceof Element) {
            Element elExp = (Element)expected;
            Element elAct = (Element)actual;
            assertEquals(message + " nodeCount", elExp.nodeCount(), elAct.nodeCount());
            for (int i = 0; i < elExp.nodeCount(); i++) {
                Node node = elExp.node(i);
                if (node instanceof Element) {
                    XPath x = DocumentFactory.getInstance().createXPath(node.getPath());
                    List found = x.selectNodes(actual);
                    if (!found.isEmpty()) {
                        if (found.size() > 1) {
                            assertEquals("found size " + x, x.selectNodes(expected).size(), found.size());
                            assertContainsOne(message + " contains found " + x, (Element)node, found);
                        } else {
                            Node foundExp = x.selectSingleNode(expected);
                            if (foundExp instanceof Element && found.get(0) instanceof Element) {
                                assertElementEquals(message + " element " + x, (Element)foundExp, (Element)found.get(0));
                            }
                        }
                    }
                }
            }
        }

    }

    private static boolean equalsNodes(Node left, Node right) {
        if (left == right)
            return true;
        if (left == null)
            return false;
        if (right == null)
            return false;

        if (left.equals(right))
            return true;

        if (left.getNodeType() != right.getNodeType())
            return false;

        if (left instanceof Element) {
            if (((Element) left).attributeCount() != ((Element)right).attributeCount())
                return false;

            for (Object aLeft:((Element) left).attributes()) {
                Object aRight = ((Element) right).attribute(((Attribute) aLeft).getName());
                if (!((Attribute) aLeft).getValue()
                        .equals(((Attribute)aRight).getValue())) {
                    return false;
                }
            }
            if (((Element) left).isTextOnly()) {
                if (!left.getText().equals(right.getText())) {
                    return false;
                }
            } else {
                if (((Element) left).elements().size() != ((Element) right).elements().size())
                    return false;
                for (Object e: ((Element) left).elements()) {
                    int sizeLeft = ((Element) left).elements(((Element)e).getName()).size();
                    int sizeRight = ((Element) right).elements(((Element)e).getName()).size();
                    if (sizeLeft != sizeRight)
                        return false;
                    if (sizeLeft == 1) {
                        return equalsNodes(((Element) left).element(((Element) e).getName()),
                                ((Element) right).element(((Element) e).getName()));
                    }
                }
            }
        }

        // TODO assume equals?
        return true;
    }
    public static void assertContainsOne(String message, Element expected, List nodes) {
        if (null == expected) {
            return;
        }
        assertNotNull(message + " nodes", nodes);
        for (Object el: nodes) {
            if (el instanceof Element) {
                Element e = (Element)el;
                if (equalsNodes(expected, e))
                    return;
            }
        }
        fail(message + " did not find containing");
    }

    /**
     * Some highlevel equality test on JDOM elements.
     *
     * @param message the message
     * @param expected expected element
     * @param actual the actual element
     */
    public static void assertElementEquals(String message, Element expected, Element actual) {
        assertEquals(message, expected.isTextOnly(), actual.isTextOnly());
        assertEquals("tagname", expected.getName(), actual.getName());

        if (expected.isTextOnly()) {
            assertEquals(message + " text", expected.getText(), actual.getText());
        } else {
            assertContainsAll(message + " containsAll", expected, actual);
        }
    }

}
