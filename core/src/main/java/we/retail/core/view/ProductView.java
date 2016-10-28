/*
 *   Copyright 2016 Adobe Systems Incorporated
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package we.retail.core.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.commons.json.JSONObject;

public class ProductView {

	private String path;
	private String pagePath;
	private String sku;
	private String title;
	private String description;
	private String price;
	private String summary;
	private String features;
	private String image;

	private List<ProductView> variants = new ArrayList<ProductView>();

	private List<String> variantAxes = new ArrayList<String>();
	private Map<String, String> variantAxesValues = new LinkedHashMap<String, String>();

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<ProductView> getVariants() {
		return variants;
	}

	public void setVariants(List<ProductView> variants) {
		this.variants = variants;
	}

	public void addVariant(ProductView variant) {
		this.variants.add(variant);
	}

	public List<String> getVariantAxes() {
		return variantAxes;
	}

	public void setVariantAxes(List<String> variantAxes) {
		this.variantAxes = variantAxes;
	}

	public void setVariantAxes(String[] variantAxes) {
		for (String axis : variantAxes) {
			this.variantAxes.add(axis.trim());
		}
	}

	public Map<String, String> getVariantAxesValues() {
		return variantAxesValues;
	}

	public String getVariantAxesValuesAsJson() {
		return new JSONObject(variantAxesValues).toString();
	}

	public String getVariantValueForAxis(String axis) {
		return variantAxesValues.get(axis);
	}

	public void setVariantAxesValues(Map<String, String> variantAxesValues) {
		this.variantAxesValues = variantAxesValues;
	}

	public void addVariantAxisValue(String axis, String value) {
		if (!variantAxesValues.containsKey(axis)) {
			variantAxesValues.put(axis, value);
		}
	}

	public Map<String, List<ProductView>> getVariantsByAxis(String axis) {
		if (variants.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, List<ProductView>> variantByAxes = new LinkedHashMap<String, List<ProductView>>();
		for (ProductView variant : variants) {
			String axisValue = variant.variantAxesValues.get(axis);
			if (axisValue != null) {
				List<ProductView> list = variantByAxes.get(axisValue);
				if (list == null) {
					list = new ArrayList<ProductView>();
					variantByAxes.put(axisValue, list);
				}
				list.add(variant);
			}
		}

		return variantByAxes;
	}

	public Map<String, Map<String, List<ProductView>>> getAllVariantsByAxes() {
		if (variantAxes.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, Map<String, List<ProductView>>> map = new LinkedHashMap<String, Map<String, List<ProductView>>>();
		for (String axis : variantAxes) {
			map.put(axis, getVariantsByAxis(axis));
		}

		return map;
	}
}
