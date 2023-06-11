package com.orgname.test.stepdefinitions;

import com.orgname.test.AppConfig;


import cucumber.api.java8.En;
import org.springframework.test.context.ContextConfiguration;

/**
 * This class bootstraps the Spring Config for the test to run.
 *
 */
@ContextConfiguration(classes = {AppConfig.class})
public class StepsBase implements En {
}