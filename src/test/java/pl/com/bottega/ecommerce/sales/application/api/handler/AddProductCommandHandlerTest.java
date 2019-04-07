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
import static org.mockito.Mockito.*;

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

    @Test public void testShouldReturnOneAsId() {

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

        assertThat(reservation.getClientData().getAggregateId(), Matchers.is(new Id("1")));
    }

    @Test public void testShouldReturnThatMethodAddWasCalledTwice() {

        AddProductCommand addProductCommand = new AddProductCommand(new Id("1"), new Id("2"), 5);
        AddProductCommand addProductCommand2 = new AddProductCommand(new Id("3"), new Id("4"), 5);

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
        addProductCommandHandler.handle(addProductCommand2);

        verify(reservation, times(2)).add(product, 5);
    }

    @Test public void testShouldReturnThatReservationRepositoryMethodsLoadAndSaveWereCalledOnce() {

        AddProductCommand addProductCommand = new AddProductCommand(new Id("1"), new Id("1"), 5);

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

        verify(reservationRepository, times(1)).load(new Id("1"));
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test public void testShouldReturnThatProductRepositoryMethodLoadWasCalledTwice() {

        AddProductCommand addProductCommand = new AddProductCommand(new Id("2"), new Id("2"), 10);

        Product product = new Product(new Id("2"), new Money(10), "Bread",
                ProductType.STANDARD);

        when(reservation.getClientData()).thenReturn(new ClientData(new Id("2"), "Piotr"));
        when(reservation.getCreateDate()).thenReturn(new Date());
        when(reservation.getStatus()).thenReturn(Reservation.ReservationStatus.OPENED);


        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        when(productRepository.load(any(Id.class))).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, times(2)).load(new Id("2"));
    }
}
