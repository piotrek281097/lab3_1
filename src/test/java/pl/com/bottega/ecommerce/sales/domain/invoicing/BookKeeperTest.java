package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.*;

public class BookKeeperTest {

    private Id id;
    private ClientData client;
    private InvoiceRequest invoiceRequest;
    private BookKeeper bookKeeper;
    private TaxPolicy taxPolicy;

    @Before public void setup() {
        id = new Id("1");
        client = new ClientData(id, "Piotrek");
        invoiceRequest = new InvoiceRequest(client);
        bookKeeper = new BookKeeper(new InvoiceFactory());
        taxPolicy = mock(TaxPolicy.class);
    }

    @Test public void testRequestInvoiceOnePositionShouldReturnOne() {

        when(taxPolicy.calculateTax(ProductType.FOOD, new Money(10))).thenReturn(new Tax(new Money(10), "5%"));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(10));
        invoiceRequest.add(requestItem);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getItems().size(), is(1));
    }

    @Test public void testCalculateTaxShouldBeCalledTwoTimes() {

        when(taxPolicy.calculateTax(ProductType.STANDARD, new Money(10) ))
                .thenReturn(new Tax(new Money(10), "10%" ));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem requestItem = new RequestItem(productData, 10, new Money(10));
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(ProductType.STANDARD, new Money(10));
    }

    @Test public void testShouldReturnClientName() {

        when(taxPolicy.calculateTax(ProductType.FOOD, new Money(20) ))
                .thenReturn(new Tax(new Money(20), "10%" ));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(20));
        invoiceRequest.add(requestItem);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getClient().getName(), org.hamcrest.Matchers.is("Piotrek"));
    }

    @Test public void testShouldReturnDrugAsProductTypeOfSecondItem() {

        when(taxPolicy.calculateTax(ProductType.DRUG, new Money(10) ))
                .thenReturn(new Tax(new Money(10), "10%" ));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.DRUG);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(10));
        RequestItem requestItem2 = new RequestItem(productData, 5, new Money(10));
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem2);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getItems().get(1).getProduct().getType(), org.hamcrest.Matchers.is(ProductType.DRUG));
    }

    @Test public void testCalculateTaxBeCalledZeroTimes() {

        when(taxPolicy.calculateTax(ProductType.FOOD, new Money(10) ))
                .thenReturn(new Tax(new Money(10), "10%" ));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(0)).calculateTax(ProductType.FOOD, new Money(10));
    }

    @Test public void testProductDataGetTypeShouldBeCalledOnce() {

        when(taxPolicy.calculateTax(ProductType.FOOD, new Money(10) ))
                .thenReturn(new Tax(new Money(10), "10%" ));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(10));
        invoiceRequest.add(requestItem);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(productData, times(1)).getType();
    }
}
