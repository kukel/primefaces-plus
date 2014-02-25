/*
 * Copyright 2009-2013 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.plus.component.datatable.feature;

import java.io.IOException;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.rowexpansion.RowExpansion;
import org.primefaces.plus.component.datatable.DataTable;
import org.primefaces.plus.component.datatable.DataTableRenderer;

public class TreeFeature implements DataTableFeature {

    public void decode(FacesContext context, DataTable table) {
        throw new RuntimeException("TreeFeature should not decode.");
    }

    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        throw new RuntimeException("TreeFeature should not encode.");
    }

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    public boolean shouldEncode(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_rowExpansion");
    }
    
}
