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
package org.primefaces.plus.component.baribbon;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;

import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.WidgetBuilder;

public class BaribbonRenderer extends BaseMenuRenderer {

    @Override
	protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        Baribbon menubar = (Baribbon) abstractMenu;
		String clientId = menubar.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Baribbon", menubar.resolveWidgetVar(), clientId)
            .attr("autoDisplay", menubar.isAutoDisplay());
		
        wb.finish();        	
	}

    @Override
	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        Baribbon menubar = (Baribbon) abstractMenu;
        String style = menubar.getStyle();
        String styleClass = menubar.getStyleClass();
        styleClass = styleClass == null ? Baribbon.CONTAINER_CLASS : Baribbon.CONTAINER_CLASS + " " + styleClass;

        encodeMenu(context, menubar, style, styleClass, "baribbon");
	}
    
    protected void encodeMenu(FacesContext context, AbstractMenu menu, String style, String styleClass, String role) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent optionsFacet = menu.getFacet("options");
        
        writer.startElement("div", menu);
		writer.writeAttribute("id", menu.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", role, null);

		writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

        if(menu.getElementsCount() > 0) {
            encodeElements(context, menu, menu.getElements());
        }
        
        if(optionsFacet != null) {
            writer.startElement("li", null);
            writer.writeAttribute("class", Menu.OPTIONS_CLASS, null);
            writer.writeAttribute("role", "menuitem", null);
            optionsFacet.encodeAll(context);
            writer.endElement("li");
        }
		
		writer.endElement("ul");

        writer.endElement("div");
    }
    
    protected void encodeElements(FacesContext context, AbstractMenu menu, List<MenuElement> elements) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        for(MenuElement element : elements) {
            if(element.isRendered()) {
                if(element instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    writer.writeAttribute("role", "menuitem", null);
                    encodeMenuItem(context, menu, (MenuItem) element);
                    writer.endElement("li");
                }
                else if(element instanceof Submenu) {
                    Submenu submenu = (Submenu) element;
                    String style = submenu.getStyle();
                    String styleClass = submenu.getStyleClass();
                    styleClass = styleClass == null ? Menu.TIERED_SUBMENU_CLASS : Menu.TIERED_SUBMENU_CLASS + " " + styleClass;
        
                    writer.startElement("li", null);
                    writer.writeAttribute("class", styleClass, null);
                    if(style != null) {
                        writer.writeAttribute("style", style, null);
                    }
                    writer.writeAttribute("role", "menuitem", null);
                    writer.writeAttribute("aria-haspopup", "true", null);
                    encodeSubmenu(context, menu, submenu);
                    writer.endElement("li");
                } 
                else if(element instanceof Separator) {
                    encodeSeparator(context, (Separator) element);
                }
            }
        }
    }
    
    protected void encodeSubmenu(FacesContext context, AbstractMenu menu, Submenu submenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();
        String label = submenu.getLabel();

        //title
        writer.startElement("a", null);
        writer.writeAttribute("href", "javascript:void(0)", null);
        writer.writeAttribute("class", Menu.MENUITEM_LINK_CLASS, null);

        if(icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_ICON_CLASS + " " + icon, null);
            writer.endElement("span");
        }

        if(label != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_TEXT_CLASS, null);
            writer.writeText(submenu.getLabel(), "value");
            writer.endElement("span");
        }
        
        encodeSubmenuIcon(context, submenu);

        writer.endElement("a");

        //submenus and menuitems
        if(submenu.getElementsCount() > 0) {
            writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.TIERED_CHILD_SUBMENU_CLASS, null);
            writer.writeAttribute("role", "menu", null);
			encodeElements(context, menu, submenu.getElements());
			writer.endElement("ul");
        }
	}
    
    protected void encodeSubmenuIcon(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getParent() instanceof Baribbon ? Menu.SUBMENU_DOWN_ICON_CLASS : Menu.SUBMENU_RIGHT_ICON_CLASS;
        
        writer.startElement("span", null);
        writer.writeAttribute("class", icon, null);
        writer.endElement("span");
    }
}