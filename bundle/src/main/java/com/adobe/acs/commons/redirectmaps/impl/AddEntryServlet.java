/*
 * #%L
 * ACS AEM Commons Bundle
 * %%
 * Copyright (C) 2013 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.acs.commons.redirectmaps.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.redirectmaps.models.RedirectMapModel;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * Servlet for adding a line into the redirect map text file
 */
@SlingServlet(methods = { "POST" }, resourceTypes = {
        "acs-commons/components/utilities/redirectmappage" }, selectors = {
                "addentry" }, extensions = { "json" }, metatype = false)
public class AddEntryServlet extends RedirectEntriesServlet {

    private static final long serialVersionUID = -1704915461516132101L;
    private static final Logger log = LoggerFactory.getLogger(AddEntryServlet.class);

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        log.trace("doPost");

        int idx = Integer.parseInt(request.getParameter("idx"), 10);
        String source = request.getParameter("source");
        String target = request.getParameter("target");
        log.debug("Removing entry with {} {} at {}",source, target, idx);

        InputStream is = request.getResource().getChild(RedirectMapModel.MAP_FILE_NODE).adaptTo(InputStream.class);
        List<String> lines = IOUtils.readLines(is);
        log.debug("Loaded {} lines", lines.size());

        lines.add(idx, source+" "+target);
        log.debug("Added entry...");

        ModifiableValueMap mvm = request.getResource().getChild(RedirectMapModel.MAP_FILE_NODE)
                .getChild(JcrConstants.JCR_CONTENT).adaptTo(ModifiableValueMap.class);
        mvm.put(JcrConstants.JCR_DATA, StringUtils.join(lines, "\n"));
        request.getResourceResolver().commit();
        request.getResourceResolver().refresh();
        log.debug("Changes saved...");

        super.doGet(request, response);
    }
}