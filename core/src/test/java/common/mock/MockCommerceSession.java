/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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
 ******************************************************************************/
package common.mock;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.Predicate;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PaymentMethod;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.api.PlacedOrderResult;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.api.ShippingMethod;
import com.adobe.cq.commerce.api.promotion.PromotionInfo;
import com.adobe.cq.commerce.api.promotion.Voucher;
import com.adobe.cq.commerce.api.promotion.VoucherInfo;
import com.adobe.cq.commerce.api.smartlist.SmartListManager;

public class MockCommerceSession implements CommerceSession {
    @Override
    public void addCartEntry(Product product, int quantity) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserLocale(Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getUserLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getAvailableCountries() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ShippingMethod> getAvailableShippingMethods() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PaymentMethod> getAvailablePaymentMethods() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PriceInfo> getProductPriceInfo(Product product) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PriceInfo> getProductPriceInfo(Product product, Predicate filter) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProductPrice(Product product, Predicate filter) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProductPrice(Product product) throws CommerceException {
        return product.getProperty("price", String.class);
    }

    @Override
    public int getCartEntryCount() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CartEntry> getCartEntries() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PriceInfo> getCartPriceInfo(Predicate filter) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCartPrice(Predicate filter) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCartEntry(Product product, int quantity, Map<String, Object> properties) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyCartEntry(int entryNumber, int quantity) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyCartEntry(int entryNumber, Map<String, Object> delta) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteCartEntry(int entryNumber) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addVoucher(String code) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeVoucher(String code) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VoucherInfo> getVoucherInfos() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportsClientsidePromotionResolution() {
        return false;
    }

    @Override
    public void addPromotion(String path) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removePromotion(String path) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PromotionInfo> getPromotions() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOrderId() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getOrderDetails(String predicate) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getOrder() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateOrderDetails(Map<String, Object> details, String predicate) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateOrder(Map<String, Object> delta) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void placeOrder(Map<String, Object> orderDetailsDelta) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlacedOrderResult getPlacedOrders(String predicate, int pageNumber, int pageSize, String sortId) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlacedOrder getPlacedOrder(String orderId) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SmartListManager getSmartListManager() {
        return null;
    }

    @Override
    public String getPriceInfo(Product product) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCartPreTaxPrice() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCartTax() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCartTotalPrice() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOrderShipping() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOrderTotalTax() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOrderTotalPrice() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Voucher> getVouchers() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateOrderDetails(Map<String, String> delta) throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getOrderDetails() throws CommerceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void submitOrder(Map<String, String> orderDetailsDelta) throws CommerceException {
        throw new UnsupportedOperationException();
    }
}
