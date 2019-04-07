package pl.com.bottega.ecommerce.sales.domain.invoicing;

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

    @Test public void testRequestInvoiceOnePositionShouldReturnOne() {
        Id id = new Id("1");
        ClientData client = new ClientData(id, "Piotrek");
        InvoiceRequest invoiceRequest = new InvoiceRequest(client);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.FOOD, new Money(10))).thenReturn(new Tax(new Money(10), "5%"));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(10));
        invoiceRequest.add(requestItem);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getItems().size(), is(1));
    }

    @Test public void testCalculateTaxShouldBeCalledTwoTimes() {
        Id id = new Id("2");
        ClientData client = new ClientData(id, "Piotr");
        InvoiceRequest invoiceRequest = new InvoiceRequest(client);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
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
}
