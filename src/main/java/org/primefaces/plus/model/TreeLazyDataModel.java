/*
 * Part of this code is:
 * 
 * Copyright 2009-2013 PrimeTek.
 *
 * The other part is 
 * 
 * Copyright 2012 Ronald van Kuijk 
 *
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
package org.primefaces.plus.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;



public abstract class TreeLazyDataModel<T> extends LazyDataModel<T> implements SelectableDataModel<T>, Serializable {
 
	private int rowIndex = -1;

	private int rowCount;

	private int pageSize;

	private List<T> data;
	
	int childRows;
	
	public TreeLazyDataModel() {
		super();
	}
	
	public boolean isRowAvailable() {
		if(data == null) {
            return false;
        }

		return rowIndex >= 0 && rowIndex < data.size();
	}

	public int getRowCount() {
		return rowCount;
	}

	public T getRowData() {
        return data.get(rowIndex);
	}

    public int getRowIndex() {
        return this.rowIndex;
    }
    
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex == -1 ? rowIndex : (rowIndex > data.size() ? -1 : rowIndex);
    }

	public Object getWrappedData() {
		return data;
	}
	public void setWrappedData(Object list) {
		this.data = (List) list;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
        throw new UnsupportedOperationException("Lazy loading is not implemented.");
    }
    
    public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String,String> filters) {
        throw new UnsupportedOperationException("Lazy loading is not implemented.");
    }

    public T getRowData(String rowKey) {
        throw new UnsupportedOperationException("getRowData(String rowKey) must be implemented when basic rowKey algorithm is not used.");
    }

    public Object getRowKey(T object) {
        throw new UnsupportedOperationException("getRowKey(T object) must be implemented when basic rowKey algorithm is not used.");
    }
	
	
	public void setRowChildrenCount(int childRows) {
		this.childRows = childRows;
	}
	
    public int getRowChildrenCount() {
    	return this.childRows;
    }
    
}
