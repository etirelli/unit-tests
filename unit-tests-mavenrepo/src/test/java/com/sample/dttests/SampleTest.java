package com.sample.dttests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.sample.decision_tables.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTest {

    private KieSession session;

    @BeforeEach
    public void setup() {
        // retrieves the factory
        KieServices ks = KieServices.Factory.get();

        // Create a GAV reference
        ReleaseId releaseId = ks.newReleaseId("org.sample", "decision-tables", "1.0.0");

        // Load GAV jar from local maven repo
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        // Get session
        session = kieContainer.newKieSession();
    }

    @Test
    public void testDecisionTable() {
        // create dataset
        Person mark = new Person();
        mark.setName("Mark");

        // execute rules
        session.insert(mark);
        session.fireAllRules();

        // check results
        assertEquals( "Good bye Mark", mark.getMessage() );
    }

}
