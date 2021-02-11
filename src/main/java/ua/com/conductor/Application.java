package ua.com.conductor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.com.conductor.config.AppConfig;
import ua.com.conductor.exception.AuthenticationException;
import ua.com.conductor.model.CinemaHall;
import ua.com.conductor.model.Movie;
import ua.com.conductor.model.MovieSession;
import ua.com.conductor.model.User;
import ua.com.conductor.security.AuthenticationService;
import ua.com.conductor.service.CinemaHallService;
import ua.com.conductor.service.MovieService;
import ua.com.conductor.service.MovieSessionService;
import ua.com.conductor.service.OrderService;
import ua.com.conductor.service.ShoppingCartService;

public class Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        Movie movie = new Movie();
        movie.setTitle("Fast and Furious");
        MovieService movieService = context.getBean(MovieService.class);
        movieService.add(movie);
        Movie movieSimon = new Movie();
        movieSimon.setTitle("Love, Simon");
        movieService.add(movieSimon);
        movieService.getAll().forEach(System.out::println);

        CinemaHall cinemaHallOne = new CinemaHall();
        CinemaHall cinemaHallTwo = new CinemaHall();
        cinemaHallOne.setCapacity(80);
        cinemaHallTwo.setCapacity(100);
        CinemaHallService cinemaHallService = context.getBean(CinemaHallService.class);
        cinemaHallService.add(cinemaHallOne);
        cinemaHallService.add(cinemaHallTwo);
        System.out.println(cinemaHallService.getAll());

        MovieSession movieSessionOne = new MovieSession();
        movieSessionOne.setMovie(movie);
        movieSessionOne.setShowTime(LocalDateTime.now());
        MovieSession movieSessionTwo = new MovieSession();
        movieSessionTwo.setMovie(movie);
        movieSessionTwo.setCinemaHall(cinemaHallTwo);
        movieSessionTwo.setShowTime(LocalDateTime.now());
        MovieSession movieSessionThree = new MovieSession();
        movieSessionThree.setMovie(movieSimon);
        movieSessionThree.setCinemaHall(cinemaHallTwo);
        movieSessionThree.setShowTime(LocalDateTime.of(2021, 1, 20, 20, 0));
        MovieSessionService movieSessionService = context.getBean(MovieSessionService.class);
        movieSessionService.add(movieSessionOne);
        movieSessionService.add(movieSessionTwo);
        movieSessionService.add(movieSessionThree);
        System.out.println();
        movieSessionService.findAvailableSessions(movie.getId(), LocalDate.now())
                .forEach(System.out::println);

        AuthenticationService authenticationService = context.getBean(AuthenticationService.class);
        User user = authenticationService.register("example@gmail.com", "TryToDoDoIt");
        User userBob = authenticationService.register("Bob@gmail.com", "BoBoBoB");
        try {
            authenticationService.login("example@gmail.com", "TryToDoDoIt");
        } catch (AuthenticationException e) {
            throw new RuntimeException("Incorrect password or email!", e);
        }
        ShoppingCartService shoppingCartService = context.getBean(ShoppingCartService.class);
        shoppingCartService.addSession(movieSessionOne, user);
        shoppingCartService.addSession(movieSessionThree, user);
        System.out.println(shoppingCartService.getByUser(user));
        OrderService orderService = context.getBean(OrderService.class);
        System.out.println(orderService.completeOrder(shoppingCartService.getByUser(user)));
        orderService.getOrdersHistory(user).forEach(System.out::println);
    }
}
