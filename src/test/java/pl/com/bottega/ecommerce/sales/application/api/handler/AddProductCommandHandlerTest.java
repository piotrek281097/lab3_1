package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private SuggestionService suggestionService;
    private SystemContext systemContext;
    private Product product;
    private Product productEquivalent;
    private Reservation reservation;
    private Id id1, id2;
    private Client client;

    @Before public void setup() {
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        clientRepository = mock(ClientRepository.class);
        suggestionService = mock(SuggestionService.class);

        SystemContext systemContext = new SystemContext();


        AddProductCommandHandlerBuilder addProductCommandHandlerBuilder = new AddProductCommandHandlerBuilder();
        addProductCommandHandlerBuilder.withProductRepository(productRepository)
                                       .withReservationRepository(reservationRepository)
                                       .withClientRepository(clientRepository)
                                       .withSuggestionService(suggestionService)
                                       .withSystemContext(systemContext);

        addProductCommandHandler = addProductCommandHandlerBuilder.build();

        product = new Product(new Id("2"), new Money(10), "Bread",
                ProductType.FOOD);

        productEquivalent = new Product(new Id("3"), new Money(10), "Milk",
                ProductType.FOOD);

        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "Piotr"), new Date());

        id1 = new Id("1");
        id2 = new Id("2");
        client = new Client();
    }

    @Test public void testShouldReturnNull() {

        AddProductCommand addProductCommand = new AddProductCommand(id1, id2, 5);

        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        when(productRepository.load(any(Id.class))).thenReturn(product);

        assertThat(addProductCommandHandler.handle(addProductCommand), Matchers.equalTo(null));
    }

    @Test public void testShouldReturnThatMethodSuggestEquivalentCalledOnce() {

        product.markAsRemoved();
        AddProductCommand addProductCommand = new AddProductCommand(id1, id2, 5);

        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);
        when(clientRepository.load(any(Id.class))).thenReturn(client);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(productEquivalent);

        addProductCommandHandler.handle(addProductCommand);
        verify(suggestionService, times(1)).suggestEquivalent(product, client);
    }

    @Test public void testShouldReturnThatMethodSaveWasCalledTwice() {

        AddProductCommand addProductCommand = new AddProductCommand(id1, id2, 5);
        AddProductCommand addProductCommand2 = new AddProductCommand(id1, id2, 5);

        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        when(productRepository.load(any(Id.class))).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand2);

        verify(reservationRepository, times(2)).save(reservation);
    }

    @Test public void testShouldReturnThatReservationRepositoryMethodsLoadAndSaveWereCalledOnce() {

        AddProductCommand addProductCommand = new AddProductCommand(id1, id2, 5);

        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        when(productRepository.load(any(Id.class))).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, times(1)).load(id1);
        verify(reservationRepository, times(1)).save(reservation);
    }

}
