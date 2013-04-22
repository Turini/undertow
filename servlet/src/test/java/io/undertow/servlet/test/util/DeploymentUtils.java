/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.undertow.servlet.test.util;

import javax.servlet.ServletException;

import io.undertow.server.handlers.CookieHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.form.FormEncodedDataHandler;
import io.undertow.server.handlers.form.MultiPartHandler;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.test.SimpleServletTestCase;
import io.undertow.test.utils.DefaultServer;

/**
 * @author Stuart Douglas
 */
public class DeploymentUtils {

    /**
     * Sets up a simple servlet deployment with the provided servlets.
     *
     * This is just a convenience method for simple deployments
     *
     * @param servlets The servlets to add
     */
    public static Deployment setupServlet(final ServletInfo... servlets) {

        final PathHandler pathHandler = new PathHandler();
        final FormEncodedDataHandler formEncodedDataHandler = new FormEncodedDataHandler(pathHandler);
        final MultiPartHandler multiPartHandler = new MultiPartHandler(formEncodedDataHandler);
        CookieHandler cookieHandler = new CookieHandler(multiPartHandler);
        final ServletContainer container = ServletContainer.Factory.newInstance();
        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(SimpleServletTestCase.class.getClassLoader())
                .setContextPath("/servletContext")
                .setClassIntrospecter(TestClassIntrospector.INSTANCE)
                .setDeploymentName("servletContext.war")
                .addServlets(servlets);
        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();
        try {
            pathHandler.addPath(builder.getContextPath(), manager.start());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        DefaultServer.setRootHandler(cookieHandler);

        return manager.getDeployment();

    }



}
