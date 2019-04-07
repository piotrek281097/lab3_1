package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler;
    private Reservation reservation;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;

    @Before public void setup() {
        addProductCommandHandler = new AddProductCommandHandler();
        reservation = mock(Reservation.class);
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
    }

    @Test public void testShouldReturnReservationStatusAsOpened() {

        AddProductCommand addProductCommand = new AddProductCommand(new Id("1"), new Id("2"), 5);

        Product product = new Product(new Id("2"), new Money(10), "Bread",
                ProductType.FOOD);

        when(reservation.getClientData()).thenReturn(new ClientData(new Id("1"), "Piotrek"));
        when(reservation.getCreateDate()).thenReturn(new Date());
        when(reservation.getStatus()).thenReturn(Reservation.ReservationStatus.OPENED);


        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        when(productRepository.load(any(Id.class))).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        assertThat(reservation.getStatus(), Matchers.is(Reservation.ReservationStatus.OPENED));
    }
}
