/*
 * Copyright 2009-2013 PrimeTek.
 *
 * Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.primefaces.org/elite/license.xhtml
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.plus.component.export;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.Exporter;
import org.primefaces.util.Constants;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

public class PDFPlusExporter {

	public void export(FacesContext context, DataTable table, String filename,
			boolean pageOnly, boolean selectionOnly, String encodingType,
			MethodExpression preProcessor, MethodExpression postProcessor)
			throws IOException {
		try {

			table.setScrollable(false); // no use in pdf
			table.setPaginator(false);

			// Document document = new Document();
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// PdfWriter.getInstance(document, baos);
			//
			// if(preProcessor != null) {
			// preProcessor.invoke(context.getELContext(), new
			// Object[]{document});
			// }
			//
			// if(!document.isOpen()) {
			// document.open();
			// }

			ByteArrayOutputStream xhtml = exportPDF(context, table, encodingType);

			// FileUtils.write(new File("/tmp", "bla.xhtml"),xhtml);

			// if(postProcessor != null) {
			// postProcessor.invoke(context.getELContext(), new
			// Object[]{document});
			// }

			// document.close();

			writePDFToResponse(context.getExternalContext(), xhtml, filename);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	
	public void exportPage(FacesContext context, UIComponent component,
			String filename, String encodingType,
			MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
		
		ByteArrayOutputStream xhtml = exportPDF(context, component, encodingType);

		try {
			writePDFToResponse(context.getExternalContext(), xhtml, filename);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	

	protected ByteArrayOutputStream exportPDF(FacesContext context,
			UIComponent component,
			String encoding) {

		/**
		 * Render the Facelets template specified.
		 * 
		 * @param template
		 *            path to the Facelets template
		 * @return rendered content of the Facelets template
		 */

		try {
			// store the original response writer
			ResponseWriter originalWriter = context.getResponseWriter();

			// put in a StringWriter to capture the output
			StringWriter stringWriter = new StringWriter();
			ResponseWriter writer = createResponseWriter(context, stringWriter);
			context.setResponseWriter(writer);

			UIViewRoot view = context.getViewRoot();
			// renderComponent(context, view);

			// // create a UIViewRoot instance using the template specified
			ViewHandler viewHandler = context.getApplication().getViewHandler();
			// UIViewRoot view = viewHandler.createView(context, template);
			//
			// // the fun part -- do the actual rendering here
			// ViewDeclarationLanguage vdl = viewHandler
			// .getViewDeclarationLanguage(context, template);
			// vdl.buildView(context, view);
			renderChildren(context, view);

			// restore the response writer
			// context.setResponseWriter(originalWriter);

			String xhtml = stringWriter.toString().replace(
					"~com.sun.faces.saveStateFieldMarker~", "");

			ITextRenderer renderer = new ITextRenderer();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			renderer.setDocumentFromString(xhtml, "http://127.0.0.1:8080/");
			renderer.layout();

			renderer.createPDF(baos, true, 1);

			return baos;
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		} catch (DocumentException exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Create ResponseWriter. Taken from FaceletViewDeclarationLanguage.java of
	 * MyFaces.
	 */
	private ResponseWriter createResponseWriter(FacesContext context,
			Writer writer) {
		ExternalContext extContext = context.getExternalContext();
		Map<String, Object> requestMap = extContext.getRequestMap();
		String contentType = (String) requestMap.get("facelets.ContentType");
		String encoding = (String) requestMap.get("facelets.Encoding");
		RenderKit renderKit = context.getRenderKit();
		return renderKit.createResponseWriter(writer, contentType, encoding);
	}

	/**
	 * Render a UIComponent. Taken from JSF.java of Seam 2.2.
	 */
	private void renderChildren(FacesContext context, UIComponent component)
			throws IOException {
		List<UIComponent> children = component.getChildren();
		for (int i = 0, size = component.getChildCount(); i < size; i++) {
			UIComponent child = (UIComponent) children.get(i);
			renderChild(context, child);
		}
	}

	/**
	 * Render the child and all its children components.
	 */
	private void renderChild(FacesContext context, UIComponent child)
			throws IOException {
		if (child.isRendered()) {
			child.encodeAll(context);
		}
	}

	private void renderComponent(FacesContext context, UIComponent component)
			throws IOException {

		component.encodeAll(context);

	}

	protected void writePDFToResponse(ExternalContext externalContext,
			ByteArrayOutputStream baos, String fileName) throws IOException,
			DocumentException {
		externalContext.setResponseContentType("application/pdf");
		externalContext.setResponseHeader("Expires", "0");
		externalContext.setResponseHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");
		externalContext.setResponseHeader("Pragma", "public");
		externalContext.setResponseHeader("Content-disposition",
				"attachment;filename=" + fileName + ".pdf");
		externalContext.setResponseContentLength(baos.size());
		externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true",
				new HashMap<String, Object>());
		OutputStream out = externalContext.getResponseOutputStream();
		baos.writeTo(out);
		externalContext.responseFlushBuffer();
	}

}