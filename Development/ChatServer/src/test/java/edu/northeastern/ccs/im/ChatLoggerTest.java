package edu.northeastern.ccs.im;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.util.reflection.Fields;

import static org.junit.Assert.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


public class ChatLoggerTest {

    FileReader fr;
    File f;
    BufferedReader br;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws FileNotFoundException {

        ChatLogger.info("start");
        f = new File("edu.northeastern.ccs.im.ChatLogger.log");
        br = new BufferedReader(new FileReader(f));
    }

    @Test
    public void testInfo() throws IOException {

        ChatLogger.info("this is some info");
        StringBuilder result = new StringBuilder();
        String next;

        while((next = br.readLine()) != null){
            result.append(next + "\n");
        }
        assertTrue(result.toString().contains("this is some info"));
    }

    @Test
    public void testWarning() throws IOException {

        ChatLogger.warning("this is a warning message");
        StringBuilder result = new StringBuilder();
        String next;

        while((next = br.readLine()) != null){
            result.append(next + "\n");
        }

        assertTrue(result.toString().contains("this is a warning message"));
    }

    @Test
    public void testError() throws IOException {

        ChatLogger.error("this is an error message");
        StringBuilder result = new StringBuilder();
        String next;

        while((next = br.readLine()) != null){
            result.append(next + "\n");
        }

        assertTrue(result.toString().contains("this is an error message"));
    }

    @Test
    public void testEnum() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class[] c = ChatLogger.class.getDeclaredClasses();
        Class<ChatLogger> clazz = ChatLogger.class;
        Method method = c[0].getDeclaredMethod("values");
        method.setAccessible(true);
        Object obj = method.invoke(null);
        System.out.println(Arrays.toString((Object[]) obj));
        Method[] m = clazz.getDeclaredMethods();
        Method met = null;
        for (Method method1 : m) {
            if (method1.getName().contains("setMode")) {
                met = method1;
            }
        }
        //Method method2 = ChatLogger.class.getDeclaredMethod("turnOnLogging");
        Object o = met.invoke( null,((Object[]) obj)[0]);
        Object o1 = met.invoke( null,((Object[]) obj)[1]);
        Object o2 = met.invoke( null,((Object[]) obj)[2]);

    }

    @Test
    public void testSetMode() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        thrown.expect(InvocationTargetException.class);
        //thrown.expectMessage("State cannot be null");

        Class<ChatLogger> clazz = ChatLogger.class;
        Constructor<?>[] construct = clazz.getDeclaredConstructors();

        construct[0].setAccessible(true);
        construct[0].newInstance();


    }
}