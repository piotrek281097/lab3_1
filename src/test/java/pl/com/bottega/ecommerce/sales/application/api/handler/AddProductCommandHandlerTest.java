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

import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;

    @Before public void setup() {
        addProductCommandHandler = new AddProductCommandHandler();
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
    }

    @Test public void testShouldReturnNull() {

        AddProductCommand addProductCommand = new AddProductCommand(new Id("1"), new Id("2"), 5);

        Product product = new Product(new Id("2"), new Money(10), "Bread",
                ProductType.FOOD);

        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "Piotr"), new Date());

        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        when(productRepository.load(any(Id.class))).thenReturn(product);

        assertThat(addProductCommandHandler.handle(addProductCommand), Matchers.equalTo(null));
    }

    @Test public void testShouldReturnThatMethodAddWasCalledTwice() {

        AddProductCommand addProductCommand = new AddProductCommand(new Id("1"), new Id("2"), 5);
        AddProductCommand addProductCommand2 = new AddProductCommand(new Id("3"), new Id("4"), 5);

        Product product = new Product(new Id("2"), new Money(10), "Bread",
                ProductType.FOOD);

        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "Piotr"), new Date());


        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        when(productRepository.load(any(Id.class))).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand2);

        verify(reservationRepository, times(2)).save(reservation);
    }

    @Test public void testShouldReturnThatReservationRepositoryMethodsLoadAndSaveWereCalledOnce() {

        AddProductCommand addProductCommand = new AddProductCommand(new Id("1"), new Id("1"), 5);

        Product product = new Product(new Id("2"), new Money(10), "Bread",
                ProductType.FOOD);

        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "Piotr"), new Date());


        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        when(productRepository.load(any(Id.class))).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, times(1)).load(new Id("1"));
        verify(reservationRepository, times(1)).save(reservation);
    }

}
