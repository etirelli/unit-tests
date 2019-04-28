package com.sample.dttests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTest {

    private KieSession session;
    private URL kjar;
    private URLClassLoader loader;

    @BeforeEach
    public void setup() {
        // gets the kjar URL
        kjar = SampleTest.class.getResource("/decision-tables-1.0.0.jar");

        // creates the classloader to find java classes in the kjar
        loader = new URLClassLoader( new URL[]{kjar} );

        // retrieves the factory
        KieServices ks = KieServices.Factory.get();

        // Adds the kjar to the repository
        KieRepository repository = ks.getRepository();
        repository.addKieModule( ResourceFactory.newUrlResource(kjar) );

        // Create a GAV reference - using LATEST instead of a fixed version, just as an example
        ReleaseId releaseId = ks.newReleaseId("org.sample", "decision-tables", "1.0.0");

        // Load GAV jar from local maven repo
        KieContainer kieContainer = ks.newKieContainer(releaseId, loader);

        // Get session
        session = kieContainer.newKieSession();
    }

    @Test
    public void testDecisionTable() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> personClass = loader.loadClass("org.sample.decision_tables.Person");
        Method setter = personClass.getMethod("setName", String.class);
        Method getter = personClass.getMethod("getMessage" );

        // create dataset
        Object mark = personClass.newInstance();
        setter.invoke( mark, "Mark" );

        // execute rules
        session.insert(mark);
        session.fireAllRules();

        // check results
        assertEquals( "Good bye Mark", getter.invoke( mark ) );
    }

}
